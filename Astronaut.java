private void checkOxygen() throws AstronautDownException {
    if (oxygen <= 0) {
        oxygen = 0;
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

Commit message:

Prevent health from going below 0

Full checklist status after this fix: ✅ All 8 tasks in your role are now genuinely, fully complete.

This also strengthens your worksheet — you can honestly add to 2.1 (Astronaut's description): "Also ensures oxygen and health never drop below 0, keeping the simulation's values realistic." Want me to fold this into the worksheet answers now?

give me full code

Here's the complete, current Astronaut.java — copy and replace the whole file:

java
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

    // Astronaut leaves the spaceship and steps onto Mars
    public void exit() {
        onMars = true;
        System.out.println(name + " has exited the spaceship onto Mars.");
    }

    // Astronaut walks around and uses up oxygen
    public void walk() throws AstronautDownException {
        if (onMars == false) {
            System.out.println(name + " cannot walk, still inside the spaceship.");
            return;
        }

        positionX = positionX + 1;
        oxygen = oxygen - 5;

        System.out.println(name + " walked to position " + positionX + ". Oxygen left: " + oxygen);

        checkOxygen();
    }

    // Astronaut goes back inside the spaceship
    public void returnToShip() {
        onMars = false;
        System.out.println(name + " has returned to the spaceship.");
    }

    // Astronaut boards the spaceship at the very start
    public void board() {
        System.out.println(name + " has boarded the spaceship.");
    }

    // If oxygen runs out, health starts dropping. Oxygen and health are both
    // kept from going below 0. If health reaches 0, the astronaut is down
    // and a custom exception is thrown.
    private void checkOxygen() throws AstronautDownException {
        if (oxygen <= 0) {
            oxygen = 0;
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

    // This method will be overridden by Engineer and Photographer
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