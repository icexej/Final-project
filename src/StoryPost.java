public class StoryPost extends Post {
    public StoryPost(int id, String content, String platform, String author, String date, String type) {
        super(id, content, platform, author, date, type);
    }
    @Override
    public String getFormattedContent() {
        return "⏳ [STORY] " + content;
    }
}