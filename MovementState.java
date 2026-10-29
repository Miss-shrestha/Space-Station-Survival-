// State pattern: each state decides how the walker behaves in that state
// and when to change to a different state.
public interface MovementState {
    void update(SurfaceWalker walker, boolean left, boolean right);
    void jump(SurfaceWalker walker);
    String getName();
}