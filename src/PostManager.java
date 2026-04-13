import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PostManager {
    // Основной список всех постов
    private List<Post> posts = new ArrayList<>();
    private final String FILE_NAME = "posts.dat";

    // 1. Метод для получения списка (используется в refreshDisplay)
    public List<Post> getAllPosts() {
        return posts;
    }

    // 2. Добавление поста и автоматическое сохранение
    public void addPost(Post post) {
        posts.add(post);
        saveToFile();
    }

    // 3. Автоматическая генерация следующего ID
    public int getNextId() {
        int maxId = 0;
        for (Post p : posts) {
            if (p.getId() > maxId) {
                maxId = p.getId();
            }
        }
        return maxId + 1;
    }

    // 4. Поиск поста по ID (нужен для добавления комментариев)
    public Post findPostById(int id) {
        for (Post p : posts) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    // 5. Удаление поста
    public void deletePost(int id) {
        posts.removeIf(p -> p.getId() == id);
        saveToFile();
    }

    // --- РАБОТА С ФАЙЛАМИ (Serialization) ---

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(posts);
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            posts = (List<Post>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error loading from file: " + e.getMessage());
        }
    }
}