
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// The context of the State pattern keeps position, ground depth,
// and footprints when the astronaut switches between states.
public class SurfaceWalker {

    public static final double WALK_SPEED = 2.5;
    public static final double LOW_GRAVITY = 0.12;
    public static final double JUMP_SPEED = 5.5;
    public static final double GROUND_Y = 610;

    private static final double MIN_X = 40;
    private static final double MAX_X = 985;
    private static final double MIN_GROUND_Y = 535;
    private static final double STEP_LENGTH = 28;
    private static final double START_X = 610;
    private static final double PARTNER_OFFSET = 62;

    private final MovementState standing = new StandingState();
    private final MovementState walking = new WalkingState();
    private final MovementState jumping = new JumpingState();

    private MovementState current = standing;

    private double x = START_X;
    private double groundY = GROUND_Y;
    private double y = GROUND_Y;
    private double velocityY = 0;
    private double distanceSinceStep = 0;

    private boolean leftFoot = true;

    private final List<Footprint> footprints = new ArrayList<>();

    // Update astronaut movement and footprints
    public void update(boolean up, boolean down,
                       boolean left, boolean right) {

        current.update(this, up, down, left, right);

        footprints.forEach(Footprint::fade);
        footprints.removeIf(Footprint::isFaded);
    }

    // Jump action
    public void jump() {
        current.jump(this);
    }

    // State transitions
    public void toStanding() {
        current = standing;
    }

    public void toWalking() {
        current = walking;
    }

    public void startJump() {
        velocityY = -JUMP_SPEED;
        current = jumping;
    }

    // Horizontal movement
    public void moveHorizontally(double dx) {
        x = Math.max(MIN_X, Math.min(MAX_X, x + dx));
    }

    // Vertical movement across Mars terrain
    public void moveVertically(double dy) {

        double targetGround = Math.max(
            MIN_GROUND_Y,
            Math.min(GROUND_Y, groundY + dy)
        );

        double actualMove = targetGround - groundY;

        groundY = targetGround;
        y += actualMove;
    }

    // Create footprints while walking
    public void leaveFootprintIfStepped(double distance) {

        distanceSinceStep += distance;

        if (distanceSinceStep >= STEP_LENGTH) {

            distanceSinceStep = 0;

            double footY = groundY + 39
                    + (leftFoot ? 4 : 12);

            footprints.add(new Footprint(x, footY));
            footprints.add(
                new Footprint(x + PARTNER_OFFSET, footY)
            );

            leftFoot = !leftFoot;
        }
    }

    // Apply Mars low gravity
    public void applyLowGravity() {

        velocityY += LOW_GRAVITY;
        y += velocityY;

        if (y >= groundY) {
            y = groundY;
            velocityY = 0;
        }
    }

    // Movement information
    public boolean isOnGround() {
        return y >= groundY;
    }

    public boolean isMoving() {
        return current != standing;
    }

    public String getStateName() {
        return current.getName();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public List<Footprint> getFootprints() {
        return Collections.unmodifiableList(footprints);
    }

    // Reset astronaut
    public void reset() {

        x = START_X;
        groundY = GROUND_Y;
        y = GROUND_Y;

        velocityY = 0;
        distanceSinceStep = 0;
        leftFoot = true;

        footprints.clear();

        current = standing;
    }
}
