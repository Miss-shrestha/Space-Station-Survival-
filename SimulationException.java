/**
 * Custom exception class for handling simulation errors such as 
 * invalid entity positions or missing references in Space Station Survival.
 */
public class SimulationException extends Exception {
    
    public SimulationException(String message) {
        super(message);
    }
    
    public static void checkPosition(int x, int y, int maxX, int maxY) throws SimulationException {
        if (x < 0 || y < 0 || x >= maxX || y >= maxY) {
            throw new SimulationException("Invalid position coordinates (" + x + ", " + y + ") outside grid bounds.");
        }
    }
}