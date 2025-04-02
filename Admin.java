import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Admin {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        // Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setBounds(0, 0, 200, 600);
        sidebar.setBackground(Color.DARK_GRAY);
        sidebar.setLayout(null);
        frame.add(sidebar);

        // Sidebar Buttons
        JButton homeButton = new JButton("Home");
        homeButton.setBounds(30, 50, 140, 40);
        sidebar.add(homeButton);

        JButton manageBooksButton = new JButton("Manage Books");
        manageBooksButton.setBounds(30, 120, 140, 40);
        sidebar.add(manageBooksButton);

        JButton manageStudentsButton = new JButton("Manage Students");
        manageStudentsButton.setBounds(30, 190, 140, 40);
        sidebar.add(manageStudentsButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(30, 500, 140, 40);
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        sidebar.add(logoutButton);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(200, 20, 680, 50);
        headerPanel.setLayout(null);
        frame.add(headerPanel);

        // Books Label (Smaller Size & Adjusted Width)
        JLabel booksLabel = new JLabel("Books: 0", SwingConstants.CENTER);
        booksLabel.setFont(new Font("Arial", Font.BOLD, 14));
        booksLabel.setOpaque(true);
        booksLabel.setBackground(Color.LIGHT_GRAY);
        booksLabel.setBounds(180, 10, 100, 30); // Width adjusted to 100px
        headerPanel.add(booksLabel);

        // Users Label (Smaller Size & Adjusted Width)
        JLabel usersLabel = new JLabel("Users: 0", SwingConstants.CENTER);
        usersLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usersLabel.setOpaque(true);
        usersLabel.setBackground(Color.LIGHT_GRAY);
        usersLabel.setBounds(400, 10, 100, 30); // Width adjusted to 100px
        headerPanel.add(usersLabel);

        // Books Table
        JTable bookTable = new JTable();
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookScrollPane.setBounds(220, 90, 640, 200);
        frame.add(bookScrollPane);

        // Users Table
        JTable userTable = new JTable();
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userScrollPane.setBounds(220, 310, 640, 200);
        frame.add(userScrollPane);

        // Database connection
        String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
        String dbUsername = "root";
        String dbPassword = "athuSQL@1407";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Load books data
            String bookQuery = "SELECT book_id, title, author, category, publication_year, available_copies FROM books";
            try (PreparedStatement bookStmt = conn.prepareStatement(bookQuery);
                 ResultSet bookRs = bookStmt.executeQuery()) {

                DefaultTableModel bookModel = new DefaultTableModel(
                        new String[]{"Book ID", "Title", "Author", "Category", "Year", "Available Copies"}, 0);
                int bookCount = 0;

                while (bookRs.next()) {
                    bookModel.addRow(new Object[]{
                            bookRs.getInt("book_id"),
                            bookRs.getString("title"),
                            bookRs.getString("author"),
                            bookRs.getString("category"),
                            bookRs.getInt("publication_year"),
                            bookRs.getInt("available_copies")
                    });
                    bookCount++;
                }

                bookTable.setModel(bookModel);
                booksLabel.setText("Books: " + bookCount);
            }

            // Load users data
            String userQuery = "SELECT user_id, name, email, role FROM users";
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery);
                 ResultSet userRs = userStmt.executeQuery()) {

                DefaultTableModel userModel = new DefaultTableModel(
                        new String[]{"User ID", "Name", "Email", "Role"}, 0);
                int userCount = 0;

                while (userRs.next()) {
                    userModel.addRow(new Object[]{
                            userRs.getInt("user_id"),
                            userRs.getString("name"),
                            userRs.getString("email"),
                            userRs.getString("role")
                    });
                    userCount++;
                }

                userTable.setModel(userModel);
                usersLabel.setText("Users: " + userCount);
            }

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        frame.setVisible(true);
    }
}
