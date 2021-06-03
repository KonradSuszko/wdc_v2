package Client;

import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
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
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LoginView extends JFrame implements ActionListener {
    Container container = getContentPane();
    JLabel userLabel = new JLabel("USERNAME");
    JLabel passwordLabel = new JLabel("PASSWORD");
    JTextField userField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("LOGIN");

    public LoginView() throws HeadlessException{
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addAction();
    }

    public void setLayoutManager() {
        container.setLayout(null);
    }

    public void setLocationAndSize() {
        userLabel.setBounds(50, 50, 100, 30);
        passwordLabel.setBounds(50, 100, 100, 30);
        userField.setBounds(150, 50, 150, 30);
        passwordField.setBounds(150, 100, 150, 30);
        loginButton.setBounds(200, 150, 100, 30);
    }

    private void addComponentsToContainer() {
        container.add(userLabel);
        container.add(passwordLabel);
        container.add(userField);
        container.add(passwordField);
        container.add(loginButton);
    }

    private void addAction() {
        loginButton.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == loginButton){
            String tmp = proceedLogging();
            if(tmp == null)
                return;
            if(tmp.equals("user not found")){
                JOptionPane.showMessageDialog(this, "User doesn't exist");
            }
            else if(tmp.equals("bad password")){
                JOptionPane.showMessageDialog(this, "Bad password");
            }
            else{
                JOptionPane.showMessageDialog(this,"Logged");
                initGallery(tmp);
            }
            userField.setText("");
            passwordField.setText("");
        }
    }

    private String proceedLogging(){
        String userName = userField.getText();
        String password = passwordField.getText();

        JSONObject loginData = null;
        String key = null;
        try{
            loginData = new JSONObject()
                    .put("username", userName)
                    .put("password", Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8)));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/login"))
                    .headers("Content-Type", "application/json;charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(loginData.toString()))
                    .build();
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .proxy(ProxySelector.of(new InetSocketAddress(8080)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            return response.body();
        } catch (JSONException | URISyntaxException ex){
            System.err.println(ex);
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initGallery(String key){
        GalleryView galleryView = new GalleryView(key);

        galleryView.setSize(700, 750);
        galleryView.setVisible(true);
        galleryView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        galleryView.setLocationRelativeTo(null);
    }
}
