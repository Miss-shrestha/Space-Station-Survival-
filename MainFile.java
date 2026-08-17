public class MainFile {

    public static void main(String[] args) {

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
        System.out.println("Solar Panel: " + solarPanel.getCondition() + "%");
        System.out.println("Oxygen Generator: " + oxygenGenerator.getCondition() + "%");
    }
}
