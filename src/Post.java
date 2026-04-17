public class Post {
    private int id;
    private String content;
    private String platform;
    private String author;
    private String date; // Новое поле
    private String type; // Новое поле

    //Constructor
    public Post(int id, String content, String platform, String author, String date, String type) {
        this.id = id;
        this.content = content;
        this.platform = platform;
        this.author = author;
        this.date = date;
        this.type = type;
    }

    // Getters
    public int getId() { return id; }
    public String getContent() { return content; }
    public String getPlatform() { return platform; }
    public String getAuthor() { return author; }
    public String getDate() { return date; } // Теперь этот метод существует!
    public String getType() { return type; } // И этот тоже!
}