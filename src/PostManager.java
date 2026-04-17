import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostManager {
    private final String URL = "jdbc:sqlite:social_media.db";

    // --- АВТОРИЗАЦИЯ И РЕГИСТРАЦИЯ ---
    public boolean login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userRole = rs.getString("role");
                Main.currentUser = new User(username, password, userRole);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return false;
    }

    public void registerUser(User user, String secretCode) {
        String role = "777".equals(secretCode) ? "Admin" : "User";
        String sql = "INSERT INTO users(username, password, role) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            System.out.println("User registered as " + role + "! ✨");
        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }

    // --- РАБОТА С ПОСТАМИ ---
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                posts.add(new Post(
                        rs.getInt("id"),
                        rs.getString("content"),
                        rs.getString("platform"),
                        rs.getString("author"),
                        rs.getString("date"),
                        rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Read error: " + e.getMessage());
        }
        return posts;
    }

    public void addPost(String content, String platform, String author, String date, String type) {
        String sql = "INSERT INTO posts(content, platform, author, date, type) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, content);
            pstmt.setString(2, platform);
            pstmt.setString(3, author);
            pstmt.setString(4, date);
            pstmt.setString(5, type);
            pstmt.executeUpdate();
            System.out.println("Post saved to DB! ✅");
        } catch (SQLException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    public void deletePost(int id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ Пост #" + id + " удален.");
            } else {
                JOptionPane.showMessageDialog(null, "Пост с таким ID не найден.");
            }
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
    }
    public void addComment(int postId, String author, String text) {
        String sql = "INSERT INTO comments(post_id, author, text) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setString(2, author);
            pstmt.setString(3, text);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Comment error: " + e.getMessage());
        }
    }

    public List<String> getCommentsForPost(int postId) {
        List<String> comments = new ArrayList<>();
        String sql = "SELECT author, text FROM comments WHERE post_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                comments.add("   💬 " + rs.getString("author") + ": " + rs.getString("text"));
            }
        } catch (SQLException e) { }
        return comments;
    }
}