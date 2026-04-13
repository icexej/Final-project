public class VideoPost extends Post {
    private double duration; // duration in minutes

    public VideoPost(int id, String content, String scheduledDate, String platform, double duration) {
        super(id, content, scheduledDate, platform);
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    @Override
    public void displayDetails() {
        System.out.println("[VIDEO POST #" + getId() + "]");
        System.out.println("Duration: " + duration + " min");
        System.out.println("---------------------------");
    }
}