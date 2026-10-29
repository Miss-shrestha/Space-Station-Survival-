public class StandingState implements MovementState {
    @Override
    public void update(SurfaceWalker walker, boolean up, boolean down,
                       boolean left, boolean right) {
        if (left != right || up != down) {
            walker.toWalking();
        }
    }

    @Override
    public void jump(SurfaceWalker walker) {
        walker.startJump();
    }

    @Override
    public String getName() { return "Standing"; }
}
