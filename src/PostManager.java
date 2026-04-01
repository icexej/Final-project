import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PostManager {
    private List<Post> posts = new ArrayList<>();
    private final String FILE_NAME = "posts.dat";

    // Метод поиска (ВСТАВЛЯЕМ СЮДА)
    // Нужен для того, чтобы найти пост по ID и добавить к нему комментарий
    public Post findPostById(int id) {
        for (Post p : posts) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null; // Если пост не найден
    }

    // 1. CREATE
    public void addPost(Post post) {
        if (findPostById(post.getId()) != null) {
            System.out.println("❌ ОШИБКА: Пост с ID " + post.getId() + " уже существует!");
            System.out.println("Пожалуйста, используйте уникальный номер.");
            return; // Выходим из метода, не добавляя пост в список
        }

        // Если проверка прошла успешно:
        posts.add(post);
        saveToFile();
        System.out.println("✅ Пост успешно добавлен и сохранен!");
    }

    // 2. READ
    public void showAllPosts() {
        if (posts.isEmpty()) {
            System.out.println("Список пуст.");
            return;
        }
        for (Post p : posts) {
            p.displayDetails(); // Вызывает метод наследника (Image, Video или Story)
            p.printComments();  // Дополнительно выводит все комментарии к этому посту
            System.out.println("---------------------------");
        }
    }

    // 3. DELETE
    public void deletePost(int id) {
        if (posts.removeIf(p -> p.getId() == id)) {
            saveToFile();
            System.out.println("Пост #" + id + " удален.");
        } else {
            System.out.println("Пост не найден.");
        }
    }

    // --- ФАЙЛОВАЯ СИСТЕМА (Requirement #4) ---

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(posts);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            posts = (List<Post>) ois.readObject();
        } catch (Exception e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
        }
    }
    public List<Post> getAllPosts() {
        return posts;
    }
}