import java.util.ArrayList;

public class AstronautTest {

    public static void main(String[] args) {

        ArrayList<Astronaut> crew = new ArrayList<Astronaut>();

        Engineer eng = new Engineer("Alex");
        Photographer photo = new Photographer("Sam");

        crew.add(eng);
        crew.add(photo);

        System.out.println("=== Normal walk ===");
        for (int i = 0; i < crew.size(); i++) {
            Astronaut astronaut = crew.get(i);
            astronaut.board();
            astronaut.exit();

            try {
                astronaut.walk();
            } catch (AstronautDownException e) {
                System.out.println("MISSION ALERT: " + e.getMessage());
            }

            astronaut.performTask();
        }

        System.out.println("\n=== Demo: walking until health runs out ===");
        Photographer explorer = new Photographer("Jordan");
        explorer.board();
        explorer.exit();

        for (int i = 0; i < 30; i++) {
            try {
                explorer.walk();
            } catch (AstronautDownException e) {
                System.out.println("MISSION ALERT: " + e.getMessage());
                break;
            }
        }
    }
}