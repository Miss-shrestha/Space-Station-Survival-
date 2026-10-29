import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Photographer extends Astronaut {

    // Generic list: only Photo objects can be stored here
    private final List<Photo> photos = new ArrayList<>();

    public Photographer(String name) {
        super(name);
    }

    // Take a photo of something at a position and store it
    public Photo takePhoto(String subject, int x, int y) {
        Photo photo = new Photo(getName(), subject, x, y);
        photos.add(photo);
        System.out.println(getName() + " took a photo of " + subject + ". Total photos: " + photos.size());
        return photo;
    }

    // Photographers override performTask to take photos instead
    @Override
    public void performTask() {
        takePhoto("the Martian horizon", 0, 0);
    }

    public int getPhotosTaken() {
        return photos.size();
    }

    // Read-only view so other classes cannot change the stored photos
    public List<Photo> getPhotos() {
        return Collections.unmodifiableList(photos);
    }
}