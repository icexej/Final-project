public class ImagePost extends Post {
    private String imageUrl;

    public ImagePost(int id, String content, String scheduledDate, String platform) {
        super(id, content, scheduledDate, platform);
    }

    @Override
    public void displayDetails() {
        System.out.println("[IMAGE POST #" + getId() + "]");
        System.out.println("Platform: " + getPlatform());
        System.out.println("Date: " + getScheduledDate());
        System.out.println("Content: " + getContent());
        System.out.println("---------------------------");
    }
}