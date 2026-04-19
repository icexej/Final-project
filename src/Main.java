import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class Main {
    public static Color buttonRose = new Color(255, 192, 203);
    public static User currentUser = null;
    private static PostManager manager = new PostManager();
    private static JTextArea displayArea = new JTextArea(15, 50);

    private static void setAestheticUI() {
        Color pastelPink = new Color(255, 240, 245);
        UIManager.put("OptionPane.background", pastelPink);
        UIManager.put("Panel.background", pastelPink);
        UIManager.put("Button.background", buttonRose);
        UIManager.put("Button.font", new Font("Comic Sans MS", Font.BOLD, 12));
    }

    public static void main(String[] args) {
        setAestheticUI();
        SetupDatabase.createNewDatabase();

        while (true) {
            String[] options = {"Login", "Register", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "Welcome to AIU Social Media!",
                    "Main Menu", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 0 && handleLogin()) break;
            if (choice == 1) showRegisterDialog();
            if (choice == 2 || choice == -1) System.exit(0);
        }
        createMainWindow();
    }

    private static boolean handleLogin() {
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        Object[] message = {"Username:", userField, "Password:", passField};

        int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = userField.getText();
            String pass = new String(passField.getPassword());
            if (manager.login(name, pass)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                return true;
            }
            JOptionPane.showMessageDialog(null, "Invalid username or password.");
        }
        return false;
    }

    private static void createMainWindow() {
        if (currentUser == null) return;

        JFrame frame = new JFrame("Social Scheduler 2026 | User: " + currentUser.getUsername());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout(15, 15));

        Color pastelPink = new Color(255, 235, 245);
        Color darkRose = new Color(219, 112, 147);
        frame.getContentPane().setBackground(pastelPink);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(darkRose);
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getUsername() + " (" + currentUser.getRole() + ") ✨");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        headerPanel.add(welcomeLabel);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Display Area
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(darkRose, 2), "Planned Posts",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Comic Sans MS", Font.BOLD, 14), darkRose));
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(pastelPink);
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnExport = new JButton("Export CSV");
        styleButton(btnExport, new Color(144, 238, 144), Color.BLACK);
        btnExport.addActionListener(e -> manager.exportToCSV());
        sidePanel.add(btnExport);
        sidePanel.add(Box.createVerticalStrut(10));

        JButton btnImport = new JButton("Import CSV");
        styleButton(btnImport, new Color(173, 216, 230), Color.BLACK); // Светло-голубая
        btnImport.addActionListener(e -> {
            manager.importFromCSV();
            refreshDisplay();
        });
        sidePanel.add(btnImport);
        sidePanel.add(Box.createVerticalStrut(10));

        JButton btnShow = new JButton(" Refresh List");
        JButton btnLogout = new JButton(" Logout");
        styleButton(btnShow, buttonRose, Color.BLACK);
        styleButton(btnLogout, new Color(255, 150, 150), Color.WHITE);
        sidePanel.add(btnShow); sidePanel.add(Box.createVerticalStrut(10));


        JButton btnComment = new JButton("Add Comment");
        styleButton(btnComment, buttonRose, Color.BLACK);
        btnComment.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog("Enter Post ID to comment:");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    int postId = Integer.parseInt(idStr);
                    String text = JOptionPane.showInputDialog("Your comment:");
                    if (text != null && !text.trim().isEmpty()) {
                        manager.addComment(postId, currentUser.getUsername(), text);
                        refreshDisplay();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid format.");
                }
            }
        });
        sidePanel.add(btnComment);
        sidePanel.add(Box.createVerticalStrut(10));


        if ("Admin".equals(currentUser.getRole())) {
            addAdminButton(sidePanel, "Add Image Post", "Image");
            addAdminButton(sidePanel, "Add Video Post", "Video");
            addAdminButton(sidePanel, "Add Story Post", "Story");

            JButton btnEdit = new JButton("Edit Post");
            styleButton(btnEdit, buttonRose, Color.BLACK);
            btnEdit.addActionListener(e -> editPost());
            sidePanel.add(btnEdit);
            sidePanel.add(Box.createVerticalStrut(10));

            JButton btnDelete = new JButton("Delete Post");
            styleButton(btnDelete, new Color(255, 150, 150), Color.WHITE);
            btnDelete.addActionListener(e -> deletePost());
            sidePanel.add(btnDelete);
        }

        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(btnLogout);
        frame.add(sidePanel, BorderLayout.EAST);

        btnShow.addActionListener(e -> refreshDisplay());
        btnLogout.addActionListener(e -> { frame.dispose(); main(null); });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        refreshDisplay();
    }

    private static void addAdminButton(JPanel panel, String label, String type) {
        JButton btn = new JButton(label);
        styleButton(btn, buttonRose, Color.BLACK);
        btn.addActionListener(e -> createPost(type));
        panel.add(btn);
        panel.add(Box.createVerticalStrut(10));
    }

    private static void refreshDisplay() {
        displayArea.setText("");
        List<Post> posts = manager.getAllPosts();
        if (posts.isEmpty()) {
            displayArea.append("\n  No posts scheduled yet.");
        } else {
            for (Post p : posts) {
                displayArea.append("🆔 ID: " + p.getId() + " | 👤 Author: " + p.getAuthor() + "\n");
                displayArea.append("📱 Platform: " + p.getPlatform() + " | 📅 Date: " + p.getDate() + " | 📂 Type: " + p.getType() + "\n");
                displayArea.append(p.getFormattedContent() + "\n");
                List<String> comments = manager.getCommentsForPost(p.getId());
                for (String c : comments) {
                    displayArea.append(c + "\n");
                }
                displayArea.append("--------------------------------------------------\n");
            }
        }
    }

    private static void createPost(String type) {
        String content = JOptionPane.showInputDialog("Enter " + type.toLowerCase() + " text:");

        if (content == null || content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Text cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String date = askDate();
        if (date == null) return;

        String plat = askPlat();
        if (plat == null) return;

        manager.addPost(content, plat, currentUser.getUsername(), date, type);
        refreshDisplay();
    }

    private static String askDate() {
        String datePattern = "\\d{2}\\.\\d{2}\\.\\d{4}";
        while (true) {
            String input = JOptionPane.showInputDialog(null, "Enter the date (DD.MM.YYYY):", "Planning", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return null;
            if (input.matches(datePattern)) return input;
            JOptionPane.showMessageDialog(null, "Format: DD.MM.YYYY", "Wrong", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String askPlat() {
        String[] plats = {"Instagram", "Telegram", "TikTok"};
        int c = JOptionPane.showOptionDialog(null, "Choose a platform:", "Select",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, plats, plats[0]);
        return (c >= 0) ? plats[c] : null;
    }

    private static void editPost() {
        String idStr = JOptionPane.showInputDialog("Enter Post ID to edit:");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                String newText = JOptionPane.showInputDialog("Enter new text for the post:");

                if (newText != null && !newText.trim().isEmpty()) {
                    manager.updatePost(id, newText); // Вызов метода из PostManager
                    JOptionPane.showMessageDialog(null, "Post #" + id + " updated! ✨");
                    refreshDisplay(); // Обновляем ленту
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.");
            }
        }
    }

    private static void deletePost() {
        String idStr = JOptionPane.showInputDialog("Enter Post ID to delete:");
        if (idStr != null) {
            try {
                manager.deletePost(Integer.parseInt(idStr));
                refreshDisplay();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid ID format.");
            }
        }
    }

    private static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
    }

    private static void showRegisterDialog() {
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        String[] roles = {"User", "Admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        JLabel secretLabel = new JLabel("Enter Secret Code:");
        JTextField secretField = new JTextField();
        secretLabel.setVisible(false); secretField.setVisible(false);

        roleBox.addActionListener(e -> {
            boolean isAdmin = roleBox.getSelectedItem().equals("Admin");
            secretLabel.setVisible(isAdmin);
            secretField.setVisible(isAdmin);
            Window window = SwingUtilities.getWindowAncestor(roleBox);
            if (window != null) window.pack();
        });

        Object[] message = {"Username:", userField, "Password:", passField, "Role:", roleBox, secretLabel, secretField};
        if (JOptionPane.showConfirmDialog(null, message, "Register", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String role = (String) roleBox.getSelectedItem();
            if (role.equals("Admin") && !"777".equals(secretField.getText().trim())) {
                JOptionPane.showMessageDialog(null, "Wrong secret code!");
                return;
            }
            manager.registerUser(new User(userField.getText().trim(), new String(passField.getPassword()).trim(), role), secretField.getText().trim());
            JOptionPane.showMessageDialog(null, "Account created!");
        }
    }
}