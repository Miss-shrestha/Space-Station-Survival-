public class SpaceMission {

    private MissionState state;
    private int fuel;
    private int oxygen;
    private int energy;
    private int timerCount;

    public SpaceMission() {
        state = MissionState.EARTH;
        fuel = 100;
        oxygen = 100;
        energy = 100;
        timerCount = 0;
    }

    public void update() {
        timerCount++;

        switch (state) {
            case EARTH:
                if (timerCount >= 100) {
                    changeState(MissionState.LAUNCHING);
                }
                break;

            case LAUNCHING:
                decreaseFuel();

                if (timerCount >= 120) {
                    changeState(MissionState.SPACE);
                }
                break;

            case SPACE:
                decreaseEnergy();

                if (timerCount >= 150) {
                    changeState(MissionState.LANDING);
                }
                break;

            case LANDING:
                decreaseFuel();

                if (timerCount >= 80) {
                    changeState(MissionState.EXPLORING);
                }
                break;

            case EXPLORING:
                decreaseOxygen();

                if (timerCount >= 200 || oxygen == 0) {
                    changeState(MissionState.COMPLETE);
                }
                break;

            case COMPLETE:
                break;
        }
    }

    private void changeState(MissionState newState) {
        state = newState;
        timerCount = 0;
    }

    private void decreaseFuel() {
        if (fuel > 0) {
            fuel--;
        }
    }

    private void decreaseOxygen() {
        if (oxygen > 0) {
            oxygen--;
        }
    }

    private void decreaseEnergy() {
        if (energy > 0) {
            energy--;
        }
    }

    public MissionState getState() {
        return state;
    }

    public int getFuel() {
        return fuel;
    }

    public int getOxygen() {
        return oxygen;
    }

    public int getEnergy() {
        return energy;
    }
}