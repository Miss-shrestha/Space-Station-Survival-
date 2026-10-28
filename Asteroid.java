import java.util.Random;

// One asteroid. Speed and spawn range match the team leader's original tuning.
public class Asteroid {
    private double x;
    private double y;
    private final int size;
    private final int speed;

    public Asteroid(int index, Random random) {
        this.speed = 5 + index % 4;
        this.size = 30 + random.nextInt(35);
        this.x = 330 + index * 100 + random.nextInt(80);
        this.y = 120 + random.nextInt(500);
    }

    public void move(Random random) {
        x -= speed;
        if (x < -60) {
            respawn(1100, 180, random);
        }
    }

    public void respawn(int baseX, int rangeX, Random random) {
        x = baseX + random.nextInt(rangeX);
        y = 110 + random.nextInt(500);
    }

    public boolean hits(double px, double py) {
        return Math.hypot(px - x, py - y) < size / 2.0 + 25;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return size; }
}