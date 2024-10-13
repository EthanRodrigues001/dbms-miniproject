package src.employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener {

    JTextField usernameField;
    JTextField passwordField;

    public Login() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(40, 20, 100, 30);
        add(usernameLabel);
        usernameField = new JTextField();
        usernameField.setBounds(150, 20, 150, 30);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(40, 70, 100, 30);
        add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 150, 30);
        add(passwordField);

        JButton login = new JButton("Login");
        login.setBounds(150, 140, 150, 30);
        login.setBackground(Color.BLACK);
        login.setForeground(Color.WHITE);
        login.addActionListener(this);
        add(login);

        // Load the image
        try {
            ImageIcon i1 = new ImageIcon("resources/icons/second.jpg");
            Image i2 = i1.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
            ImageIcon i3 = new ImageIcon(i2);
            JLabel image = new JLabel(i3);
            image.setBounds(350, 0, 200, 200);
            add(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSize(600, 300);
        setLocation(450, 200);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            Conn c = new Conn();
            String query = "SELECT * FROM login WHERE username = '"+username+"' AND password = '"+password+"'";
            ResultSet rs = c.getStatement().executeQuery(query);
            if (rs.next()) {
                setVisible(false);
                new Home();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password");
                setVisible(false);
                new Login();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
