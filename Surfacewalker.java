import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// The "context" of the State pattern: it owns all the state objects, so data such as
// position, velocity and footprints survives every transition.
public class SurfaceWalker {
    public static final double WALK_SPEED = 2.5;
    public static final double LOW_GRAVITY = 0.12;   // Earth-like would be about 0.5
    public static final double JUMP_SPEED = 5.5;
    public static final double GROUND_Y = 524;       // drawing anchor so the boots touch the ground

    private static final double MIN_X = 40;
    private static final double MAX_X = 1040;
    private static final double STEP_LENGTH = 28;
    private static final double START_X = 200;

    private final MovementState standing = new StandingState();
    private final MovementState walking = new WalkingState();
    private final MovementState jumping = new JumpingState();
    private MovementState current = standing;

    private double x = START_X;
    private double y = GROUND_Y;
    private double velocityY = 0;
    private double distanceSinceStep = 0;
    private boolean leftFoot = true;
    private final List<Footprint> footprints = new ArrayList<>();

    public void update(boolean left, boolean right) {
        current.update(this, left, right);
        footprints.forEach(Footprint::fade);
        footprints.removeIf(Footprint::isFaded);   // lambda-style removal of old footprints
    }

    public void jump() {
        current.jump(this);
    }

    // transitions, called by the states
    public void toStanding() { current = standing; }
    public void toWalking() { current = walking; }
    public void startJump() {
        velocityY = -JUMP_SPEED;
        current = jumping;
    }

    // actions, called by the states
    public void moveHorizontally(double dx) {
        x = Math.max(MIN_X, Math.min(MAX_X, x + dx));
    }

    public void leaveFootprintIfStepped(double distance) {
        distanceSinceStep += distance;
        if (distanceSinceStep >= STEP_LENGTH) {
            distanceSinceStep = 0;
            footprints.add(new Footprint(x, y + 36 + (leftFoot ? 4 : 12)));
            leftFoot = !leftFoot;
        }
    }

    public void applyLowGravity() {
        velocityY += LOW_GRAVITY;
        y += velocityY;
        if (y >= GROUND_Y) {
            y = GROUND_Y;
            velocityY = 0;
        }
    }

    public boolean isOnGround() { return y >= GROUND_Y; }
    public boolean isMoving() { return current != standing; }
    public String getStateName() { return current.getName(); }

    public double getX() { return x; }
    public double getY() { return y; }

    public List<Footprint> getFootprints() {
        return Collections.unmodifiableList(footprints);
    }

    public void reset() {
        x = START_X;
        y = GROUND_Y;
        velocityY = 0;
        distanceSinceStep = 0;
        leftFoot = true;
        footprints.clear();
        current = standing;
    }
}