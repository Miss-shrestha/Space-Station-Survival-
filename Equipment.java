public class Equipment {

    protected String name;
    protected int condition;

    public Equipment(String name) {
        this.name = name;
        this.condition = 100;
    }

    public void operate() {
        checkWorking();
        System.out.println(name + " is operating.");
    }

    public void repair() {
        condition = 100;
        System.out.println(name + " has been fully repaired.");
    }

    public void repair(int amount) {
        condition = condition + amount;

        if (condition > 100) {
            condition = 100;
        }

        System.out.println(name + " condition: " + condition + "%");
    }

    public void damage(int amount) {
        if (amount > 0) {
            condition -= amount;

            if (condition < 0) {
                condition = 0;
            }
        }

        System.out.println(name + " condition: " + condition + "%");
    }

    public boolean isWorking() {
        return condition > 0;
    }

    protected void checkWorking() {
        if (!isWorking()) {
            throw new MissionException(name + " is damaged and cannot operate.");
        }
    }

    public int getCondition() {
        return condition;
    }
}
