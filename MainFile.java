import javax.swing.JFrame;

public class MainFile {

    public static void main(String[] args) {

        // Existing equipment
        SolarPanel solarPanel = new SolarPanel();
        OxygenGenerator oxygenGenerator = new OxygenGenerator();

        System.out.println("=== Space Station Survival ===");

        System.out.println("\nTesting equipment:");
        solarPanel.operate();
        oxygenGenerator.operate();

        System.out.println("\nTesting repairs:");
        solarPanel.repair();
        oxygenGenerator.repair(20);

        System.out.println("\nTesting conditions:");
        System.out.println("Solar Panel: "
                + solarPanel.getCondition() + "%");

        System.out.println("Oxygen Generator: "
                + oxygenGenerator.getCondition() + "%");


        // ======================================
        // AAYUSH - SPACECRAFT FUEL SYSTEM
        // ======================================

        FuelTank fuelTank = new FuelTank(100);

        System.out.println("\n=== Spacecraft System ===");
        System.out.println("Starting Fuel: "
                + fuelTank.getFuel() + "%");


        // Launch
        System.out.println("\nLaunching spaceship...");

        if (fuelTank.useFuel(20)) {
            System.out.println("Launch successful.");
            System.out.println("Fuel after launch: "
                    + fuelTank.getFuel() + "%");
        } else {
            System.out.println(
                    "Launch failed. Not enough fuel.");
        }


        // Moving upward
        System.out.println("\nMoving spaceship upward...");

        if (fuelTank.useFuel(5)) {
            System.out.println(
                    "Spaceship moved upward.");

            System.out.println(
                    "Fuel remaining: "
                    + fuelTank.getFuel()
                    + "%");
        } else {
            System.out.println(
                    "Not enough fuel to move.");
        }


        // Travel towards Mars
        System.out.println("\nTravelling towards Mars...");

        if (fuelTank.useFuel(30)) {
            System.out.println(
                    "Spaceship is travelling to Mars.");

            System.out.println(
                    "Fuel remaining: "
                    + fuelTank.getFuel()
                    + "%");
        } else {
            System.out.println(
                    "Not enough fuel to travel.");
        }


        // Landing
        System.out.println("\nAttempting Mars landing...");

        if (fuelTank.useFuel(10)) {
            System.out.println(
                    "Spaceship successfully landed on Mars.");

            System.out.println(
                    "Fuel remaining: "
                    + fuelTank.getFuel()
                    + "%");
        } else {
            System.out.println(
                    "Landing failed. Not enough fuel.");
        }


        // Final fuel
        System.out.println("\nFinal Fuel: "
                + fuelTank.getFuel()
                + "%");


        // ======================================
        // EXISTING GUI
        // ======================================

        JFrame window =
                new JFrame("Space Station Survival");

        window.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        window.setSize(700, 500);

        window.add(new SimulationPanel());

        window.setLocationRelativeTo(null);

        window.setVisible(true);
    }
}