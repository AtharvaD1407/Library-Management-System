import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class User {
    public static void main(String[] args) {
        JFrame frame = new JFrame("User Dashboard");
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

        JButton searchBookButton = new JButton("Search Book");
        searchBookButton.setBounds(30, 120, 140, 40);
        sidebar.add(searchBookButton);

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

        // Books Label (Displays Total Books Count)
        JLabel booksLabel = new JLabel("Books: 0", SwingConstants.CENTER);
        booksLabel.setFont(new Font("Arial", Font.BOLD, 14));
        booksLabel.setOpaque(true);
        booksLabel.setBackground(Color.LIGHT_GRAY);
        booksLabel.setBounds(290, 10, 100, 30); // Centered
        headerPanel.add(booksLabel);

        // Books Table
        JTable bookTable = new JTable();
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookScrollPane.setBounds(220, 90, 640, 400);
        frame.add(bookScrollPane);

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

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        frame.setVisible(true);
    }
}
