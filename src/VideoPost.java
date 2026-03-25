public class VideoPost extends Post {
    private double duration; // duration in minutes

    public VideoPost(int id, String content, String scheduledDate, String platform, double duration) {
        super(id, content, scheduledDate, platform);
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        if (duration > 0) { //
            this.duration = duration;
        } else {
            System.out.println("Error: Duration must be greater than 0!");
        }
    }
    //Method
    @Override
    public void displayDetails() {
        System.out.println("[VIDEO POST #" + getId() + "]");
        System.out.println("Platform: " + getPlatform());
        System.out.println("Date: " + getScheduledDate());
        System.out.println("Content: " + getContent());
        System.out.println("Duration: " + duration + " min");
        System.out.println("---------------------------");
    }
}