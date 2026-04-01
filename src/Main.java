import javax.swing.*;
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

    // --- 1. СИСТЕМА ВХОДА ---
    private static void showLoginDialog() {
        String[] options = {"Администратор", "Гость", "Выйти"};
        int choice = JOptionPane.showOptionDialog(null, "Добро пожаловать! Выберите тип входа:",
                "Social Scheduler 2026", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) { // Админ
            String pass = JOptionPane.showInputDialog(null, "Введите пароль:", "Авторизация", JOptionPane.QUESTION_MESSAGE);
            if ("2404".equals(pass)) {
                currentUser = new User("Admin", "2404", "ADMIN");
                createMainWindow();
            } else {
                JOptionPane.showMessageDialog(null, "❌ Неверный пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                showLoginDialog();
            }
        } else if (choice == 1) { // Гость
            String name = JOptionPane.showInputDialog("Введите ваш никнейм:");
            currentUser = new User(name == null || name.isEmpty() ? "Аноним" : name, "", "USER");
            createMainWindow();
        } else {
            System.exit(0);
        }
    }

    // --- 2. ГЛАВНОЕ ОКНО ПРОГРАММЫ ---
    private static void createMainWindow() {
        JFrame frame = new JFrame("Social Scheduler - Сессия: " + currentUser.getUsername());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));

        // Текстовая область для вывода постов
        displayArea.setEditable(false);
        displayArea.setBackground(new Color(245, 245, 245));
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Боковая панель управления
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Общие кнопки
        JButton btnShow = new JButton("📱 Обновить список");
        JButton btnComment = new JButton("💬 Комментировать");
        JButton btnLogout = new JButton("🚪 Сменить профиль");

        styleButton(btnShow); styleButton(btnComment); styleButton(btnLogout);

        sidePanel.add(btnShow); sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(btnComment); sidePanel.add(Box.createVerticalStrut(10));

        // Кнопки только для Админа
        if ("ADMIN".equals(currentUser.getRole())) {
            JButton btnImg = new JButton("🖼 Добавить Image Post");
            JButton btnVid = new JButton("🎥 Добавить Video Post");
            JButton btnSty = new JButton("✨ Добавить Story Post");
            JButton btnDel = new JButton("🗑 Удалить пост");

            styleButton(btnImg); styleButton(btnVid); styleButton(btnSty); styleButton(btnDel);

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

        // Логика общих кнопок
        btnShow.addActionListener(e -> refreshDisplay());
        btnComment.addActionListener(e -> addComment());
        btnLogout.addActionListener(e -> { frame.dispose(); showLoginDialog(); });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        refreshDisplay();
    }

    // --- 3. ЛОГИКА ВАЛИДАЦИИ И СОЗДАНИЯ ---

    private static void refreshDisplay() {
        displayArea.setText("--- ТЕКУЩИЕ ПЛАНЫ ПОСТОВ ---\n\n");
        List<Post> posts = manager.getAllPosts(); // Проверь, что в PostManager есть этот метод!
        if (posts.isEmpty()) {
            displayArea.append("Список пуст. Добавьте первый пост как администратор.");
        } else {
            for (Post p : posts) {
                displayArea.append("🆔 ID: " + p.getId() + " | Платформа: " + p.getPlatform() + "\n");
                displayArea.append("📅 Дата: " + p.getScheduledDate() + "\n");
                displayArea.append("📝 Текст: " + p.getContent() + "\n");
                if (p.getComments().isEmpty()) {
                    displayArea.append("💬 Комментарии: нет\n");
                } else {
                    displayArea.append("💬 Комментарии:\n");
                    for (String c : p.getComments()) displayArea.append("   - " + c + "\n");
                }
                displayArea.append("--------------------------------------------------\n");
            }
        }
    }

    private static int askValidId() {
        while (true) {
            String input = JOptionPane.showInputDialog("Введите уникальный ID (число):");
            if (input == null) return -1;
            try {
                int id = Integer.parseInt(input);
                if (id > 0 && manager.findPostById(id) == null) return id;
                JOptionPane.showMessageDialog(null, "❌ ID должен быть уникальным и больше 0!");
            } catch (Exception e) { JOptionPane.showMessageDialog(null, "❌ Введите число!"); }
        }
    }

    private static String askValidDate() {
        while (true) {
            String date = JOptionPane.showInputDialog("Дата (дд.мм.гггг):");
            if (date == null) return null;
            if (date.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
                String[] p = date.split("\\.");
                int d = Integer.parseInt(p[0]), m = Integer.parseInt(p[1]), y = Integer.parseInt(p[2]);
                if (d >= 1 && d <= 31 && m >= 1 && m <= 12 && y >= 2000 && y <= 2026) return date;
            }
            JOptionPane.showMessageDialog(null, "❌ Ошибка! Формат дд.мм.гггг (год до 2026)");
        }
    }

    private static String askPlatform() {
        String[] plats = {"Instagram", "Telegram", "TikTok", "Facebook"};
        int c = JOptionPane.showOptionDialog(null, "Выберите платформу:", "Платформа",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, plats, plats[0]);
        return (c >= 0) ? plats[c] : "Instagram";
    }

    private static void createImagePost() {
        int id = askValidId(); if (id == -1) return;
        String content = JOptionPane.showInputDialog("Текст поста:");
        String date = askValidDate(); if (date == null) return;
        String plat = askPlatform();
        String url = JOptionPane.showInputDialog("Путь к фото (image path):");
        manager.addPost(new ImagePost(id, content, date, plat, url));
        refreshDisplay();
    }

    private static void createVideoPost() {
        int id = askValidId(); if (id == -1) return;
        String content = JOptionPane.showInputDialog("Текст видео:");
        String date = askValidDate(); if (date == null) return;
        String plat = askPlatform();
        double dur = Double.parseDouble(JOptionPane.showInputDialog("Длительность (мин):"));
        manager.addPost(new VideoPost(id, content, date, plat, dur));
        refreshDisplay();
    }

    private static void createStoryPost() {
        int id = askValidId(); if (id == -1) return;
        String content = JOptionPane.showInputDialog("Текст сторис:");
        String date = askValidDate(); if (date == null) return;String plat = askPlatform();
        int rel = JOptionPane.showConfirmDialog(null, "Только для близких?", "Приватность", JOptionPane.YES_NO_OPTION);
        manager.addPost(new StoryPost(id, content, date, plat, rel == JOptionPane.YES_OPTION));
        refreshDisplay();
    }

    private static void addComment() {
        String idStr = JOptionPane.showInputDialog("ID поста для комментария:");
        if (idStr == null) return;
        Post p = manager.findPostById(Integer.parseInt(idStr));
        if (p != null) {
            String comm = JOptionPane.showInputDialog("Ваш комментарий:");
            if (comm != null) {
                p.addComment(currentUser.getUsername(), comm);
                manager.saveToFile();
                refreshDisplay();
            }
        } else JOptionPane.showMessageDialog(null, "❌ Пост не найден.");
    }

    private static void deletePost() {
        String idStr = JOptionPane.showInputDialog("Введите ID для удаления:");
        if (idStr != null) {
            manager.deletePost(Integer.parseInt(idStr));
            refreshDisplay();
        }
    }

    private static void styleButton(JButton btn) {
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}