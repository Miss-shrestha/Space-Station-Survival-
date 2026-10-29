public class Footprint {
    private static final int LIFETIME = 600;   // frames before a footprint disappears

    private final double x;
    private final double y;
    private int age = 0;

    public Footprint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void fade() { age++; }

    public boolean isFaded() { return age >= LIFETIME; }

    public double getOpacity() { return 1.0 - (double) age / LIFETIME; }

    public double getX() { return x; }
    public double getY() { return y; }
}