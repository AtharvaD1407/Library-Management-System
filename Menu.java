import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Menu{

    // Database connection details
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC"; 
    static final String DB_USER = "root"; 
    static final String DB_PASS = ""; 

    public static void main(String[] args) {
        JFrame frame = new JFrame("Library Management System");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center window on screen

        // Panel to hold buttons
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // Buttons for Add, Remove, and Search
        JButton addBookButton = new JButton("Add Book");
        JButton removeBookButton = new JButton("Remove Book");
        JButton searchBookButton = new JButton("Search Book");

        // Add buttons to the panel
        panel.add(addBookButton);
        panel.add(removeBookButton);
        panel.add(searchBookButton);

        // Add panel to the frame
        frame.add(panel);

        // Action listener for Add Book
        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = JOptionPane.showInputDialog(frame, "Enter Book Title:");
                String author = JOptionPane.showInputDialog(frame, "Enter Author Name:");
                String year = JOptionPane.showInputDialog(frame, "Enter Publication Year:");

                if (addBook(title, author, year)) {
                    JOptionPane.showMessageDialog(frame, "Book Added Successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Error Adding Book!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action listener for Remove Book
        removeBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookId = JOptionPane.showInputDialog(frame, "Enter Book ID to Remove:");
                if (removeBook(bookId)) {
                    JOptionPane.showMessageDialog(frame, "Book Removed Successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Error Removing Book!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action listener for Search Book
        searchBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookId = JOptionPane.showInputDialog(frame, "Enter Book ID to Search:");
                searchBook(bookId);
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    // Method to add a book to the database
    private static boolean addBook(String title, String author, String year) {
        String query = "INSERT INTO books (title, author, year) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, title);
            pst.setString(2, author);
            pst.setString(3, year);

            int rowsAffected = pst.executeUpdate(); // Execute the insert query
            return rowsAffected > 0; // If one or more rows are affected, return true
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Method to remove a book from the database by ID
    private static boolean removeBook(String bookId) {
        String query = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, bookId);
            int rowsAffected = pst.executeUpdate(); // Execute the delete query
            return rowsAffected > 0; // If the book is deleted, return true
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Method to search a book by ID and show details
    private static void searchBook(String bookId) {
        String query = "SELECT * FROM books WHERE book_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, bookId);
            ResultSet rs = pst.executeQuery(); // Execute the search query

            if (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String year = rs.getString("year");
                String details = "Book ID: " + bookId + "\nTitle: " + title + "\nAuthor: " + author + "\nYear: " + year;
                JOptionPane.showMessageDialog(null, details, "Book Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No book found with the given ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
