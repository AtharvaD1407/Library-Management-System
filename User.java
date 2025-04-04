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

        JPanel sidebar = new JPanel();
        sidebar.setBounds(0, 0, 200, 600);
        sidebar.setBackground(Color.DARK_GRAY);
        sidebar.setLayout(null);
        frame.add(sidebar);

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

        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(200, 20, 680, 50);
        headerPanel.setLayout(null);
        frame.add(headerPanel);

        JLabel booksLabel = new JLabel("Books: 0", SwingConstants.CENTER);
        booksLabel.setFont(new Font("Arial", Font.BOLD, 14));
        booksLabel.setOpaque(true);
        booksLabel.setBackground(Color.LIGHT_GRAY);
        booksLabel.setBounds(290, 10, 100, 30); 
        headerPanel.add(booksLabel);

        JTable bookTable = new JTable();
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookScrollPane.setBounds(220, 90, 640, 400);
        frame.add(bookScrollPane);

        String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
        String dbUsername = "root";
        String dbPassword = "";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

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

        logoutButton.addActionListener(e -> {
            frame.dispose();
            Main.main(null);
        });

        frame.setVisible(true);
    }

    private static void showReviews() {
        JFrame reviewFrame = new JFrame("Book Reviews");
        reviewFrame.setSize(700, 500);
        reviewFrame.setLocationRelativeTo(null);
        reviewFrame.setLayout(null);

        JTable reviewTable = new JTable();
        JScrollPane reviewScrollPane = new JScrollPane(reviewTable);
        reviewScrollPane.setBounds(20, 20, 650, 380); 
        reviewFrame.add(reviewScrollPane);
        
        JButton addReviewButton = new JButton("Add Review");
        addReviewButton.setBounds(280, 420, 140, 40); 
        reviewFrame.add(addReviewButton);
        
        addReviewButton.addActionListener(e -> addReview(reviewTable));

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

                reviewTable.getColumnModel().getColumn(0).setPreferredWidth(150); 
                reviewTable.getColumnModel().getColumn(1).setPreferredWidth(120); 
                reviewTable.getColumnModel().getColumn(2).setPreferredWidth(60); 
                reviewTable.getColumnModel().getColumn(3).setPreferredWidth(300);

            }

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        reviewFrame.setVisible(true);
    }
    
    private static void addReview(JTable reviewTable) {
        JFrame addReviewFrame = new JFrame("Add New Review");
        addReviewFrame.setSize(500, 450);
        addReviewFrame.setLocationRelativeTo(null);
        addReviewFrame.setLayout(null);
        
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
        
        String[] ratings = {"1", "2", "3", "4", "5"};
        JComboBox<String> ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setBounds(140, 110, 50, 25);
        addReviewFrame.add(ratingComboBox);
        
        JLabel reviewLabel = new JLabel("Review:");
        reviewLabel.setBounds(30, 150, 100, 25);
        addReviewFrame.add(reviewLabel);
        
        JTextArea reviewTextArea = new JTextArea();
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        JScrollPane reviewScrollPane = new JScrollPane(reviewTextArea);
        reviewScrollPane.setBounds(140, 150, 300, 180);
        addReviewFrame.add(reviewScrollPane);
        
        JButton submitButton = new JButton("Submit Review");
        submitButton.setBounds(180, 350, 140, 40);
        addReviewFrame.add(submitButton);
        
        submitButton.addActionListener(e -> {
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
            
            String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
            String dbUsername = "root";
            String dbPassword = "";
            
            try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                String verifyBookQuery = "SELECT book_id FROM books WHERE title = ? AND author = ?";
                try (PreparedStatement verifyStmt = conn.prepareStatement(verifyBookQuery)) {
                    verifyStmt.setString(1, title);
                    verifyStmt.setString(2, author);
                    
                    try (ResultSet rs = verifyStmt.executeQuery()) {
                        if (!rs.next()) {
                            String checkTitleQuery = "SELECT author FROM books WHERE title = ?";
                            try (PreparedStatement checkTitleStmt = conn.prepareStatement(checkTitleQuery)) {
                                checkTitleStmt.setString(1, title);
                                
                                try (ResultSet titleRs = checkTitleStmt.executeQuery()) {
                                    if (titleRs.next()) {
                                        String correctAuthor = titleRs.getString("author");
                                        JOptionPane.showMessageDialog(addReviewFrame, 
                                                "The book \"" + title + "\" exists but with author: " + correctAuthor, 
                                                "Author Mismatch", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(addReviewFrame, 
                                                "The book \"" + title + "\" does not exist in our database.", 
                                                "Book Not Found", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                            return;
                        }
                        
                        int bookId = rs.getInt("book_id");
                        
                        int userId = 1; 
                        
                        String insertReviewQuery = "INSERT INTO reviews (user_id, book_id, rating, review_text) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertReviewQuery)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setInt(2, bookId);
                            insertStmt.setInt(3, Integer.parseInt(rating));
                            insertStmt.setString(4, reviewText);
                            
                            int rowsAffected = insertStmt.executeUpdate();
                            
                            if (rowsAffected > 0) {
                                DefaultTableModel model = (DefaultTableModel) reviewTable.getModel();
                                model.addRow(new Object[]{title, author, Integer.parseInt(rating), reviewText});
                                
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
        JFrame searchInputFrame = new JFrame("Search Book");
        searchInputFrame.setSize(400, 300);
        searchInputFrame.setLocationRelativeTo(null);
        searchInputFrame.setLayout(null);
        
        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setBounds(30, 30, 100, 25);
        searchInputFrame.add(titleLabel);
        
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(30, 80, 100, 25);
        searchInputFrame.add(authorLabel);
        
        JLabel bookIdLabel = new JLabel("Book ID:");
        bookIdLabel.setBounds(30, 130, 100, 25);
        searchInputFrame.add(bookIdLabel);
        
        JTextField titleField = new JTextField();
        titleField.setBounds(130, 30, 200, 25);
        searchInputFrame.add(titleField);
        
        JTextField authorField = new JTextField();
        authorField.setBounds(130, 80, 200, 25);
        searchInputFrame.add(authorField);
        
        JTextField bookIdField = new JTextField();
        bookIdField.setBounds(130, 130, 200, 25);
        searchInputFrame.add(bookIdField);
        
        JButton searchButton = new JButton("Search");
        searchButton.setBounds(150, 190, 100, 30);
        searchInputFrame.add(searchButton);
        
        String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
        String dbUsername = "root";
        String dbPassword = "";
        
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
        
        searchButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String bookIdStr = bookIdField.getText().trim();
            
            if (title.isEmpty() && author.isEmpty() && bookIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(searchInputFrame, "Please fill at least one search field",
                                            "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
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
                        if (!rs.isBeforeFirst()) {
                            JOptionPane.showMessageDialog(searchInputFrame, "No books found matching your criteria",
                                                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        
                        JFrame resultFrame = new JFrame("Search Results");
                        resultFrame.setSize(700, 400);
                        resultFrame.setLocationRelativeTo(null);
                        resultFrame.setLayout(null);
                        
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
                        
                        JTable resultTable = new JTable(model);
                        JScrollPane scrollPane = new JScrollPane(resultTable);
                        scrollPane.setBounds(20, 20, 650, 300);
                        resultFrame.add(scrollPane);
                        
                        resultTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // Book ID
                        resultTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Title
                        resultTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
                        resultTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Category
                        resultTable.getColumnModel().getColumn(4).setPreferredWidth(50);  // Year
                        resultTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Copies
                        
                        searchInputFrame.dispose();
                        
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