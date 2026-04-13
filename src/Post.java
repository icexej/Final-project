import java.util.ArrayList;
import java.util.List;


public abstract class Post  {
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

    public int getId() { return id; }

    public String getContent() { return content; }

    public String getScheduledDate() { return scheduledDate; }

    public String getPlatform() { return platform; }

    public List<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
    }
    // В файле Post.java
    public void addComment(String author, String text) {
        // Мы склеиваем имя и текст в одну красивую строку
        this.comments.add(author + ": " + text);
    }

    public abstract void displayDetails();
}