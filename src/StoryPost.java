public class StoryPost extends Post {
    private boolean isCloseFriendsOnly;

    public StoryPost(int id, String content, String scheduledDate, String platform, boolean isCloseFriendsOnly) {
        super(id, content, scheduledDate, platform);
        this.isCloseFriendsOnly = isCloseFriendsOnly;
    }

    public boolean isCloseFriendsOnly() { return isCloseFriendsOnly; }
    public void setCloseFriendsOnly(boolean closeFriendsOnly) { isCloseFriendsOnly = closeFriendsOnly; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    @Override
    public void displayDetails() {
        String visibility = isCloseFriendsOnly ? "Close Friends ONLY" : "Everyone";
        System.out.println("[STORY #" + getId() + "]");
        System.out.println("Platform: " + getPlatform());
        System.out.println("Visibility: " + visibility);
        System.out.println("Text/Sticker: " + getContent());
        System.out.println("---------------------------");
    }
}
