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

        JButton reviewsButton = new JButton("Reviews");
        reviewsButton.setBounds(30, 190, 140, 40);
        sidebar.add(reviewsButton);
        reviewsButton.addActionListener(e -> showReviews());

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
            String bookQuery = "SELECT title, author, category, publication_year, available_copies " +
                               "FROM books " +
                               "ORDER BY " +
                               "    CASE " +
                               "        WHEN title REGEXP '^[0-9]' THEN 1  " +
                               "        ELSE 0 " +
                               "    END," +
                               "    title ASC;";

            try (PreparedStatement bookStmt = conn.prepareStatement(bookQuery);
                 ResultSet bookRs = bookStmt.executeQuery()) {

                DefaultTableModel bookModel = new DefaultTableModel(
                        new String[]{"Title", "Author", "Category", "Year", "Copies"}, 0);
                int bookCount = 0;

                while (bookRs.next()) {
                    bookModel.addRow(new Object[]{
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

                // Adjust column widths
                bookTable.getColumnModel().getColumn(0).setPreferredWidth(180); // Title
                bookTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Author
                bookTable.getColumnModel().getColumn(2).setPreferredWidth(140); // Category
                bookTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Year (Smaller)
                bookTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Available Copies (Smaller)
            }

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Logout Button Action
        logoutButton.addActionListener(e -> {
            // Close the current user window
            frame.dispose();
            // Call the Main method to return to the login screen or initial app screen
            Main.main(null);
        });

        frame.setVisible(true);
    }

    // Method to display the reviews
    private static void showReviews() {
        JFrame reviewFrame = new JFrame("Book Reviews");
        reviewFrame.setSize(700, 500);
        reviewFrame.setLocationRelativeTo(null);
        reviewFrame.setLayout(null);

        JTable reviewTable = new JTable();
        JScrollPane reviewScrollPane = new JScrollPane(reviewTable);
        reviewScrollPane.setBounds(20, 20, 650, 400);
        reviewFrame.add(reviewScrollPane);

        // Database connection
        String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
        String dbUsername = "root";
        String dbPassword = "athuSQL@1407";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String reviewQuery = "SELECT books.title, books.author, reviews.rating, reviews.review_text " +
                                 "FROM reviews " +
                                 "JOIN books ON reviews.book_id = books.book_id";

            try (PreparedStatement reviewStmt = conn.prepareStatement(reviewQuery);
                 ResultSet reviewRs = reviewStmt.executeQuery()) {

                DefaultTableModel reviewModel = new DefaultTableModel(
                        new String[]{"Book Title", "Author", "Rating", "Review"}, 0);

                while (reviewRs.next()) {
                    reviewModel.addRow(new Object[]{
                            reviewRs.getString("title"),
                            reviewRs.getString("author"),
                            reviewRs.getInt("rating"),
                            reviewRs.getString("review_text")
                    });
                }

                reviewTable.setModel(reviewModel);

                // Adjust column widths
                reviewTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Book Title
                reviewTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Author
                reviewTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // Rating
                reviewTable.getColumnModel().getColumn(3).setPreferredWidth(300); // Review (Extended)

            }

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        reviewFrame.setVisible(true);
    }
}
