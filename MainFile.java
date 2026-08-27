import javax.swing.JFrame;

public class MainFile {

    public static void main(String[] args) {

        SolarPanel solarPanel = new SolarPanel();
        OxygenGenerator oxygenGenerator = new OxygenGenerator();

        Station station = new Station();
        station.addEquipment(solarPanel);
        station.addEquipment(oxygenGenerator);

        System.out.println("=== Space Station Survival ===");

        System.out.println("\nTesting equipment:");
        station.operateAll();

        System.out.println("\nTesting repairs:");
        solarPanel.repair();
        oxygenGenerator.repair(20);

        System.out.println("\nTesting conditions:");
        System.out.println("Solar Panel: " + solarPanel.getCondition() + "%");
        System.out.println("Oxygen Generator: " + oxygenGenerator.getCondition() + "%");

        Engineer engineer = new Engineer("Alex");
        engineer.repairEquipment(solarPanel);

        JFrame window = new JFrame("Space Station Survival");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(700, 500);
        window.add(new SimulationPanel(station));
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}