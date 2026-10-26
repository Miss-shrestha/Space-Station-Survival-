import java.util.Random;

public class Asteroid {
    private double x;
    private double y;
    private final int size;
    private final int speed;

    public Asteroid(int index, Random random) {
        this.speed = 2 + index % 3;
        this.size = 30 + random.nextInt(35);
        this.x = 420 + index * 145 + random.nextInt(100);
        this.y = 120 + random.nextInt(500);
    }

    public void move(Random random) {
        x -= speed;
        if (x < -60) {
            respawn(1120, 400, random);
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