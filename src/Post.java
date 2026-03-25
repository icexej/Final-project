import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Абстрактный класс — фундамент для всех типов постов
public abstract class Post implements Serializable {
    // Требование №8: Инкапсуляция (поля private)
    private int id;
    private String content;
    private String scheduledDate;
    private String platform;
    private List<String> comments; // Список для хранения комментариев

    public Post(int id, String content, String scheduledDate, String platform) {
        this.id = id;
        this.content = content;
        this.scheduledDate = scheduledDate;
        this.platform = platform;
        this.comments = new ArrayList<>(); // Инициализируем пустой список при создании
    }

    // --- Геттеры и Сеттеры (Инкапсуляция) ---
    public int getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getPlatform() { return platform; }

    // --- Логика комментариев (доступна и Админу, и Гостю) ---
    public void addComment(String author, String text) {
        if (text != null && !text.trim().isEmpty()) {
            comments.add(author + ": " + text);
        }
    }

    public List<String> getComments() {
        return comments;
    }

    // Вспомогательный метод для вывода комментариев на экран
    public void printComments() {
        if (comments.isEmpty()) {
            System.out.println("   (Комментариев пока нет)");
        } else {
            System.out.println("   Комментарии:");
            for (String c : comments) {
                System.out.println("     - " + c);
            }
        }
    }

    // Требование №10: Полиморфизм (абстрактный метод)
    // Каждый наследник (Video, Image, Story) реализует его по-своему
    public abstract void displayDetails();
}