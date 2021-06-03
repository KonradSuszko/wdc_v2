package Client;


import javax.swing.*;

public class ClientApp{
    public static void main(String[] args) {
        LoginView login = new LoginView();
        login.setSize(350, 250);
        login.setVisible(true);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setLocationRelativeTo(null);
    }
}
