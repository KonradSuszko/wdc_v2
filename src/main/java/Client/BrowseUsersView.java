package Client;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BrowseUsersView extends JFrame implements ActionListener {
    private HttpClient client;
    private String key;

    private JScrollPane scrollPane = new JScrollPane();
    private JButton deleteButton = new JButton("DELETE");
    private DefaultListModel dm = new DefaultListModel();
    private JList jList = new JList();

    private List<String> combinedStrings;

    public BrowseUsersView(String key) {
        this.key = key;
        this.client = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .proxy(ProxySelector.of(new InetSocketAddress(8080)))
                .build();

        this.combinedStrings = getUsers();
        setLocationAndSize();
        addAction();
        jList.setModel(dm);
        dm.addAll(combinedStrings);
        setLocationAndSize();
        scrollPane.setViewportView(jList);
        add(deleteButton);
        add(scrollPane);
    }

    private void setLocationAndSize(){
        this.setSize(300, 300);
        this.setFocusable(true);

        deleteButton.setBounds(350, 40, 100, 30);
    }

    private void addAction(){
        deleteButton.addActionListener(this);
    }

    private List<String> getUsers(){
        List<String> result = new ArrayList<>();
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/getUsers"))
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .GET()
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            byte[] allBytes = response.body();
            byte[] nBytes = new byte[4];
            nBytes[0] = allBytes[0];
            nBytes[1] = allBytes[1];
            nBytes[2] = allBytes[2];
            nBytes[3] = allBytes[3];
            Integer n = ByteBuffer.wrap(nBytes).getInt();
            Integer[] integers = new Integer[n];
            for(int i = 0; i < n; i++){
                byte[] tmp = new byte[4];
                tmp[0] = allBytes[(i+1)*4];
                tmp[1] = allBytes[(i+1)*4 + 1];
                tmp[2] = allBytes[(i+1)*4 + 2];
                tmp[3] = allBytes[(i+1)*4 + 3];
                integers[i] = ByteBuffer.wrap(tmp).getInt();
            }


            for(Integer i = 0; i < n; i++) {

                HttpRequest tmpReq = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/getSingleUser?i=" + integers[i].toString()))
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .GET()
                        .build();
                HttpResponse<String> tmpResponse = client.send(tmpReq, HttpResponse.BodyHandlers.ofString());
                JSONTokener tokener = new JSONTokener(tmpResponse.body());
                JSONObject json = new JSONObject(tokener);

                String id = json.getString("id");
                String username = json.getString("username");
                String highestRole = json.getString("highestRole");
                String highestPolicy = json.getString("highestPolicy");
                result.add(id + "\t" + username + "\t" + highestRole + "\t" + highestPolicy);
            }
        } catch (URISyntaxException ex){
            System.err.println(ex);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == deleteButton){
            Integer index = jList.getSelectedIndex();
            if(index == -1){
                return;
            }
            String tmp = (String) dm.get(index);
            String intString = tmp.split(" ")[0];
            Integer id = Integer.parseInt(intString);
            dm.remove(index);
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/deleteUser?id=" + id.toString()))
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .header("Token", key)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
            } catch (Exception ex){
                System.err.println(ex);
            }
        }
    }
}
