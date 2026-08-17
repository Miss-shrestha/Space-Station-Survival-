public class Main {

    public static void main(String[] args) {
        SolarPanel solarPanel = new SolarPanel();
        OxygenGenerator oxygenGenerator = new OxygenGenerator();

        solarPanel.operate();
        oxygenGenerator.operate();

        solarPanel.repair();
        oxygenGenerator.repair(20);
    }
}
