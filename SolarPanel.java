public class SolarPanel extends Equipment {

    public SolarPanel() {
        super("Solar Panel");
    }

    @Override
    public void operate() {
        checkWorking();
        System.out.println(name + " is generating energy.");
    }
}
