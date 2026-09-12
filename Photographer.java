public class Photographer extends Astronaut {

    private int photosTaken;

    public Photographer(String name) {
        super(name);
        photosTaken = 0;
    }

    // Photographers override performTask to take photos instead
    @Override
    public void performTask() {
        photosTaken = photosTaken + 1;
        System.out.println(getName() + " took a photo. Total photos: " + photosTaken);
    }

    public int getPhotosTaken() {
        return photosTaken;
    }
}