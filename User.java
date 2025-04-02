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
        homeButton.addActionListener(e -> User.main(null));

        JButton searchBookButton = new JButton("Search Book");
        searchBookButton.setBounds(30, 120, 140, 40);
        sidebar.add(searchBookButton);
        searchBookButton.addActionListener(e -> searchBook());

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
        String dbPassword = "";

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
        reviewScrollPane.setBounds(20, 20, 650, 380); // Reduced height to make space for button
        reviewFrame.add(reviewScrollPane);
        
        // Add review button - styled like sidebar buttons
        JButton addReviewButton = new JButton("Add Review");
        addReviewButton.setBounds(280, 420, 140, 40); // Centered horizontally
        reviewFrame.add(addReviewButton);
        
        // Add action listener to the add review button
        addReviewButton.addActionListener(e -> addReview(reviewTable));

        // Database connection
        String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
        String dbUsername = "root";
        String dbPassword = "";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String reviewQuery = "SELECT books.title, books.author, reviews.rating, reviews.review_text " +
                    "FROM reviews " +
                    "JOIN books ON reviews.book_id = books.book_id";

            try (PreparedStatement reviewStmt = conn.prepareStatement(reviewQuery);
                    ResultSet reviewRs = reviewStmt.executeQuery()) {

                DefaultTableModel reviewModel = new DefaultTableModel(
                        new String[] { "Book Title", "Author", "Rating", "Review" }, 0);

                while (reviewRs.next()) {
                    reviewModel.addRow(new Object[] {
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
                reviewTable.getColumnModel().getColumn(2).setPreferredWidth(60); // Rating
                reviewTable.getColumnModel().getColumn(3).setPreferredWidth(300); // Review (Extended)

            }

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        reviewFrame.setVisible(true);
    }
    
    // Method to add a new review
    private static void addReview(JTable reviewTable) {
        // Create a new frame for adding review
        JFrame addReviewFrame = new JFrame("Add New Review");
        addReviewFrame.setSize(500, 450);
        addReviewFrame.setLocationRelativeTo(null);
        addReviewFrame.setLayout(null);
        
        // Create labels and input fields
        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setBounds(30, 30, 100, 25);
        addReviewFrame.add(titleLabel);
        
        JTextField titleField = new JTextField();
        titleField.setBounds(140, 30, 300, 25);
        addReviewFrame.add(titleField);
        
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(30, 70, 100, 25);
        addReviewFrame.add(authorLabel);
        
        JTextField authorField = new JTextField();
        authorField.setBounds(140, 70, 300, 25);
        addReviewFrame.add(authorField);
        
        JLabel ratingLabel = new JLabel("Rating (1-5):");
        ratingLabel.setBounds(30, 110, 100, 25);
        addReviewFrame.add(ratingLabel);
        
        // Create a combo box for rating selection
        String[] ratings = {"1", "2", "3", "4", "5"};
        JComboBox<String> ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setBounds(140, 110, 50, 25);
        addReviewFrame.add(ratingComboBox);
        
        JLabel reviewLabel = new JLabel("Review:");
        reviewLabel.setBounds(30, 150, 100, 25);
        addReviewFrame.add(reviewLabel);
        
        // Create a text area for the review with scroll pane
        JTextArea reviewTextArea = new JTextArea();
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        JScrollPane reviewScrollPane = new JScrollPane(reviewTextArea);
        reviewScrollPane.setBounds(140, 150, 300, 180);
        addReviewFrame.add(reviewScrollPane);
        
        // Submit button - centered like sidebar buttons
        JButton submitButton = new JButton("Submit Review");
        submitButton.setBounds(180, 350, 140, 40);
        addReviewFrame.add(submitButton);
        
        // Action listener for submit button
        submitButton.addActionListener(e -> {
            // Validate all fields are filled
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String rating = (String) ratingComboBox.getSelectedItem();
            String reviewText = reviewTextArea.getText().trim();
            
            if (title.isEmpty() || author.isEmpty() || reviewText.isEmpty()) {
                JOptionPane.showMessageDialog(addReviewFrame, 
                        "All fields are required. Please fill all the information.", 
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Database connection parameters
            String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
            String dbUsername = "root";
            String dbPassword = "";
            
            try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // First, verify the book exists with the given title and author
                String verifyBookQuery = "SELECT book_id FROM books WHERE title = ? AND author = ?";
                try (PreparedStatement verifyStmt = conn.prepareStatement(verifyBookQuery)) {
                    verifyStmt.setString(1, title);
                    verifyStmt.setString(2, author);
                    
                    try (ResultSet rs = verifyStmt.executeQuery()) {
                        if (!rs.next()) {
                            // Book not found or author doesn't match
                            // First check if the book exists with a different author
                            String checkTitleQuery = "SELECT author FROM books WHERE title = ?";
                            try (PreparedStatement checkTitleStmt = conn.prepareStatement(checkTitleQuery)) {
                                checkTitleStmt.setString(1, title);
                                
                                try (ResultSet titleRs = checkTitleStmt.executeQuery()) {
                                    if (titleRs.next()) {
                                        // Book exists but with different author
                                        String correctAuthor = titleRs.getString("author");
                                        JOptionPane.showMessageDialog(addReviewFrame, 
                                                "The book \"" + title + "\" exists but with author: " + correctAuthor, 
                                                "Author Mismatch", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        // Book doesn't exist at all
                                        JOptionPane.showMessageDialog(addReviewFrame, 
                                                "The book \"" + title + "\" does not exist in our database.", 
                                                "Book Not Found", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                            return;
                        }
                        
                        // Book exists with the correct author, get the book_id
                        int bookId = rs.getInt("book_id");
                        
                        // Get a sample user_id for the review (you might want to change this in a real app)
                        // For now, we'll use user_id = 1 as an example
                        int userId = 1; // This should ideally be the logged-in user's ID
                        
                        // Insert the review with the required user_id field
                        String insertReviewQuery = "INSERT INTO reviews (user_id, book_id, rating, review_text) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertReviewQuery)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setInt(2, bookId);
                            insertStmt.setInt(3, Integer.parseInt(rating));
                            insertStmt.setString(4, reviewText);
                            
                            int rowsAffected = insertStmt.executeUpdate();
                            
                            if (rowsAffected > 0) {
                                // Add the new review to the table
                                DefaultTableModel model = (DefaultTableModel) reviewTable.getModel();
                                model.addRow(new Object[]{title, author, Integer.parseInt(rating), reviewText});
                                
                                // Inform the user and close the add review frame
                                JOptionPane.showMessageDialog(addReviewFrame, 
                                        "Review added successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
                                
                                addReviewFrame.dispose();
                            }
                        }
                    }
                }
                
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addReviewFrame, 
                        "Database error: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        addReviewFrame.setVisible(true);
    }
    
    private static void searchBook() {
        // Create search input frame
        JFrame searchInputFrame = new JFrame("Search Book");
        searchInputFrame.setSize(400, 300);
        searchInputFrame.setLocationRelativeTo(null);
        searchInputFrame.setLayout(null);
        
        // Labels
        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setBounds(30, 30, 100, 25);
        searchInputFrame.add(titleLabel);
        
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(30, 80, 100, 25);
        searchInputFrame.add(authorLabel);
        
        JLabel bookIdLabel = new JLabel("Book ID:");
        bookIdLabel.setBounds(30, 130, 100, 25);
        searchInputFrame.add(bookIdLabel);
        
        // Text fields
        JTextField titleField = new JTextField();
        titleField.setBounds(130, 30, 200, 25);
        searchInputFrame.add(titleField);
        
        JTextField authorField = new JTextField();
        authorField.setBounds(130, 80, 200, 25);
        searchInputFrame.add(authorField);
        
        JTextField bookIdField = new JTextField();
        bookIdField.setBounds(130, 130, 200, 25);
        searchInputFrame.add(bookIdField);
        
        // Search button
        JButton searchButton = new JButton("Search");
        searchButton.setBounds(150, 190, 100, 30);
        searchInputFrame.add(searchButton);
        
        // Database connection parameters
        String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
        String dbUsername = "root";
        String dbPassword = "";
        
        // Auto-fill functionality for title field
        titleField.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (!title.isEmpty()) {
                try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    
                    String query = "SELECT book_id, author FROM books WHERE title = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, title);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                bookIdField.setText(String.valueOf(rs.getInt("book_id")));
                                authorField.setText(rs.getString("author"));
                            }
                        }
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(searchInputFrame, "Error querying database: " + ex.getMessage(), 
                                                 "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Auto-fill functionality for author field
        authorField.addActionListener(e -> {
            String author = authorField.getText().trim();
            if (!author.isEmpty()) {
                try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    
                    String query = "SELECT book_id, title FROM books WHERE author = ? LIMIT 1";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, author);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                bookIdField.setText(String.valueOf(rs.getInt("book_id")));
                                titleField.setText(rs.getString("title"));
                            }
                        }
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(searchInputFrame, "Error querying database: " + ex.getMessage(), 
                                                 "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Auto-fill functionality for book ID field
        bookIdField.addActionListener(e -> {
            String bookIdStr = bookIdField.getText().trim();
            if (!bookIdStr.isEmpty()) {
                try {
                    int bookId = Integer.parseInt(bookIdStr);
                    try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        
                        String query = "SELECT title, author FROM books WHERE book_id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(query)) {
                            stmt.setInt(1, bookId);
                            try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next()) {
                                    titleField.setText(rs.getString("title"));
                                    authorField.setText(rs.getString("author"));
                                }
                            }
                        }
                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(searchInputFrame, "Error querying database: " + ex.getMessage(),
                                                    "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(searchInputFrame, "Please enter a valid book ID",
                                                "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Search button action
        searchButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String bookIdStr = bookIdField.getText().trim();
            
            // Check if at least one field is filled
            if (title.isEmpty() && author.isEmpty() && bookIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(searchInputFrame, "Please fill at least one search field",
                                            "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Construct query based on provided fields
            try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                StringBuilder queryBuilder = new StringBuilder("SELECT * FROM books WHERE 1=1");
                
                if (!title.isEmpty()) {
                    queryBuilder.append(" AND title LIKE ?");
                }
                if (!author.isEmpty()) {
                    queryBuilder.append(" AND author LIKE ?");
                }
                if (!bookIdStr.isEmpty()) {
                    queryBuilder.append(" AND book_id = ?");
                }
                
                try (PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
                    int paramIndex = 1;
                    
                    if (!title.isEmpty()) {
                        stmt.setString(paramIndex++, "%" + title + "%");
                    }
                    if (!author.isEmpty()) {
                        stmt.setString(paramIndex++, "%" + author + "%");
                    }
                    if (!bookIdStr.isEmpty()) {
                        try {
                            int bookId = Integer.parseInt(bookIdStr);
                            stmt.setInt(paramIndex++, bookId);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(searchInputFrame, "Please enter a valid book ID",
                                                        "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        // Check if any results found
                        if (!rs.isBeforeFirst()) {
                            JOptionPane.showMessageDialog(searchInputFrame, "No books found matching your criteria",
                                                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        
                        // Book found, create result frame
                        JFrame resultFrame = new JFrame("Search Results");
                        resultFrame.setSize(700, 400);
                        resultFrame.setLocationRelativeTo(null);
                        resultFrame.setLayout(null);
                        
                        // Create table model for results
                        DefaultTableModel model = new DefaultTableModel(
                            new String[] {"Book ID", "Title", "Author", "Category", "Year", "Available Copies"}, 0);
                        
                        while (rs.next()) {
                            model.addRow(new Object[] {
                                rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getString("category"),
                                rs.getInt("publication_year"),
                                rs.getInt("available_copies")
                            });
                        }
                        
                        // Create and display result table
                        JTable resultTable = new JTable(model);
                        JScrollPane scrollPane = new JScrollPane(resultTable);
                        scrollPane.setBounds(20, 20, 650, 300);
                        resultFrame.add(scrollPane);
                        
                        // Adjust column widths
                        resultTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // Book ID
                        resultTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Title
                        resultTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
                        resultTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Category
                        resultTable.getColumnModel().getColumn(4).setPreferredWidth(50);  // Year
                        resultTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Copies
                        
                        // Close search input frame
                        searchInputFrame.dispose();
                        
                        // Display result frame
                        resultFrame.setVisible(true);
                    }
                }
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(searchInputFrame, "Error searching database: " + ex.getMessage(),
                                            "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        searchInputFrame.setVisible(true);
    }
}