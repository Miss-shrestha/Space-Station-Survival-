public class JumpingState implements MovementState {
    @Override
    public void update(SurfaceWalker walker, boolean up, boolean down,
                       boolean left, boolean right) {
        double dx = (right ? 1 : 0) - (left ? 1 : 0);
        double dy = (down ? 1 : 0) - (up ? 1 : 0);
        if (dx != 0 || dy != 0) {
            double scale = SurfaceWalker.WALK_SPEED / Math.hypot(dx, dy);
            walker.moveHorizontally(dx * scale);
            walker.moveVertically(dy * scale);
        }
        walker.applyLowGravity();
        if (walker.isOnGround()) {
            if (dx != 0 || dy != 0) {
                walker.toWalking();
            } else {
                walker.toStanding();
            }
        }
    }

    @Override
    public void jump(SurfaceWalker walker) {
        // No double jumping while airborne.
    }

    @Override
    public String getName() { return "Jumping"; }
}
