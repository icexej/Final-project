import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class Main {
    private static PostManager manager = new PostManager();
    private static User currentUser = null;
    private static JTextArea displayArea = new JTextArea(15, 50);

    public static void main(String[] args) {
        manager.loadFromFile();
        showLoginDialog();
    }

    // --- 1. ОКНО ВХОДА ---
    private static void showLoginDialog() {
        UIManager.put("OptionPane.background", new Color(255, 240, 245));
        UIManager.put("Panel.background", new Color(255, 240, 245));

        String[] options = {"Administrator", "Guest", "Exit"};
        int choice = JOptionPane.showOptionDialog(null, "Welcome! Please log in:",
                "Social Scheduler 2026", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == 0) { // Admin
            String pass = JOptionPane.showInputDialog(null, "Enter Password (2404):", "Auth", JOptionPane.QUESTION_MESSAGE);
            if ("2404".equals(pass)) {
                currentUser = new User("Admin", "2404", "ADMIN");
                createMainWindow();
            } else {
                JOptionPane.showMessageDialog(null, "❌ Wrong password!", "Error", JOptionPane.ERROR_MESSAGE);
                showLoginDialog();
            }
        } else if (choice == 1) { // Guest
            String name = JOptionPane.showInputDialog("Enter your nickname:");
            currentUser = new User(name == null || name.isEmpty() ? "Guest" : name, "", "USER");
            createMainWindow();
        } else {
            System.exit(0);
        }
    }

    // --- 2. ГЛАВНОЕ ОКНО ---
    private static void createMainWindow() {
        JFrame frame = new JFrame("🌸 Social Scheduler 2026 - " + currentUser.getUsername() + " 🌸");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setLayout(new BorderLayout(15, 15));

        // Палитра цветов
        Color pastelPink = new Color(255, 235, 245);
        Color buttonRose = new Color(255, 192, 203);
        Color darkRose = new Color(219, 112, 147);

        frame.getContentPane().setBackground(pastelPink);

        // Область вывода постов
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        displayArea.setBackground(Color.WHITE);
        displayArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(displayArea);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(darkRose, 2), "Planned Posts",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Comic Sans MS", Font.BOLD, 14), darkRose);
        scrollPane.setBorder(border);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Боковая панель управления
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(pastelPink);
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Кнопки
        JButton btnShow = new JButton(" Refresh List");
        JButton btnComm = new JButton(" Add Comment");
        JButton btnLogout = new JButton(" Logout");

        styleButton(btnShow, buttonRose, Color.BLACK);
        styleButton(btnComm, buttonRose, Color.BLACK);
        styleButton(btnLogout, new Color(255, 150, 150), Color.WHITE);

        sidePanel.add(btnShow); sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(btnComm); sidePanel.add(Box.createVerticalStrut(15));

        // Админ-панель
        if ("ADMIN".equals(currentUser.getRole())) {
            JButton btnImg = new JButton(" Add Image Post");
            JButton btnVid = new JButton(" Add Video Post");
            JButton btnSty = new JButton(" Add Story Post");
            JButton btnDel = new JButton(" Delete Post");styleButton(btnImg, buttonRose, Color.BLACK);
            styleButton(btnVid, buttonRose, Color.BLACK);
            styleButton(btnSty, buttonRose, Color.BLACK);
            styleButton(btnDel, new Color(255, 200, 200), Color.DARK_GRAY);

            sidePanel.add(btnImg); sidePanel.add(Box.createVerticalStrut(10));
            sidePanel.add(btnVid); sidePanel.add(Box.createVerticalStrut(10));
            sidePanel.add(btnSty); sidePanel.add(Box.createVerticalStrut(10));
            sidePanel.add(btnDel); sidePanel.add(Box.createVerticalStrut(10));

            btnImg.addActionListener(e -> createImagePost());
            btnVid.addActionListener(e -> createVideoPost());
            btnSty.addActionListener(e -> createStoryPost());
            btnDel.addActionListener(e -> deletePost());
        }

        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(btnLogout);
        frame.add(sidePanel, BorderLayout.EAST);

        btnShow.addActionListener(e -> refreshDisplay());
        btnComm.addActionListener(e -> addComment());
        btnLogout.addActionListener(e -> { frame.dispose(); showLoginDialog(); });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        refreshDisplay();
    }

    // --- 3. ЛОГИКА ---
    private static void refreshDisplay() {
        displayArea.setText("");
        List<Post> posts = manager.getAllPosts();

        if (posts.isEmpty()) {
            displayArea.append("\n  No posts scheduled yet.");
        } else {
            for (Post p : posts) {
                int commentCount = p.getComments().size();

                // Общая информация для всех постов
                displayArea.append(" ID: " + p.getId() + " | Platform: " + p.getPlatform() + "\n");
                displayArea.append(" Date: " + p.getScheduledDate() + "\n");
                displayArea.append(" Text: " + p.getContent() + "\n");

                // --- ВЫВОД СПЕЦИФИЧЕСКИХ ДАННЫХ ---
                if (p instanceof VideoPost) {
                    // Если это видео, выводим длительность
                    VideoPost vp = (VideoPost) p;
                    displayArea.append(" Type: Video Post | Duration: " + vp.getDuration() + " min\n");
                }
                else if (p instanceof StoryPost) {
                    // Если это сторис, выводим приватность
                    StoryPost sp = (StoryPost) p;
                    String privacy = sp.isCloseFriendsOnly() ? "Close Friends Only" : "Public";
                    displayArea.append(" Type: Story Post | Privacy: " + privacy + "\n");
                }
                else if (p instanceof ImagePost) {
                    // Если это фото
                    displayArea.append(" Type: Image Post\n");
                }

                // Счетчик комментариев
                displayArea.append(" Comments count: " + commentCount + "\n");

                // Список комментариев (если есть)
                if (commentCount > 0) {
                    for (String c : p.getComments()) {
                        displayArea.append("   - " + c + "\n");
                    }
                }
                displayArea.append(" --------------------------------------------------\n");
            }
        }
    }

    private static void createImagePost() {
        // Теперь ID берется автоматически!
        int id = manager.getNextId();

        String content = JOptionPane.showInputDialog(null, "Enter post text:", "New Image Post", JOptionPane.QUESTION_MESSAGE);
        if (content == null) return; // Если нажали Cancel

        String date = askDate();
        if (date == null) return;

        String plat = askPlat();

        manager.addPost(new ImagePost(id, content, date, plat));
        refreshDisplay();

        JOptionPane.showMessageDialog(null, "Post created with ID: " + id);
    }

    private static void createVideoPost() {
        int id = manager.getNextId();

        String content = JOptionPane.showInputDialog(null, "Enter video description:", "New Video Post", JOptionPane.QUESTION_MESSAGE);
        if (content == null) return;

        String date = askDate();
        if (date == null) return;

        String plat = askPlat();

        try {
            String durStr = JOptionPane.showInputDialog(null, "Enter duration (in minutes):", "Video Duration", JOptionPane.QUESTION_MESSAGE);
            if (durStr == null) return;
            double duration = Double.parseDouble(durStr);

            manager.addPost(new VideoPost(id, content, date, plat, duration));
            refreshDisplay();
            JOptionPane.showMessageDialog(null, "Video Post created! ID: " + id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, " Please enter a valid number for duration!");
        }
    }

    private static void createStoryPost() {
        int id = manager.getNextId();

        String content = JOptionPane.showInputDialog(null, "Enter story text:", "New Story Post", JOptionPane.QUESTION_MESSAGE);
        if (content == null) return;

        String date = askDate();
        if (date == null) return;

        String plat = askPlat();

        // Окно выбора: Yes = Close Friends, No = Public
        int choice = JOptionPane.showConfirmDialog(null, "Is this for Close Friends only?", "Privacy Settings", JOptionPane.YES_NO_OPTION);
        boolean isCloseFriends = (choice == JOptionPane.YES_OPTION);

        manager.addPost(new StoryPost(id, content, date, plat, isCloseFriends));
        refreshDisplay();
        JOptionPane.showMessageDialog(null, "Story Post created! ID: " + id);
    }

    private static void addComment() {
        String idStr = JOptionPane.showInputDialog("Post ID:");
        if (idStr == null) return;
        Post p = manager.findPostById(Integer.parseInt(idStr));
        if (p != null) {
            String comm = JOptionPane.showInputDialog("Comment text:");
            if (comm != null) {
                p.addComment(currentUser.getUsername(), comm);
                manager.saveToFile();
                refreshDisplay();
            }
        } else JOptionPane.showMessageDialog(null, "Not found!");
    }

    private static void deletePost() {
        String idStr = JOptionPane.showInputDialog("ID to delete:");
        if (idStr != null) {
            manager.deletePost(Integer.parseInt(idStr));
            refreshDisplay();
        }
    }

    // --- ИСПРАВЛЕННЫЕ ХЕЛПЕРЫ ---

    private static int askId() {
        while (true) {
            String s = JOptionPane.showInputDialog(null, "Enter Unique ID (Positive Number):", "ID Input", JOptionPane.QUESTION_MESSAGE);
            if (s == null) return -1; // Если нажали Cancel, выходим в меню

            try {
                int id = Integer.parseInt(s);
                if (id > 0 && manager.findPostById(id) == null) {
                    return id;
                } else {
                    JOptionPane.showMessageDialog(null, " ID must be positive and unique!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, " Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String askDate() {
        while (true) {
            String d = JOptionPane.showInputDialog(null, "Enter Date (dd.mm.yyyy):", "Date Input", JOptionPane.QUESTION_MESSAGE);
            if (d == null) return null; // Если нажали Cancel, выходим в меню

            // Регулярное выражение и проверка диапазона
            if (d.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
                try {
                    String[] parts = d.split("\\.");
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);

                    if (day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 2000 && year <= 2026) {
                        return d; // Дата верна
                    }
                } catch (Exception e) {}
            }
            JOptionPane.showMessageDialog(null, " Invalid format or date! Use dd.mm.yyyy (Year up to 2026)", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String askPlat() {
        String[] plats = {"Instagram", "Telegram", "TikTok"};
        int c = JOptionPane.showOptionDialog(null, "Platform:", "Select", 0, 3, null, plats, plats[0]);
        return (c >= 0) ? plats[c] : "Instagram";
    }

    private static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 1));
    }
}
