public class FuelTank {

    private int fuel;
    private int maximumFuel;

    public FuelTank(int maximumFuel) {
        this.maximumFuel = maximumFuel;
        this.fuel = maximumFuel;
    }

    public boolean useFuel(int amount) {
        if (amount <= 0) {
            return false;
        }

        if (fuel >= amount) {
            fuel = fuel - amount;
            return true;
        }

        return false;
    }

    public void refill() {
        fuel = maximumFuel;
    }

    public void refill(int amount) {
        if (amount > 0) {
            fuel = fuel + amount;

            if (fuel > maximumFuel) {
                fuel = maximumFuel;
            }
        }
    }

    public int getFuel() {
        return fuel;
    }

    public int getMaximumFuel() {
        return maximumFuel;
    }

    public boolean isEmpty() {
        return fuel == 0;
    }
}