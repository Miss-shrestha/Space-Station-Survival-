public class WalkingState implements MovementState {
    @Override
    public void update(SurfaceWalker walker, boolean left, boolean right) {
        if (left == right) {          // no key, or both keys: stop
            walker.toStanding();
            return;
        }
        double dx = right ? SurfaceWalker.WALK_SPEED : -SurfaceWalker.WALK_SPEED;
        walker.moveHorizontally(dx);
        walker.leaveFootprintIfStepped(Math.abs(dx));
    }

    @Override
    public void jump(SurfaceWalker walker) {
        walker.startJump();
    }

    @Override
    public String getName() { return "Walking"; }
}