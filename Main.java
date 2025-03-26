import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main {
    public static void main(String[] args) {

        // Create the frame for the login page
        JFrame frame = new JFrame("Login Page");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the window

        // Create a panel
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        // Username label
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(10, 20, 80, 25);
        panel.add(usernameLabel);

        // Username text field
        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        // Password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        // Password text field
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 255, 25);
        panel.add(loginButton);

        // Add action listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = String.valueOf(passwordField.getPassword());

                // Validate login
                if (validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Login successful!");
                    frame.dispose();  // Close login window

                    // Open new menu window (Make sure Menu class exists)
                    Menu.main(null);
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Set frame visibility
        frame.setVisible(true);
    }

    private static boolean validateLogin(String username, String password) {
        // MySQL connection details
        String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC"; // Add timezone fix
        String dbUsername = "root"; 
        String dbPassword = "";

        String query = "SELECT * FROM login WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement pst = conn.prepareStatement(query)) {

            // Load MySQL driver (optional but useful for debugging)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Set query parameters
            pst.setString(1, username);
            pst.setString(2, password);

            // Execute query
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next(); // Return true if user is found
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false; // Return false if login fails
    }
}
