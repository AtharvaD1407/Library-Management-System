import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Main {
    private static String userRole = ""; // Stores whether "admin" or "user"

    public static void main(String[] args) {
        showWelcomePage();
    }

    private static void showWelcomePage() {
        JFrame frame = new JFrame("Welcome Page");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        JLabel label = new JLabel("Select Role:");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBounds(150, 30, 200, 30);
        panel.add(label);

        JButton adminButton = new JButton("Admin");
        adminButton.setBounds(100, 80, 200, 40);
        panel.add(adminButton);

        JButton userButton = new JButton("User");
        userButton.setBounds(100, 140, 200, 40);
        panel.add(userButton);

        adminButton.addActionListener(e -> {
            userRole = "admin";
            frame.dispose();
            showAuthPage();
        });

        userButton.addActionListener(e -> {
            userRole = "user";
            frame.dispose();
            showAuthPage();
        });
        frame.setVisible(true);
    }

    private static void showAuthPage() {
        JFrame frame = new JFrame("Authentication - " + userRole.toUpperCase());
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        JLabel label = new JLabel("Login or Sign Up?");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBounds(130, 30, 200, 30);
        panel.add(label);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 80, 200, 40);
        panel.add(loginButton);

        JButton signupButton = new JButton("Sign Up");
        signupButton.setBounds(100, 140, 200, 40);
        panel.add(signupButton);

        loginButton.addActionListener(e -> {
            frame.dispose();
            showLoginPage();
        });

        signupButton.addActionListener(e -> {
            frame.dispose();
            showSignupPage();
        });

        frame.setVisible(true);
    }

    private static void showLoginPage() {
        JFrame frame = new JFrame("Login - " + userRole.toUpperCase());
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 50, 100, 30);
        panel.add(usernameLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(150, 50, 200, 30);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 100, 30);
        panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBounds(150, 100, 200, 30);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(50, 160, 300, 40);
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = String.valueOf(passwordField.getPassword());

            if (validateLogin(username, password)) {
                JOptionPane.showMessageDialog(frame, "Login successful!");
                frame.dispose();

                if (userRole == "admin") {
                    Admin.main(null);
                } else {
                    User.main(null);
                }

            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private static void showSignupPage() {
        JFrame frame = new JFrame("Sign Up - " + userRole.toUpperCase());
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 30, 100, 30);
        panel.add(emailLabel);

        JTextField emailText = new JTextField(20);
        emailText.setBounds(150, 30, 200, 30);
        panel.add(emailText);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 80, 100, 30);
        panel.add(usernameLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(150, 80, 200, 30);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 130, 100, 30);
        panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBounds(150, 130, 200, 30);
        panel.add(passwordField);

        JButton signupButton = new JButton("Sign Up");
        signupButton.setBounds(50, 190, 300, 40);
        panel.add(signupButton);

        signupButton.addActionListener(e -> {
            String email = emailText.getText();
            String username = userText.getText();
            String password = String.valueOf(passwordField.getPassword());

            if (registerUser(email, username, password)) {
                JOptionPane.showMessageDialog(frame, "Sign Up Successful!");
                frame.dispose();

                if (userRole == "user") {
                    User.main(null);
                } else {
                    Admin.main(null);
                }

            } else {
                JOptionPane.showMessageDialog(frame, "Error in signing up.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private static boolean validateLogin(String username, String password) {
        String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
        String dbUsername = "root";
        String dbPassword = "athuSQL@1407";

        String query = "SELECT * FROM users WHERE name = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement pst = conn.prepareStatement(query)) {

            Class.forName("com.mysql.cj.jdbc.Driver");

            pst.setString(1, username);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    private static boolean registerUser(String email, String username, String password) {
        String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
        String dbUsername = "root";
        String dbPassword = "athuSQL@1407";

        String query = "INSERT INTO users (email, name, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement pst = conn.prepareStatement(query)) {

            Class.forName("com.mysql.cj.jdbc.Driver");

            pst.setString(1, email);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.setString(4, userRole);

            return pst.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }
}
