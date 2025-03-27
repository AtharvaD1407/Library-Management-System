import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Menu{

    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/userDB?serverTimezone=UTC"; 
    static final String DB_USER = "root"; 
    static final String DB_PASS = ""; 

    public static void main(String[] args) {
        JFrame frame = new JFrame("Library Management System");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton addBookButton = new JButton("Add Book");
        JButton removeBookButton = new JButton("Remove Book");
        JButton searchBookButton = new JButton("Search Book");

        panel.add(addBookButton);
        panel.add(removeBookButton);
        panel.add(searchBookButton);

        frame.add(panel);

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

        searchBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookId = JOptionPane.showInputDialog(frame, "Enter Book ID to Search:");
                searchBook(bookId);
            }
        });

        frame.setVisible(true);
    }

    private static boolean addBook(String title, String author, String year) {
        String query = "INSERT INTO books (title, author, year) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, title);
            pst.setString(2, author);
            pst.setString(3, year);

            int rowsAffected = pst.executeUpdate(); 
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static boolean removeBook(String bookId) {
        String query = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, bookId);
            int rowsAffected = pst.executeUpdate(); 
            return rowsAffected > 0; 
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static void searchBook(String bookId) {
        String query = "SELECT * FROM books WHERE book_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, bookId);
            ResultSet rs = pst.executeQuery(); 

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
