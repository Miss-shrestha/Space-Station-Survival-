public class OxygenGenerator extends Equipment {

    public OxygenGenerator() {
        super("Oxygen Generator");
    }

    @Override
    public void operate() {
        System.out.println(name + " is producing oxygen.");
    }
}
