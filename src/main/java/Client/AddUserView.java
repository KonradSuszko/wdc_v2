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
import java.util.Optional;

public class AddUserView extends JFrame implements ActionListener {
    private Container container = new Container();
    private JLabel userLabel = new JLabel("USERNAME");
    private JLabel passwordLabel = new JLabel("PASSWORD");
    private JTextField userField = new JTextField();
    private JTextField passwordField = new JTextField();
    private JButton addButton = new JButton("ADD");
    private JCheckBox roleUserCheckbox = new JCheckBox("User");
    private JCheckBox roleStuffCheckbox = new JCheckBox("Stuff");
    private JCheckBox roleAdminCheckbox = new JCheckBox("Admin");
    private JCheckBox policy1Checkbox = new JCheckBox("Policy 1");
    private JCheckBox policy2Checkbox = new JCheckBox("Policy 2");
    private JCheckBox policy3Checkbox = new JCheckBox("Policy 3");
    private JCheckBox policy4Checkbox = new JCheckBox("Policy 4");
    private JCheckBox policy5Checkbox = new JCheckBox("Policy 5");

    private HttpClient client;
    private String key;
    private GalleryView parent;

    public AddUserView(String key, GalleryView parent) throws HeadlessException {
        this.key = key;
        this.parent = parent;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addButton.addActionListener(this);
        add(container);
        setVisible(true);

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .proxy(ProxySelector.of(new InetSocketAddress(8080)))
                .build();
    }

    private void setLayoutManager(){
        container.setLayout(null);
    }

    private void setLocationAndSize(){
        this.setSize(600, 250);
        this.setFocusable(true);

        userLabel.setBounds(50, 50, 100, 30);
        passwordLabel.setBounds(50, 100, 100, 30);
        userField.setBounds(150, 50, 150, 30);
        passwordField.setBounds(150, 100, 150, 30);
        addButton.setBounds(200, 150, 100, 30);

        roleUserCheckbox.setBounds(320, 50, 100, 10);
        roleStuffCheckbox.setBounds(320, 65, 100, 10);
        roleAdminCheckbox.setBounds(320, 80, 100, 10);

        policy1Checkbox.setBounds(450, 50, 100, 10);
        policy2Checkbox.setBounds(450, 65, 100, 10);
        policy3Checkbox.setBounds(450, 80, 100, 10);
        policy4Checkbox.setBounds(450, 95, 100, 10);
        policy5Checkbox.setBounds(450, 110, 100, 10);
    }

    private void addComponentsToContainer(){
        container.add(userLabel);
        container.add(passwordLabel);
        container.add(userField);
        container.add(passwordField);
        container.add(addButton);
        container.add(roleUserCheckbox);
        container.add(roleStuffCheckbox);
        container.add(roleAdminCheckbox);
        container.add(policy1Checkbox);
        container.add(policy2Checkbox);
        container.add(policy3Checkbox);
        container.add(policy4Checkbox);
        container.add(policy5Checkbox);
    }

    private void proceedAdding(){
        String username = userField.getText();
        String hashedPassword = Base64.getEncoder().encodeToString(passwordField.getText().getBytes(StandardCharsets.UTF_8));

        try{
            JSONObject json = prepareJSON(username, hashedPassword);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/addUser"))
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("Authorization", "Bearer " + key)
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 403) {
                JOptionPane.showMessageDialog(this, "Forbidden access");
                return;
            }
            else if(response.statusCode() == 200) {
                String answer = response.body();
                Optional<String> newToken = response.headers().firstValue("Token");
                newToken.ifPresent(this::updateToken);
                if (answer.equals("ok")) {
                    JOptionPane.showMessageDialog(this, "User added");
                } else if (answer.equals("user already exist")) {
                    JOptionPane.showMessageDialog(this, "Username already exist");
                } else {
                    JOptionPane.showMessageDialog(this, "Other error");
                }
            }
            else{
                JOptionPane.showMessageDialog(this, "Unknown error");
                return;
            }
        }catch (JSONException ex){
            System.err.println(ex);
            return;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }

    private JSONObject prepareJSON(String username, String hashedPassword) throws JSONException {
        return new JSONObject()
                .put("username", username)
                .put("password", hashedPassword)
                .put("userRole", roleUserCheckbox.isSelected())
                .put("stuffRole", roleStuffCheckbox.isSelected())
                .put("adminRole", roleAdminCheckbox.isSelected())
                .put("policy1", policy1Checkbox.isSelected())
                .put("policy2", policy2Checkbox.isSelected())
                .put("policy3", policy3Checkbox.isSelected())
                .put("policy4", policy4Checkbox.isSelected())
                .put("policy5", policy5Checkbox.isSelected());
    }

    private void updateToken(String token){
        this.key = token;
        parent.updateToken(token);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton){
            proceedAdding();
        }
    }
}
