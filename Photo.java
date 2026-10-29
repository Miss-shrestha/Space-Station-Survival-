public class Photo {
    private final String photographer;
    private final String subject;
    private final int x;
    private final int y;

    public Photo(String photographer, String subject, int x, int y) {
        this.photographer = photographer;
        this.subject = subject;
        this.x = x;
        this.y = y;
    }

    public String getPhotographer() { return photographer; }
    public String getSubject() { return subject; }
    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public String toString() {
        return photographer + " photographed " + subject + " at (" + x + ", " + y + ")";
    }
}