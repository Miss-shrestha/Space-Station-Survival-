import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.swing.*;

public class SpaceMissionGame extends JPanel
        implements ActionListener, KeyListener {

    private final Timer timer = new Timer(16, this);
    private final Random random = new Random();

    private static final int READY = 0;
    private static final int BOARDING = 1;
    private static final int COUNTDOWN = 2;
    private static final int LAUNCHING = 3;
    private static final int SPACE = 4;
    private static final int MARS = 5;
    private static final int WIN = 6;
    private static final int LOSE = 7;
    private static final int EXPLORE = 8;      // walking on the Martian surface

    private static final int ECO_MODE = 0;
    private static final int NORMAL_MODE = 1;
    private static final int BOOST_MODE = 2;
    private int travelMode = NORMAL_MODE;
    private double currentRocketSpeed = 0;
    private double landingSpeed = 5.0;
    private boolean hardLanding = false;

    private int stage = READY;
    private int stageTimer = 0;
    private int score = 0;

    private double rocketX = 775;
    private double rocketY = 525;
    private double astronautX = 400;
    private double astronautY = 610;

    private double fuel = 100;
    private double oxygen = 100;
    private double energy = 100;
    private double equipment = 100;

    // Mission Resource Report
    private final List<ResourceReading> resourceHistory =
            new ArrayList<>();

    private int resourceSampleTimer = 0;

    private static class ResourceReading {

        private final String resourceName;
        private final double value;

        ResourceReading(String resourceName, double value) {
            this.resourceName = resourceName;
            this.value = value;
        }

        String getResourceName() {
            return resourceName;
        }

        double getValue() {
            return value;
        }
    }

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private boolean equipmentBroken = false;
    
    // Mission Control Emergency System
private boolean emergencyActive = false;
private boolean emergencyCompleted = false;
private static final double EMERGENCY_TRIGGER_X = 450;

    private String message =
            "Mars mission ready";

    private final int[] starX = new int[90];
    private final int[] starY = new int[90];

    // Generic list of Asteroid objects (replaces three parallel int arrays)
    private final List<Asteroid> asteroids = new ArrayList<>();

    private double cloud1 = -100;
    private double cloud2 = 340;
    private double cloud3 = 760;