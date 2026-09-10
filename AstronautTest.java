public class AstronautTest {

    public static void main(String[] args) {
        Engineer alex = createEngineer("Alex");
        showAstronautInfo(alex);
        moveTowardsRocket(alex);
        boardRocket(alex);
        engineerRepairsEquipment(alex);

        Photographer sam = createPhotographer("Sam");
        boardRocket(sam);
        photographerPreparesCamera(sam);
    }

    // Creates a new Engineer astronaut
    public static Engineer createEngineer(String name) {
        System.out.println("=== Creating an astronaut ===");
        return new Engineer(name);
    }

    // Creates a new Photographer astronaut
    public static Photographer createPhotographer(String name) {
        System.out.println("=== Creating an astronaut ===");
        return new Photographer(name);
    }

    // Shows an astronaut's name, health, and oxygen
    public static void showAstronautInfo(Astronaut astronaut) {
        System.out.println("\n=== Astronaut details ===");
        System.out.println("Name: " + astronaut.getName());
        System.out.println("Health: " + astronaut.getHealth());
        System.out.println("Oxygen: " + astronaut.getOxygen());
    }

    // Moves the astronaut towards the rocket, ready to board
    public static void moveTowardsRocket(Astronaut astronaut) {
        System.out.println("\n=== Moving towards the rocket ===");
        System.out.println(astronaut.getName() + " is walking towards the rocket.");
    }

    // Astronaut boards the rocket
    public static void boardRocket(Astronaut astronaut) {
        System.out.println("\n=== Boarding the rocket ===");
        astronaut.board();
    }

    // Engineer repairs a piece of equipment
    public static void engineerRepairsEquipment(Engineer engineer) {
        System.out.println("\n=== Engineer repairing equipment ===");
        SolarPanel panel = new SolarPanel();
        engineer.repairEquipment(panel);
    }

    // Photographer prepares the camera and takes a photo
    public static void photographerPreparesCamera(Photographer photographer) {
        System.out.println("\n=== Photographer preparing the camera ===");
        photographer.performTask();
    }
}