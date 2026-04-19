public class ImagePost extends Post {
    public ImagePost(int id, String content, String platform, String author, String date, String type) {
        super(id, content, platform, author, date, type);
    }
    @Override
    public String getFormattedContent() {
        return "[IMAGE] " + content;
    }
}