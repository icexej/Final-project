public class VideoPost extends Post {
    public VideoPost(int id, String content, String platform, String author, String date, String type) {
        super(id, content, platform, author, date, type);
    }
    @Override
    public String getFormattedContent() {
        return "🎥 [VIDEO] " + content;
    }
}
