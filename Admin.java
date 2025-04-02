import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Admin {
    private static JTable mainBookTable;
    private static JTable mainUserTable;
    private static JLabel booksLabel;
    private static JLabel usersLabel;
    private static String url = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC";
    private static String dbUsername = "root";
    private static String dbPassword = "";

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
        homeButton.addActionListener(e -> Admin.main(null));

        JButton manageBooksButton = new JButton("Manage Books");
        manageBooksButton.setBounds(30, 120, 140, 40);
        sidebar.add(manageBooksButton);

        JButton manageUsersButton = new JButton("Manage Users");
        manageUsersButton.setBounds(30, 190, 140, 40);
        sidebar.add(manageUsersButton);

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
        booksLabel = new JLabel("Books: 0", SwingConstants.CENTER);
        booksLabel.setFont(new Font("Arial", Font.BOLD, 14));
        booksLabel.setOpaque(true);
        booksLabel.setBackground(Color.LIGHT_GRAY);
        booksLabel.setBounds(180, 10, 100, 30);
        headerPanel.add(booksLabel);

        // Users Label (Smaller Size & Adjusted Width)
        usersLabel = new JLabel("Users: 0", SwingConstants.CENTER);
        usersLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usersLabel.setOpaque(true);
        usersLabel.setBackground(Color.LIGHT_GRAY);
        usersLabel.setBounds(400, 10, 100, 30);
        headerPanel.add(usersLabel);

        // Books Table
        mainBookTable = new JTable();
        JScrollPane bookScrollPane = new JScrollPane(mainBookTable);
        bookScrollPane.setBounds(220, 90, 640, 200);
        frame.add(bookScrollPane);

        // Users Table
        mainUserTable = new JTable();
        JScrollPane userScrollPane = new JScrollPane(mainUserTable);
        userScrollPane.setBounds(220, 310, 640, 200);
        frame.add(userScrollPane);

        // Load initial data
        refreshMainData();

        // Add action to Manage Students Button
        manageUsersButton.addActionListener(e -> {
            openManageUsersFrame();
        });

        // Add action to Manage Books Button
        manageBooksButton.addActionListener(e -> {
            openManageBooksFrame();
        });

        logoutButton.addActionListener(e -> {
            frame.dispose();
            Main.main(null);
        });

        frame.setVisible(true);
    }

    // Method to refresh main screen data
    private static void refreshMainData() {
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

                mainBookTable.setModel(bookModel);
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

                mainUserTable.setModel(userModel);
                usersLabel.setText("Users: " + userCount);
            }

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to open Manage Users Frame
    private static void openManageUsersFrame() {
        JFrame usersFrame = new JFrame("Manage Users");
        usersFrame.setSize(800, 500);
        usersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        usersFrame.setLocationRelativeTo(null);
        usersFrame.setLayout(null);

        // Users Table
        JTable userTable = new JTable();
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userScrollPane.setBounds(20, 20, 750, 350);
        usersFrame.add(userScrollPane);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBounds(20, 380, 750, 60);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        usersFrame.add(buttonsPanel);

        // Add User Button
        JButton addUserButton = new JButton("Add User");
        buttonsPanel.add(addUserButton);

        // Remove User Button
        JButton removeUserButton = new JButton("Remove User");
        buttonsPanel.add(removeUserButton);

        // Update User Button
        JButton updateUserButton = new JButton("Update User");
        buttonsPanel.add(updateUserButton);

        // Load users data into table
        refreshUserTable(userTable);

        // Add User Button Action
        addUserButton.addActionListener(e -> {
            JFrame addFrame = new JFrame("Add User");
            addFrame.setSize(350, 250);
            addFrame.setLocationRelativeTo(null);
            addFrame.setLayout(null);

            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setBounds(30, 30, 100, 25);
            addFrame.add(nameLabel);
            
            JTextField nameField = new JTextField();
            nameField.setBounds(140, 30, 180, 25);
            addFrame.add(nameField);

            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setBounds(30, 70, 100, 25);
            addFrame.add(emailLabel);
            
            JTextField emailField = new JTextField();
            emailField.setBounds(140, 70, 180, 25);
            addFrame.add(emailField);

            JButton saveButton = new JButton("Save");
            saveButton.setBounds(70, 130, 100, 30);
            addFrame.add(saveButton);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBounds(180, 130, 100, 30);
            addFrame.add(cancelButton);

            saveButton.addActionListener(saveEvent -> {
                String name = nameField.getText();
                String email = emailField.getText();
                
                try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                    // Here would be the actual code to insert the user into database
                    String insertQuery = "INSERT INTO users (name, email, role) VALUES (?, ?, 'user')";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                        pstmt.setString(1, name);
                        pstmt.setString(2, email);
                        pstmt.executeUpdate();
                    }
                    
                    JOptionPane.showMessageDialog(addFrame, "User added: " + name);
                    
                    // Close add frame
                    addFrame.dispose();
                    
                    // Refresh data in both frames
                    refreshUserTable(userTable);
                    refreshMainData();
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(cancelEvent -> addFrame.dispose());

            addFrame.setVisible(true);
        });

        // Remove User Button Action
        removeUserButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(usersFrame, "Please select a user to remove", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int userId = (int) userTable.getValueAt(selectedRow, 0);
            String userName = (String) userTable.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(usersFrame, 
                "Are you sure you want to remove user: " + userName + "?", 
                "Confirm Removal", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                    String deleteQuery = "DELETE FROM users WHERE user_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                        pstmt.setInt(1, userId);
                        int affectedRows = pstmt.executeUpdate();
                        
                        if (affectedRows > 0) {
                            JOptionPane.showMessageDialog(usersFrame, "User removed successfully");
                            
                            // Refresh data in both frames
                            refreshUserTable(userTable);
                            refreshMainData();
                        } else {
                            JOptionPane.showMessageDialog(usersFrame, "Failed to remove user", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Update User Button Action
        updateUserButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(usersFrame, "Please select a user to update", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int userId = (int) userTable.getValueAt(selectedRow, 0);
            String currentName = (String) userTable.getValueAt(selectedRow, 1);
            String currentEmail = (String) userTable.getValueAt(selectedRow, 2);
            
            JFrame updateFrame = new JFrame("Update User");
            updateFrame.setSize(350, 250);
            updateFrame.setLocationRelativeTo(null);
            updateFrame.setLayout(null);

            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setBounds(30, 30, 100, 25);
            updateFrame.add(nameLabel);
            
            JTextField nameField = new JTextField(currentName);
            nameField.setBounds(140, 30, 180, 25);
            updateFrame.add(nameField);

            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setBounds(30, 70, 100, 25);
            updateFrame.add(emailLabel);
            
            JTextField emailField = new JTextField(currentEmail);
            emailField.setBounds(140, 70, 180, 25);
            updateFrame.add(emailField);

            JButton updateConfirmButton = new JButton("Update");
            updateConfirmButton.setBounds(70, 130, 100, 30);
            updateFrame.add(updateConfirmButton);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBounds(180, 130, 100, 30);
            updateFrame.add(cancelButton);

            updateConfirmButton.addActionListener(updateEvent -> {
                String newName = nameField.getText();
                String newEmail = emailField.getText();
                
                try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                    String updateQuery = "UPDATE users SET name = ?, email = ? WHERE user_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                        pstmt.setString(1, newName);
                        pstmt.setString(2, newEmail);
                        pstmt.setInt(3, userId);
                        int affectedRows = pstmt.executeUpdate();
                        
                        if (affectedRows > 0) {
                            JOptionPane.showMessageDialog(updateFrame, "User updated successfully");
                            
                            // Close update frame
                            updateFrame.dispose();
                            
                            // Refresh data in both frames
                            refreshUserTable(userTable);
                            refreshMainData();
                        } else {
                            JOptionPane.showMessageDialog(updateFrame, "Failed to update user", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(cancelEvent -> updateFrame.dispose());

            updateFrame.setVisible(true);
        });

        usersFrame.setVisible(true);
    }
    
    // Method to refresh user table in the manage users frame
    private static void refreshUserTable(JTable userTable) {
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Load users data
            String userQuery = "SELECT user_id, name, email, role FROM users";
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery);
                 ResultSet userRs = userStmt.executeQuery()) {

                DefaultTableModel userModel = new DefaultTableModel(
                        new String[]{"User ID", "Name", "Email", "Role"}, 0);

                while (userRs.next()) {
                    userModel.addRow(new Object[]{
                            userRs.getInt("user_id"),
                            userRs.getString("name"),
                            userRs.getString("email"),
                            userRs.getString("role")
                    });
                }

                userTable.setModel(userModel);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to open Manage Books Frame
    private static void openManageBooksFrame() {
        JFrame booksFrame = new JFrame("Manage Books");
        booksFrame.setSize(800, 500);
        booksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        booksFrame.setLocationRelativeTo(null);
        booksFrame.setLayout(null);

        // Books Table
        JTable bookTable = new JTable();
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookScrollPane.setBounds(20, 20, 750, 350);
        booksFrame.add(bookScrollPane);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBounds(20, 380, 750, 60);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        booksFrame.add(buttonsPanel);

        // Add Book Button
        JButton addBookButton = new JButton("Add Book");
        buttonsPanel.add(addBookButton);

        // Remove Book Button
        JButton removeBookButton = new JButton("Remove Book");
        buttonsPanel.add(removeBookButton);

        // Update Book Button
        JButton updateBookButton = new JButton("Update Book");
        buttonsPanel.add(updateBookButton);

        // Load books data into table
        refreshBookTable(bookTable);

        // Add Book Button Action
        addBookButton.addActionListener(e -> {
            JFrame addFrame = new JFrame("Add Book");
            addFrame.setSize(350, 350);
            addFrame.setLocationRelativeTo(null);
            addFrame.setLayout(null);

            JLabel titleLabel = new JLabel("Title:");
            titleLabel.setBounds(30, 30, 100, 25);
            addFrame.add(titleLabel);
            
            JTextField titleField = new JTextField();
            titleField.setBounds(140, 30, 180, 25);
            addFrame.add(titleField);

            JLabel authorLabel = new JLabel("Author:");
            authorLabel.setBounds(30, 70, 100, 25);
            addFrame.add(authorLabel);
            
            JTextField authorField = new JTextField();
            authorField.setBounds(140, 70, 180, 25);
            addFrame.add(authorField);

            JLabel categoryLabel = new JLabel("Category:");
            categoryLabel.setBounds(30, 110, 100, 25);
            addFrame.add(categoryLabel);
            
            JTextField categoryField = new JTextField();
            categoryField.setBounds(140, 110, 180, 25);
            addFrame.add(categoryField);

            JLabel yearLabel = new JLabel("Year:");
            yearLabel.setBounds(30, 150, 100, 25);
            addFrame.add(yearLabel);
            
            JTextField yearField = new JTextField();
            yearField.setBounds(140, 150, 180, 25);
            addFrame.add(yearField);

            JLabel copiesLabel = new JLabel("Copies:");
            copiesLabel.setBounds(30, 190, 100, 25);
            addFrame.add(copiesLabel);
            
            JTextField copiesField = new JTextField();
            copiesField.setBounds(140, 190, 180, 25);
            addFrame.add(copiesField);

            JButton saveButton = new JButton("Save");
            saveButton.setBounds(70, 240, 100, 30);
            addFrame.add(saveButton);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBounds(180, 240, 100, 30);
            addFrame.add(cancelButton);

            saveButton.addActionListener(saveEvent -> {
                String title = titleField.getText();
                String author = authorField.getText();
                String category = categoryField.getText();
                
                try {
                    int year = Integer.parseInt(yearField.getText());
                    int copies = Integer.parseInt(copiesField.getText());
                    
                    try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                        String insertQuery = "INSERT INTO books (title, author, category, publication_year, available_copies) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                            pstmt.setString(1, title);
                            pstmt.setString(2, author);
                            pstmt.setString(3, category);
                            pstmt.setInt(4, year);
                            pstmt.setInt(5, copies);
                            pstmt.executeUpdate();
                            
                            JOptionPane.showMessageDialog(addFrame, "Book added: " + title);
                            
                            // Close add frame
                            addFrame.dispose();
                            
                            // Refresh data in both frames
                            refreshBookTable(bookTable);
                            refreshMainData();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addFrame, "Please enter valid numbers for year and copies", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(cancelEvent -> addFrame.dispose());

            addFrame.setVisible(true);
        });

        // Remove Book Button Action
        removeBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(booksFrame, "Please select a book to remove", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int bookId = (int) bookTable.getValueAt(selectedRow, 0);
            String bookTitle = (String) bookTable.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(booksFrame, 
                "Are you sure you want to remove book: " + bookTitle + "?", 
                "Confirm Removal", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                    String deleteQuery = "DELETE FROM books WHERE book_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                        pstmt.setInt(1, bookId);
                        int affectedRows = pstmt.executeUpdate();
                        
                        if (affectedRows > 0) {
                            JOptionPane.showMessageDialog(booksFrame, "Book removed successfully");
                            
                            // Refresh data in both frames
                            refreshBookTable(bookTable);
                            refreshMainData();
                        } else {
                            JOptionPane.showMessageDialog(booksFrame, "Failed to remove book", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Update Book Button Action
        updateBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(booksFrame, "Please select a book to update", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int bookId = (int) bookTable.getValueAt(selectedRow, 0);
            String currentTitle = (String) bookTable.getValueAt(selectedRow, 1);
            String currentAuthor = (String) bookTable.getValueAt(selectedRow, 2);
            String currentCategory = (String) bookTable.getValueAt(selectedRow, 3);
            int currentYear = (int) bookTable.getValueAt(selectedRow, 4);
            int currentCopies = (int) bookTable.getValueAt(selectedRow, 5);
            
            JFrame updateFrame = new JFrame("Update Book");
            updateFrame.setSize(350, 350);
            updateFrame.setLocationRelativeTo(null);
            updateFrame.setLayout(null);

            JLabel titleLabel = new JLabel("Title:");
            titleLabel.setBounds(30, 30, 100, 25);
            updateFrame.add(titleLabel);
            
            JTextField titleField = new JTextField(currentTitle);
            titleField.setBounds(140, 30, 180, 25);
            updateFrame.add(titleField);

            JLabel authorLabel = new JLabel("Author:");
            authorLabel.setBounds(30, 70, 100, 25);
            updateFrame.add(authorLabel);
            
            JTextField authorField = new JTextField(currentAuthor);
            authorField.setBounds(140, 70, 180, 25);
            updateFrame.add(authorField);

            JLabel categoryLabel = new JLabel("Category:");
            categoryLabel.setBounds(30, 110, 100, 25);
            updateFrame.add(categoryLabel);
            
            JTextField categoryField = new JTextField(currentCategory);
            categoryField.setBounds(140, 110, 180, 25);
            updateFrame.add(categoryField);

            JLabel yearLabel = new JLabel("Year:");
            yearLabel.setBounds(30, 150, 100, 25);
            updateFrame.add(yearLabel);
            
            JTextField yearField = new JTextField(String.valueOf(currentYear));
            yearField.setBounds(140, 150, 180, 25);
            updateFrame.add(yearField);

            JLabel copiesLabel = new JLabel("Copies:");
            copiesLabel.setBounds(30, 190, 100, 25);
            updateFrame.add(copiesLabel);
            
            JTextField copiesField = new JTextField(String.valueOf(currentCopies));
            copiesField.setBounds(140, 190, 180, 25);
            updateFrame.add(copiesField);

            JButton updateConfirmButton = new JButton("Update");
            updateConfirmButton.setBounds(70, 240, 100, 30);
            updateFrame.add(updateConfirmButton);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBounds(180, 240, 100, 30);
            updateFrame.add(cancelButton);

            updateConfirmButton.addActionListener(updateEvent -> {
                String newTitle = titleField.getText();
                String newAuthor = authorField.getText();
                String newCategory = categoryField.getText();
                
                try {
                    int newYear = Integer.parseInt(yearField.getText());
                    int newCopies = Integer.parseInt(copiesField.getText());
                    
                    try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                        String updateQuery = "UPDATE books SET title = ?, author = ?, category = ?, publication_year = ?, available_copies = ? WHERE book_id = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                            pstmt.setString(1, newTitle);
                            pstmt.setString(2, newAuthor);
                            pstmt.setString(3, newCategory);
                            pstmt.setInt(4, newYear);
                            pstmt.setInt(5, newCopies);
                            pstmt.setInt(6, bookId);
                            int affectedRows = pstmt.executeUpdate();
                            
                            if (affectedRows > 0) {
                                JOptionPane.showMessageDialog(updateFrame, "Book updated successfully");
                                
                                // Close update frame
                                updateFrame.dispose();
                                
                                // Refresh data in both frames
                                refreshBookTable(bookTable);
                                refreshMainData();
                            } else {
                                JOptionPane.showMessageDialog(updateFrame, "Failed to update book", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(updateFrame, "Please enter valid numbers for year and copies", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(cancelEvent -> updateFrame.dispose());

            updateFrame.setVisible(true);
        });

        booksFrame.setVisible(true);
    }
    
    // Method to refresh book table in the manage books frame
    private static void refreshBookTable(JTable bookTable) {
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Load books data
            String bookQuery = "SELECT book_id, title, author, category, publication_year, available_copies FROM books";
            try (PreparedStatement bookStmt = conn.prepareStatement(bookQuery);
                 ResultSet bookRs = bookStmt.executeQuery()) {

                DefaultTableModel bookModel = new DefaultTableModel(
                        new String[]{"Book ID", "Title", "Author", "Category", "Year", "Available Copies"}, 0);

                while (bookRs.next()) {
                    bookModel.addRow(new Object[]{
                            bookRs.getInt("book_id"),
                            bookRs.getString("title"),
                            bookRs.getString("author"),
                            bookRs.getString("category"),
                            bookRs.getInt("publication_year"),
                            bookRs.getInt("available_copies")
                    });
                }

                bookTable.setModel(bookModel);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}