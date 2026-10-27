import java.util.ArrayList;

public class AstronautTest {

    public static void main(String[] args) {

        // Create Sky as the Engineer
        Engineer sky = createEngineer("Sky");

        showAstronautInfo(sky);
        moveTowardsRocket(sky);
        boardRocket(sky);
        engineerRepairsEquipment(sky);

        // Create Rain as the Photographer
        Photographer rain = createPhotographer("Rain");

        showAstronautInfo(rain);
        moveTowardsRocket(rain);
        boardRocket(rain);
        photographerPreparesCamera(rain);

        // Stores different astronaut subclasses together
        ArrayList<Astronaut> crew = new ArrayList<>();

        crew.add(sky);
        crew.add(rain);

        System.out.println("\n=== Victory Crew Tasks ===");

        // Polymorphism: each astronaut performs their own task
        for (Astronaut astronaut : crew) {
            astronaut.performTask();
        }
    }

    // Creates an Engineer astronaut
    public static Engineer createEngineer(String name) {
        System.out.println("\n=== Creating Engineer ===");
        return new Engineer(name);
    }

    // Creates a Photographer astronaut
    public static Photographer createPhotographer(String name) {
        System.out.println("\n=== Creating Photographer ===");
        return new Photographer(name);
    }

    // Displays an astronaut's current information
    public static void showAstronautInfo(Astronaut astronaut) {
        System.out.println("\n=== Astronaut Details ===");
        System.out.println("Name: " + astronaut.getName());
        System.out.println("Health: " + astronaut.getHealth());
        System.out.println("Oxygen: " + astronaut.getOxygen());
    }

    // Moves an astronaut towards the rocket
    public static void moveTowardsRocket(Astronaut astronaut) {
        System.out.println("\n=== Moving Towards the Rocket ===");
        System.out.println(
                astronaut.getName()
                        + " is walking towards the Victory rocket."
        );
    }

    // Boards an astronaut onto the rocket
    public static void boardRocket(Astronaut astronaut) {
        System.out.println("\n=== Boarding the Rocket ===");
        astronaut.board();
    }

    // Sky repairs the solar panel
    public static void engineerRepairsEquipment(Engineer engineer) {
        System.out.println("\n=== Engineer Repairing Equipment ===");

        SolarPanel panel = new SolarPanel();
        engineer.repairEquipment(panel);
    }

    // Rain prepares the camera and completes the photography task
    public static void photographerPreparesCamera(
            Photographer photographer) {

        System.out.println(
                "\n=== Photographer Preparing the Camera ==="
        );

        photographer.performTask();
    }
}