// State pattern: each state controls movement and transitions.
public interface MovementState {
    void update(SurfaceWalker walker, boolean up, boolean down,
                boolean left, boolean right);
    void jump(SurfaceWalker walker);
    String getName();
}
