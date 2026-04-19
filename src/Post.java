public class Post {
    protected int id;
    protected String content, platform, author, date, type;

    public Post(int id, String content, String platform, String author, String date, String type) {
        this.id = id;
        this.content = content;
        this.platform = platform;
        this.author = author;
        this.date = date;
        this.type = type;
    }

    public String getFormattedContent() {
        return "📝 [POST] " + content;
    }

    public int getId() { return id; }
    public String getContent() { return content; }
    public String getPlatform() { return platform; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public String getType() { return type; }
}