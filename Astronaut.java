public class Astronaut {

    private String name;
    private int health;
    private int oxygen;
    private int positionX;
    private int positionY;
    private boolean onMars;

    public Astronaut(String name) {
        this.name = name;
        this.health = 100;
        this.oxygen = 100;
        this.positionX = 0;
        this.positionY = 0;
        this.onMars = false;
    }

    public void exit() {
        onMars = true;
        System.out.println(name + " has exited the spaceship onto Mars.");
    }

    public void walk() throws AstronautDownException {
        if (onMars == false) {
            System.out.println(name + " cannot walk, still inside the spaceship.");
            return;
        }

        positionX = positionX + 1;
        oxygen = oxygen - 5;

        if (oxygen < 0) {
            oxygen = 0;
        }

        System.out.println(name + " walked to position " + positionX + ". Oxygen left: " + oxygen);

        checkOxygen();
    }

    public void returnToShip() {
        onMars = false;
        System.out.println(name + " has returned to the spaceship.");
    }

    public void board() {
        System.out.println(name + " has boarded the spaceship.");
    }

    private void checkOxygen() throws AstronautDownException {
        if (oxygen <= 0) {
            health = health - 10;

            if (health < 0) {
                health = 0;
            }

            System.out.println("Warning! " + name + " is out of oxygen. Health is now " + health);
        }

        if (health <= 0) {
            throw new AstronautDownException(name + " has run out of health and is down!");
        }
    }

    public void performTask() {
        System.out.println(name + " has no special task to perform.");
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getOxygen() {
        return oxygen;
    }
}