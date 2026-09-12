public class Engineer extends Astronaut {

    public Engineer(String name) {
        super(name);
    }

    // Engineers override performTask to repair equipment instead
    @Override
    public void performTask() {
        System.out.println(getName() + " is repairing equipment.");
    }

    // Engineer specific method to repair a piece of equipment
    public void repairEquipment(Equipment equipment) {
        if (equipment == null) {
            System.out.println("There is no equipment here to repair.");
            return;
        }

        equipment.repair();
        System.out.println(getName() + " repaired the " + equipment.getClass().getSimpleName());
    }
}