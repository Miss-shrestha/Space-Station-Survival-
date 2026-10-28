public class JumpingState implements MovementState {
    @Override
    public void update(SurfaceWalker walker, boolean left, boolean right) {
        walker.applyLowGravity();                 // Mars gravity: floaty jump
        if (left != right) {
            walker.moveHorizontally(left ? -SurfaceWalker.WALK_SPEED : SurfaceWalker.WALK_SPEED);
        }
        if (walker.isOnGround()) {                // landed: choose the next state
            if (left != right) {
                walker.toWalking();
            } else {
                walker.toStanding();
            }
        }
    }

    @Override
    public void jump(SurfaceWalker walker) {
        // no double jumping while in the air
    }

    @Override
    public String getName() { return "Jumping"; }
}