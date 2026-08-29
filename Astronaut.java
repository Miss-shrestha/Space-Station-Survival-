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
            health = health -