import java.util.ArrayList;

public class AstronautTest {

    public static void main(String[] args) {

        // Create an Engineer
        Engineer alex = createEngineer("Alex");

        showAstronautInfo(alex);
        moveTowardsRocket(alex);
        boardRocket(alex);
        engineerRepairsEquipment(alex);

        // Create a Photographer
        Photographer sam = createPhotographer("Sam");

        boardRocket(sam);
        photographerPreparesCamera(sam);

        // Generic ArrayList that stores different types of Astronauts
        ArrayList<Astronaut> crew = new ArrayList<>();

        crew.add(alex);
        crew.add(sam);

        System.out.println("\n=== Crew tasks ===");

        // Polymorphism - each astronaut performs their own task
        for (Astronaut astronaut : crew) {
            astronaut.performTask();
        }
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

    // Shows an astronaut's information
    public static void showAstronautInfo(Astronaut astronaut) {
        System.out.println("\n=== Astronaut details ===");
        System.out.println("Name: " + astronaut.getName());
        System.out.println("Health: " + astronaut.getHealth());
        System.out.println("Oxygen: " + astronaut.getOxygen());
    }

    // Moves an astronaut towards the rocket
    public static void moveTowardsRocket(Astronaut astronaut) {
        System.out.println("\n=== Moving towards the rocket ===");
        System.out.println(
            astronaut.getName() + " is walking towards the rocket."
        );
    }

    // Astronaut boards the rocket
    public static void boardRocket(Astronaut astronaut) {
        System.out.println("\n=== Boarding the rocket ===");
        astronaut.board();
    }

    // Engineer repairs equipment
    public static void engineerRepairsEquipment(Engineer engineer) {
        System.out.println("\n=== Engineer repairing equipment ===");

        SolarPanel panel = new SolarPanel();

        engineer.repairEquipment(panel);
    }

    // Photographer performs their special task
    public static void photographerPreparesCamera(
            Photographer photographer) {

        System.out.println(
            "\n=== Photographer preparing the camera ==="
        );

        photographer.performTask();
    }
}