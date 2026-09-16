public class Camera extends Equipment {

    private int photosTaken;

    public Camera() {
        super("Camera");
        photosTaken = 0;
    }

    @Override
    public void operate() {
        checkWorking();
        photosTaken++;
        System.out.println(name + " took a photo.");
    }

    public int getPhotosTaken() {
        return photosTaken;
    }
}