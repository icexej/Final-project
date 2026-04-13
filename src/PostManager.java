import java.io.*;
import java.util.*;

public class PostManager {
    private List<Post> posts = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    // Файлы теперь текстовые (.txt), чтобы их можно было легко читать
    private final String POST_FILE = "posts.txt";
    private final String USER_FILE = "users.txt";

    // --- УПРАВЛЕНИЕ ПОСТАМИ ---

    public List<Post> getAllPosts() {
        return posts;
    }

    public void addPost(Post post) {
        posts.add(post);
        saveToFile();
    }

    public int getNextId() {
        int maxId = 0;
        for (Post p : posts) {
            if (p.getId() > maxId) {
                maxId = p.getId();
            }
        }
        return maxId + 1;
    }

    public Post findPostById(int id) {
        for (Post p : posts) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public void deletePost(int id) {
        posts.removeIf(p -> p.getId() == id);
        saveToFile();
    }

    // --- УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ ---

    public List<User> getAllUsers() {
        return users;
    }

    public void registerUser(User user) {
        users.add(user);
        saveUsersToFile();
    }

    public boolean userExists(String username) {
        for (User u : users) {
            // Строгое сравнение (case-sensitive)
            if (u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User loginUser(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    // --- РАБОТА С ФАЙЛАМИ (Запись в читаемый текст) ---

    public void saveUsersToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (User u : users) {
                writer.println(u.getUsername() + "," + u.getPassword() + "," + u.getRole());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUsersFromFile() {
        users.clear();
        File file = new File(USER_FILE);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(POST_FILE))) {
            for (Post p : posts) {
                String type = "IMAGE";
                String extra = "none";

                if (p instanceof VideoPost) {
                    type = "VIDEO";
                    extra = String.valueOf(((VideoPost) p).getDuration());
                } else if (p instanceof StoryPost) {
                    type = "STORY";
                    extra = String.valueOf(((StoryPost) p).isCloseFriendsOnly());
                }

                writer.println(type + "|" + p.getId() + "|" + p.getScheduledDate() + "|" +
                        p.getPlatform() + "|" + p.getContent() + "|" + extra);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        posts.clear();
        File file = new File(POST_FILE);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;

                String type = parts[0];
                int id = Integer.parseInt(parts[1]);
                String date = parts[2];String plat = parts[3];
                String cont = parts[4];
                String extra = parts[5];

                if (type.equals("VIDEO")) {
                    posts.add(new VideoPost(id, cont, date, plat, Double.parseDouble(extra)));
                } else if (type.equals("STORY")) {
                    posts.add(new StoryPost(id, cont, date, plat, Boolean.parseBoolean(extra)));
                } else {
                    posts.add(new ImagePost(id, cont, date, plat));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}