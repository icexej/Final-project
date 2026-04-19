import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
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

    public List<Post> getAllPosts() {

        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("content");
                String platform = rs.getString("platform");
                String author = rs.getString("author");
                String date = rs.getString("date");
                String type = rs.getString("type");

                // Используем полиморфизм при создании объектов
                if ("Video".equals(type)) posts.add(new VideoPost(id, content, platform, author, date, type));
                else if ("Story".equals(type)) posts.add(new StoryPost(id, content, platform, author, date, type));
                else posts.add(new ImagePost(id, content, platform, author, date, type));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return posts;
    }

    // UPDATE операция (для полного CRUD)
    public void updatePost(int id, String newContent) {
        String sql = "UPDATE posts SET content = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newContent);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void exportToCSV() {
        List<Post> posts = getAllPosts();
        try (FileWriter writer = new FileWriter("social_data_export.csv")) {
            writer.append("ID,Type,Author,Platform,Date,Content\n");
            for (Post p : posts) {
                writer.append(p.getId() + ",").append(p.getType() + ",").append(p.getAuthor() + ",")
                        .append(p.getPlatform() + ",").append(p.getDate() + ",").append(p.getContent() + "\n");
            }
            javax.swing.JOptionPane.showMessageDialog(null, "Data exported to social_data_export.csv! ✅");
        } catch (IOException e) { e.printStackTrace(); }
    }


    public void importFromCSV() {
        String line;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("social_data_export.csv"))) {
            br.readLine(); // Пропускаем заголовок (ID, Type...)
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 6) {
                    // data[5] - контент, data[3] - платформа, data[2] - автор, data[4] - дата, data[1] - тип
                    addPost(data[5], data[3], data[2], data[4], data[1]);
                    count++;
                }
            }
            javax.swing.JOptionPane.showMessageDialog(null, "Successfully imported " + count + " posts! ✅");
        } catch (java.io.IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Import error: " + e.getMessage());
        }
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
        // Используем try (), чтобы соединение закрылось само!
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
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