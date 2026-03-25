public class ImagePost extends Post {
    private String imageUrl;
    private String filter;

    public ImagePost(int id, String content, String scheduledDate, String platform, String imageUrl, String filter) {
        super(id, content, scheduledDate, platform);
        this.imageUrl = imageUrl;
        this.filter = filter;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public void displayDetails() {
        System.out.println("[IMAGE POST #" + getId() + "]");
        System.out.println("Platform: " + getPlatform());
        System.out.println("Date: " + getScheduledDate());
        System.out.println("Content: " + getContent());
        System.out.println("Image Path: " + imageUrl);
        System.out.println("Applied Filter: " + filter);
        System.out.println("---------------------------");
    }
}