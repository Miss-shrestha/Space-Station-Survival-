public class Equipment {
    protected String name;
    protected int condition;

    public Equipment(String name) {
        this.name = name;
        this.condition = 100;
    }

    public void operate() {
        System.out.println(name + " is operating.");
    }

    // First repair method
    public void repair() {
        condition = 100;
        System.out.println(name + " has been fully repaired.");
    }

    // Overloaded repair method
    public void repair(int amount) {
        condition = condition + amount;

        if (condition > 100) {
            condition = 100;
        }

        System.out.println(name + " condition: " + condition);
    }
}
