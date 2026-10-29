public class WalkingState implements MovementState {
    @Override
    public void update(SurfaceWalker walker, boolean up, boolean down,
                       boolean left, boolean right) {
        double dx = (right ? 1 : 0) - (left ? 1 : 0);
        double dy = (down ? 1 : 0) - (up ? 1 : 0);
        if (dx == 0 && dy == 0) {
            walker.toStanding();
            return;
        }
        // Diagonal movement uses the same speed as straight movement.
        double scale = SurfaceWalker.WALK_SPEED / Math.hypot(dx, dy);
        dx *= scale;
        dy *= scale;
        walker.moveHorizontally(dx);
        walker.moveVertically(dy);
        walker.leaveFootprintIfStepped(Math.hypot(dx, dy));
    }

    @Override
    public void jump(SurfaceWalker walker) {
        walker.startJump();
    }

    @Override
    public String getName() { return "Walking"; }
}
