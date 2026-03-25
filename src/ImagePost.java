public class ImagePost extends Post {
    private String imageUrl;

    public ImagePost(int id, String content, String scheduledDate, String platform, String imageUrl) {
        super(id, content, scheduledDate, platform);
        this.imageUrl = imageUrl;
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
        System.out.println("---------------------------");
    }
}