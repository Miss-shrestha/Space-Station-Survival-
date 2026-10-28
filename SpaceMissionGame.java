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
    private static final int FLAG_X = 1066;

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

    private String message =
            "Press ENTER to begin the Mars mission";

    private final int[] starX = new int[90];
    private final int[] starY = new int[90];

    // Generic list of Asteroid objects (replaces three parallel int arrays)
    private final List<Asteroid> asteroids = new ArrayList<>();

    private double cloud1 = -100;
    private double cloud2 = 340;
    private double cloud3 = 760;
    private double marsRocketY = -280;
    private boolean marsLanded = false;
    private int marsExitTimer = 0;
    private double marsAstronautX = 750;

    // Crew: a generic list of Astronaut objects (Sky the Engineer, Rain the Photographer)
    private final List<Astronaut> crew = new ArrayList<>();
    private Engineer engineer;
    private Photographer photographer;
    private SolarPanel panel = new SolarPanel();
    private int activeIndex = 0;
    private int walkTimer = 0;

    // Mars exploration: the State pattern context lives in SurfaceWalker
    private final SurfaceWalker walker = new SurfaceWalker();
    private boolean reachedMars = false;
    private boolean reachedExplore = false;
    private int marsStepTimer = 0;

    public SpaceMissionGame() {
        setPreferredSize(new Dimension(1100, 700));
        setFocusable(true);
        addKeyListener(this);

        for (int i = 0; i < starX.length; i++) {
            starX[i] = random.nextInt(1100);
            starY[i] = random.nextInt(700);
        }

        createCrew();
        resetAsteroids();
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window =
                    new JFrame("Space Simulation Survival");

            SpaceMissionGame game =
                    new SpaceMissionGame();

            window.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            window.add(game);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setResizable(false);
            window.setVisible(true);

            game.requestFocusInWindow();
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        updateGame();
        repaint();
    }

    // ============================================
    // RESOURCE REPORT - STREAMS AND COLLECTORS
    // ============================================

    private void collectResourceReadings() {

        resourceHistory.add(
                new ResourceReading("Fuel", fuel)
        );

        resourceHistory.add(
                new ResourceReading("Oxygen", oxygen)
        );

        resourceHistory.add(
                new ResourceReading("Energy", energy)
        );

        resourceHistory.add(
                new ResourceReading("Equipment", equipment)
        );
    }

    private Map<String, DoubleSummaryStatistics>
            createResourceReport() {

        return resourceHistory.parallelStream()
                .collect(
                        Collectors.groupingBy(
                                ResourceReading::getResourceName,
                                Collectors.summarizingDouble(
                                        ResourceReading::getValue
                                )
                        )
                );
    }

    private void drawCollectorReport(Graphics2D g) {

        if (resourceHistory.isEmpty() ||
                stage == READY) {
            return;
        }

        Map<String, DoubleSummaryStatistics> report =
                createResourceReport();

        g.setColor(new Color(10, 30, 48, 220));
        g.fillRoundRect(
                20, 100, 280, 110, 12, 12
        );

        g.setColor(Color.WHITE);
        g.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        g.drawString(
                "MISSION RESOURCE REPORT",
                35,
                123
        );

        g.setFont(
                new Font("SansSerif", Font.PLAIN, 11)
        );

        String[] names = {
                "Fuel",
                "Oxygen",
                "Energy",
                "Equipment"
        };

        for (int i = 0; i < names.length; i++) {

            DoubleSummaryStatistics stats =
                    report.get(names[i]);

            if (stats != null) {

                int column = i % 2;
                int row = i / 2;

                int x = 35 + column * 135;
                int y = 153 + row * 25;

                g.drawString(
                        names[i] + ": " +
                                String.format(
                                        "%.1f",
                                        stats.getAverage()
                                ) + "%",
                        x,
                        y
                );
            }
        }

        g.setFont(
                new Font("SansSerif", Font.PLAIN, 10)
        );

        g.drawString(
                "Samples: " + (resourceHistory.size() / 4),
                35,
                197
        );
    }

    private void createCrew() {
        crew.clear();
        engineer = new Engineer("Sky");
        photographer = new Photographer("Rain");
        crew.add(engineer);
        crew.add(photographer);
        activeIndex = 0;
    }

    private Astronaut activeAstronaut() {
        return crew.get(activeIndex);
    }

    private void updateGame() {
        cloud1 += 0.35;
        cloud2 += 0.22;
        cloud3 += 0.28;

        if (cloud1 > 1200) {
            cloud1 = -170;
        }

        if (cloud2 > 1200) {
            cloud2 = -170;
        }

        if (cloud3 > 1200) {
            cloud3 = -170;
        }

        if (stage == BOARDING) {
            if (stageTimer > 0) {
                stageTimer--;
                message = "Sky and Rain are waving before boarding";
            } else {
                astronautX += 2.2;
                astronautY += (560 - astronautY) * 0.02;
                message = "Sky and Rain are boarding the rocket";

                if (astronautX >= 675) {
                    crew.forEach(Astronaut::board);
                    stage = COUNTDOWN;
                    stageTimer = 180;
                }
            }

        } else if (stage == COUNTDOWN) {
            stageTimer--;

            message = "Launch in "
                    + Math.max(
                    1,
                    (int) Math.ceil(stageTimer / 60.0)
            );

            if (stageTimer <= 0) {
                stage = LAUNCHING;
                message = "LIFTOFF!";
            }

        } else if (stage == LAUNCHING) {
            rocketY -= 4.0;
            fuel -= 0.15;

            if (rocketY < -280) {
                crew.forEach(Astronaut::exit);
                stage = SPACE;
                rocketX = 120;
                rocketY = 350;

                message =
                        "Fly to Mars and avoid the asteroids";
            }

        } else if (stage == SPACE) {
            updateSpaceMission();

        } else if (stage == MARS) {
            updateMarsLanding();

        } else if (stage == EXPLORE) {
            updateExploration();
        }

        // Fuel no longer matters once the rocket has landed
        boolean fuelOut = fuel <= 0
                && stage != MARS
                && stage != EXPLORE;

        if ((fuelOut
                || oxygen <= 0
                || energy <= 0
                || equipment <= 0)
                && stage != WIN
                && stage != LOSE
                && stage != MARS
                && stage != READY) {

            stage = LOSE;
            message =
                    "MISSION FAILED - Press N to restart";
        }

        sampleMissionResources();
    }

    // Save a resource snapshot about once per second.
    private void sampleMissionResources() {
        if (stage != READY && stage != WIN && stage != LOSE) {
            if (++resourceSampleTimer >= 60) {
                collectResourceReadings();
                resourceSampleTimer = 0;
            }
        }
    }

    private void updateMarsLanding() {
        if (!marsLanded) {
            marsRocketY += 5.0;
            message = "Victory rocket is landing on Mars";

            if (marsRocketY >= 545) {
                marsRocketY = 545;
                marsLanded = true;
                marsExitTimer = 75;
                message = "Landing successful";
            }

        } else if (marsExitTimer > 0) {
            marsExitTimer--;
            message = "Preparing Sky and Rain for Mars exploration";

        } else if (marsAstronautX > 610) {
            marsAstronautX -= 2.0;
            message = "Sky and Rain are leaving the rocket";

        } else {
            marsAstronautX = 610;
            startMarsExploration();
        }
    }

    private void startMarsExploration() {
        stage = EXPLORE;
        reachedExplore = true;
        marsStepTimer = 0;
        walker.reset();
        message = "Walk to the flag! SPACE jumps, P takes a photo, Q switches astronaut";
    }

    private void updateExploration() {
        walker.update(left, right);

        // Moving (walking or jumping) uses oxygen; standing still does not
        if (walker.isMoving()) {
            marsStepTimer++;
            if (marsStepTimer >= 40) {
                marsStepTimer = 0;
                tickOxygen();
            }
        }

        if (stage == EXPLORE && walker.getX() >= SurfaceWalker.FLAG_REACHED_X) {
            stage = WIN;
            score = (int) (fuel + oxygen + energy + equipment)
                    + photographer.getPhotosTaken() * 5;
            message = "FLAG PLANTED! Mission score: " + score;
        }
    }

    // Oxygen comes from the real Astronaut objects. walk() throws the custom
    // AstronautDownException when health runs out, which ends the mission.
    private void tickOxygen() {
        try {
            for (Astronaut member : crew) {
                member.walk();
            }
            oxygen = activeAstronaut().getOxygen();
        } catch (AstronautDownException e) {
            stage = LOSE;
            message = "MISSION FAILED - " + e.getMessage();
        }
    }

    private void updateSpaceMission() {
        double speed =
                equipmentBroken ? 1.8 : 4.0;

        if (up && rocketY > 95) {
            rocketY -= speed;
        }

        if (down && rocketY < 640) {
            rocketY += speed;
        }

        if (left && rocketX > 45) {
            rocketX -= speed;
        }

        if (right && rocketX < 1030) {
            rocketX += speed;
        }

        if (up || down || left || right) {
            fuel -= 0.025;
        }

        // Oxygen is now drawn from the Astronaut objects every 5 seconds
        walkTimer++;
        if (walkTimer >= 300) {
            walkTimer = 0;
            tickOxygen();
        }

        energy -= 0.004;

        if (!equipmentBroken
                && random.nextInt(2400) == 0) {

            equipmentBroken = true;
            equipment -= 25;

            message =
                    "Equipment failure! Press R to repair";
        }

        // Lambda: move every asteroid
        asteroids.forEach(a -> a.move(random));

        // Stream: find every asteroid hitting the rocket
        List<Asteroid> collided = asteroids.stream()
                .filter(a -> a.hits(rocketX, rocketY))
                .collect(Collectors.toList());

        for (Asteroid a : collided) {
            equipment -= 0.8;
            energy -= 0.4;
            a.respawn(1100, 180, random);

            message =
                    "Asteroid collision! Equipment damaged";
        }

        if (rocketX > 940) {
            stage = MARS;
            reachedMars = true;

            marsRocketY = -280;
            marsLanded = false;
            marsExitTimer = 75;
            marsAstronautX = 750;

            rocketX = 775;
            rocketY = 525;

            up = false;
            down = false;
            left = false;
            right = false;

            score = (int) (
                    fuel
                            + oxygen
                            + energy
                            + equipment
            );

            message =
                    "Approaching the Mars landing site";
        }
    }

    private void resetAsteroids() {
        asteroids.clear();
        for (int i = 0; i < 10; i++) {
            asteroids.add(new Asteroid(i, random));
        }
    }

    @Override
    protected void paintComponent(
            Graphics graphics
    ) {
        super.paintComponent(graphics);

        Graphics2D g =
                (Graphics2D) graphics;

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (stage <= LAUNCHING) {
            drawEarth(g);

        } else if (stage == SPACE
                || (stage == LOSE && !reachedMars)) {

            drawSpace(g);

        } else {
            drawMars(g);
        }

        drawDashboard(g);
        drawCollectorReport(g);
    }

    private void drawEarth(Graphics2D g) {
        GradientPaint sky =
                new GradientPaint(
                        0,
                        0,
                        new Color(35, 155, 230),
                        0,
                        500,
                        new Color(185, 225, 247)
                );

        g.setPaint(sky);
        g.fillRect(0, 0, 1100, 500);

        g.setColor(new Color(255, 220, 65));
        g.fillOval(900, 110, 80, 80);

        drawCloud(g, (int) cloud1, 115, 1.0);
        drawCloud(g, (int) cloud2, 185, 0.7);
        drawCloud(g, (int) cloud3, 105, 0.9);

        g.setColor(new Color(76, 167, 105));
        g.fillOval(-150, 390, 520, 180);
        g.fillOval(260, 400, 580, 170);
        g.fillOval(710, 380, 560, 190);

        g.setColor(new Color(36, 150, 72));
        g.fillRect(0, 455, 1100, 245);

        drawFence(g);
        drawControlRoom(g);
        drawLaunchPad(g);
        drawLaunchTower(g);

        if (stage != SPACE) {
            drawRocket(
                    g,
                    rocketX,
                    rocketY,
                    stage >= COUNTDOWN
            );
        }

        if (stage == READY
                || stage == BOARDING) {

            drawAstronaut(
                    g,
                    astronautX,
                    astronautY
            );
        }
    }

    private void drawCloud(
            Graphics2D g,
            int x,
            int y,
            double size
    ) {
        g.setColor(
                new Color(255, 255, 255, 235)
        );

        g.fillOval(
                x,
                y,
                (int) (130 * size),
                (int) (45 * size)
        );

        g.fillOval(
                (int) (x - 38 * size),
                (int) (y + 7 * size),
                (int) (75 * size),
                (int) (40 * size)
        );

        g.fillOval(
                (int) (x + 40 * size),
                (int) (y + 7 * size),
                (int) (78 * size),
                (int) (42 * size)
        );

        g.fillOval(
                (int) (x - 10 * size),
                (int) (y - 25 * size),
                (int) (70 * size),
                (int) (62 * size)
        );
    }

    private void drawControlRoom(Graphics2D g) {
        // Building shadow
        g.setColor(new Color(18, 92, 67, 120));
        g.fillRoundRect(47, 500, 310, 142, 18, 18);

        // Main building
        g.setColor(new Color(224, 238, 243));
        g.fillRoundRect(55, 500, 292, 133, 18, 18);

        // Blue line underneath the roof
        g.setColor(new Color(0, 151, 206));
        g.fillRect(55, 515, 292, 14);

        // Main roof
        g.setColor(new Color(30, 52, 65));
        g.fillRoundRect(43, 476, 316, 48, 15, 15);

        // Inner roof panel
        g.setColor(new Color(22, 43, 56));
        g.fillRoundRect(50, 484, 302, 34, 9, 9);

        // Mission Control title
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g.drawString("MISSION CONTROL", 68, 507);

        // Three computer screens
        for (int i = 0; i < 3; i++) {
            int screenX = 73 + i * 66;

            // Dark screen frame
            g.setColor(new Color(28, 55, 68));
            g.fillRoundRect(screenX, 541, 56, 52, 8, 8);

            // Blue screen
            g.setColor(new Color(6, 168, 218));
            g.fillRoundRect(screenX + 6, 547, 44, 39, 5, 5);

            // Screen reflection
            g.setColor(new Color(91, 210, 240));
            g.fillRoundRect(screenX + 11, 552, 28, 5, 3, 3);

            // Information lines
            g.setColor(new Color(0, 98, 154));
            g.fillRoundRect(screenX + 11, 566, 35, 3, 2, 2);
            g.fillRoundRect(screenX + 11, 575, 25, 3, 2, 2);
        }

        // Door frame
        g.setColor(new Color(35, 61, 74));
        g.fillRoundRect(276, 537, 49, 96, 8, 8);

        // Door
        g.setColor(new Color(67, 91, 103));
        g.fillRoundRect(283, 546, 35, 87, 6, 6);

        // Door handle
        g.setColor(new Color(255, 191, 24));
        g.fillOval(307, 583, 7, 7);

        // Systems status panel
        g.setColor(new Color(38, 67, 79));
        g.fillRoundRect(67, 603, 188, 24, 8, 8);

        // Status lights
        g.setColor(new Color(29, 226, 143));
        g.fillOval(80, 611, 8, 8);

        g.setColor(new Color(255, 193, 26));
        g.fillOval(95, 611, 8, 8);

        g.setColor(new Color(255, 73, 64));
        g.fillOval(110, 611, 8, 8);

        // Systems Online text
        g.setColor(new Color(203, 222, 230));
        g.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g.drawString("SYSTEMS ONLINE", 129, 619);
    }

    private void drawFence(Graphics2D g) {
        g.setColor(new Color(190, 200, 200));
        g.setStroke(new BasicStroke(2));

        for (int x = 0;
             x < 1100;
             x += 45) {

            g.drawLine(x, 445, x, 535);
        }

        g.drawLine(0, 460, 1100, 460);
        g.drawLine(0, 490, 1100, 490);
        g.drawLine(0, 520, 1100, 520);
    }

    private void drawLaunchPad(Graphics2D g) {
        g.setColor(new Color(92, 98, 103));

        Polygon pad =
                new Polygon(
                        new int[]{590, 965, 1060, 485},
                        new int[]{555, 555, 700, 700},
                        4
                );

        g.fillPolygon(pad);

        g.setColor(new Color(175, 181, 184));
        g.fillOval(620, 520, 330, 80);

        g.setColor(new Color(70, 78, 84));
        g.fillOval(660, 535, 250, 50);

        g.setColor(new Color(250, 195, 35));
        g.setStroke(new BasicStroke(6));
        g.drawOval(637, 526, 296, 67);
    }

    private void drawLaunchTower(Graphics2D g) {
        g.setColor(new Color(65, 72, 78));
        g.setStroke(new BasicStroke(7));

        g.drawLine(730, 515, 730, 240);
        g.drawLine(820, 515, 820, 240);

        g.setStroke(new BasicStroke(3));

        for (int y = 260;
             y < 505;
             y += 40) {

            g.drawLine(730, y, 820, y + 40);
            g.drawLine(820, y, 730, y + 40);
            g.drawLine(730, y, 820, y);
        }
    }

    private void drawRocket(
            Graphics2D g,
            double x,
            double y,
            boolean fire
    ) {
        int rx = (int) x;
        int ry = (int) y;

        g.setStroke(new BasicStroke(
                2,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        // Animated rocket fire
        if (fire) {
            int movement = (int) (System.currentTimeMillis() / 80 % 14);
            int flameLength;

            if (stage == LAUNCHING) {
                flameLength = 125 + movement;
            } else {
                flameLength = 65 + movement;
            }

            // Outer red flame
            g.setColor(new Color(225, 45, 25));
            g.fillPolygon(new Polygon(
                    new int[]{rx - 28, rx, rx + 28},
                    new int[]{ry + 15, ry + flameLength, ry + 15},
                    3
            ));

            // Orange flame
            g.setColor(new Color(255, 135, 15));
            g.fillPolygon(new Polygon(
                    new int[]{rx - 20, rx, rx + 20},
                    new int[]{ry + 13, ry + flameLength - 22, ry + 13},
                    3
            ));

            // Yellow centre
            g.setColor(new Color(255, 230, 70));
            g.fillPolygon(new Polygon(
                    new int[]{rx - 10, rx, rx + 10},
                    new int[]{ry + 12, ry + flameLength - 44, ry + 12},
                    3
            ));
        }

        // Engine nozzle
        g.setColor(new Color(67, 82, 94));
        g.fillRoundRect(rx - 30, ry - 12, 60, 28, 10, 10);

        g.setColor(new Color(45, 58, 68));
        g.drawRoundRect(rx - 30, ry - 12, 60, 28, 10, 10);

        // Left fin
        Polygon leftFin = new Polygon(
                new int[]{rx - 37, rx - 66, rx - 37},
                new int[]{ry - 105, ry + 5, ry - 20},
                3
        );

        g.setColor(new Color(225, 0, 55));
        g.fillPolygon(leftFin);

        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(48, 61, 71));
        g.drawPolygon(leftFin);

        // Right fin
        Polygon rightFin = new Polygon(
                new int[]{rx + 37, rx + 66, rx + 37},
                new int[]{ry - 105, ry + 5, ry - 20},
                3
        );

        g.setColor(new Color(225, 0, 55));
        g.fillPolygon(rightFin);

        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(48, 61, 71));
        g.drawPolygon(rightFin);

        // Main rocket body
        g.setColor(new Color(234, 241, 245));
        g.fillRoundRect(rx - 38, ry - 230, 76, 225, 30, 30);

        // Light highlight on the body
        g.setColor(new Color(250, 253, 255, 150));
        g.fillRoundRect(rx - 29, ry - 222, 20, 198, 16, 16);

        // Body outline
        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(48, 61, 71));
        g.drawRoundRect(rx - 38, ry - 230, 76, 225, 30, 30);

        // Pointed nose
        Polygon nose = new Polygon(
                new int[]{rx - 38, rx, rx + 38},
                new int[]{ry - 225, ry - 320, ry - 225},
                3
        );

        g.setColor(new Color(247, 250, 252));
        g.fillPolygon(nose);

        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(48, 61, 71));
        g.drawPolygon(nose);

        // Red stripe
        g.setColor(new Color(225, 0, 55));
        g.fillRect(rx - 35, ry - 91, 70, 31);

        // Window outer frame
        g.setColor(new Color(16, 55, 76));
        g.fillOval(rx - 32, ry - 196, 64, 64);

        // Blue glass
        g.setColor(new Color(0, 160, 215));
        g.fillOval(rx - 25, ry - 189, 50, 50);

        // Darker glass shading
        g.setColor(new Color(0, 113, 183));
        g.fillArc(rx - 25, ry - 189, 50, 50, 210, 150);

        // Window reflection
        g.setColor(new Color(190, 239, 253));
        g.fillOval(rx - 18, ry - 183, 18, 13);

        // Victory badge
        g.setColor(new Color(0, 91, 172));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        String rocketName = "VICTORY";
        FontMetrics nameMetrics = g.getFontMetrics();

        g.drawString(
                rocketName,
                rx - nameMetrics.stringWidth(rocketName) / 2,
                ry - 104
        );

        // Small bolts near the engine
        g.setColor(new Color(108, 128, 139));
        g.fillOval(rx - 31, ry - 43, 5, 5);
        g.fillOval(rx + 26, ry - 43, 5, 5);

        // Restore normal stroke for the other drawings
        g.setStroke(new BasicStroke(1));
    }

    private void drawAstronaut(
            Graphics2D g,
            double x,
            double y
    ) {
        int startX = (int) x;
        int ay = (int) y;

        boolean strolling =
                (stage == BOARDING && stageTimer <= 0)
                        || (stage == EXPLORE && walker.isMoving());

        int walk = strolling
                ? (int) (Math.sin(System.currentTimeMillis() * 0.012) * 4)
                : 0;

        int wave = stage == BOARDING && stageTimer > 0
                ? (int) (Math.sin(System.currentTimeMillis() * 0.018) * 7)
                : 0;

        String[] names = {"Sky", "Rain"};
        Color[] colours = {
                new Color(0, 157, 220),
                new Color(255, 85, 40)
        };

        for (int i = 0; i < 2; i++) {
            int ax = startX + i * 62;
            Color suitColour = colours[i];

            // Arms
            g.setColor(new Color(245, 248, 250));
            g.setStroke(new BasicStroke(
                    11,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            if (i == 0) {
                // Sky waves only the outside left hand
                if (stage == BOARDING && stageTimer > 0) {
                    g.drawLine(ax - 17, ay - 38, ax - 32 + wave, ay - 70);
                } else {
                    g.drawLine(ax - 17, ay - 38, ax - 29, ay - 11);
                }

                // Sky's inside hand stays joined
                g.drawLine(ax + 17, ay - 38, ax + 31, ay - 11);

            } else {
                // Rain's inside hand stays joined
                g.drawLine(ax - 17, ay - 38, ax - 31, ay - 11);

                // Rain waves only the outside right hand
                if (stage == BOARDING && stageTimer > 0) {
                    g.drawLine(ax + 17, ay - 38, ax + 32 + wave, ay - 70);
                } else {
                    g.drawLine(ax + 17, ay - 38, ax + 29, ay - 11);
                }
            }

            // Spacesuit body
            g.setColor(new Color(245, 248, 250));
            g.fillRoundRect(ax - 22, ay - 50, 44, 58, 13, 13);

            // Helmet
            g.fillOval(ax - 25, ay - 94, 50, 50);

            // Dark helmet frame
            g.setColor(new Color(31, 51, 62));
            g.fillOval(ax - 20, ay - 89, 40, 40);

            // Coloured visor
            g.setColor(suitColour);
            g.fillOval(ax - 15, ay - 84, 30, 30);

            // Visor reflection
            g.setColor(new Color(255, 255, 255, 165));
            g.fillOval(ax - 11, ay - 81, 12, 7);

            // Chest panel
            g.setColor(new Color(44, 61, 72));
            g.fillRoundRect(ax - 14, ay - 39, 28, 21, 5, 5);

            // Coloured belt
            g.setColor(suitColour);
            g.fillRoundRect(ax - 19, ay - 8, 38, 11, 4, 4);

            // Legs
            g.setColor(new Color(245, 248, 250));

            g.fillRoundRect(ax - 18 + walk, ay + 4, 13, 35, 8, 8);
            g.fillRoundRect(ax + 5 - walk, ay + 4, 13, 35, 8, 8);

            // Name centred on chest panel
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 9));
            FontMetrics nameMetrics = g.getFontMetrics();
            g.drawString(
                    names[i],
                    ax - nameMetrics.stringWidth(names[i]) / 2,
                    ay - 25
            );
        }

        g.setStroke(new BasicStroke(1));
    }

    // Small yellow arrow above the astronaut the player is controlling
    private void drawActiveMarker(Graphics2D g) {
        int centreX = (int) walker.getX() + activeIndex * 62;
        int top = (int) walker.getY() - 118;

        g.setColor(new Color(255, 214, 40));
        g.fillPolygon(new Polygon(
                new int[]{centreX - 9, centreX + 9, centreX},
                new int[]{top, top, top + 13},
                3
        ));
    }

    private void drawSpace(Graphics2D g) {
        // Dark blue and black galaxy background
        GradientPaint galaxy = new GradientPaint(
                0, 0, new Color(2, 5, 18),
                1100, 700, new Color(7, 31, 73)
        );
        g.setPaint(galaxy);
        g.fillRect(0, 0, 1100, 700);

        // Stars
        for (int i = 0; i < starX.length; i++) {
            if (i % 7 == 0) {
                g.setColor(new Color(80, 210, 255));
            } else {
                g.setColor(Color.WHITE);
            }

            int size = 1 + i % 3;
            g.fillOval(starX[i], starY[i], size, size);
        }

        // Rotation value for both planets
        double spin = System.currentTimeMillis() * 0.0015;

        // Blue planet
        int bluePlanetX = 165;
        int bluePlanetY = 190;

        // Blue atmospheric glow
        g.setColor(new Color(40, 190, 255, 55));
        g.fillOval(bluePlanetX - 45, bluePlanetY - 45, 90, 90);

        // Shaded blue planet
        RadialGradientPaint bluePlanetPaint = new RadialGradientPaint(
                bluePlanetX - 12,
                bluePlanetY - 15,
                55,
                new float[]{0.0f, 0.65f, 1.0f},
                new Color[]{
                        new Color(75, 220, 255),
                        new Color(0, 145, 220),
                        new Color(0, 55, 135)
                }
        );
        g.setPaint(bluePlanetPaint);
        g.fillOval(bluePlanetX - 37, bluePlanetY - 37, 74, 74);

        g.setColor(new Color(35, 225, 175));
        g.fillOval(
                bluePlanetX + (int) (Math.cos(spin) * 19) - 8,
                bluePlanetY + (int) (Math.sin(spin) * 10) - 5,
                16,
                10
        );
        g.fillOval(
                bluePlanetX + (int) (Math.cos(spin + 2.5) * 22) - 6,
                bluePlanetY + (int) (Math.sin(spin + 2.5) * 13) - 4,
                12,
                8
        );

        g.setColor(new Color(80, 200, 255));
        g.setStroke(new BasicStroke(2));
        g.drawOval(bluePlanetX - 37, bluePlanetY - 37, 74, 74);

        // Detailed rotating purple planet
        int purplePlanetX = 695;
        int purplePlanetY = 205;

        // Outer glow layers
        g.setColor(new Color(125, 55, 210, 25));
        g.fillOval(purplePlanetX - 54, purplePlanetY - 54, 108, 108);

        g.setColor(new Color(190, 90, 255, 40));
        g.fillOval(purplePlanetX - 45, purplePlanetY - 45, 90, 90);

        // Back section of the ring
        Graphics2D backRing = (Graphics2D) g.create();
        backRing.rotate(-0.38, purplePlanetX, purplePlanetY);
        backRing.setColor(new Color(150, 90, 35));
        backRing.setStroke(new BasicStroke(
                10,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        backRing.drawArc(
                purplePlanetX - 55,
                purplePlanetY - 19,
                110,
                38,
                0,
                180
        );
        backRing.dispose();

        // Shaded planet body
        RadialGradientPaint purplePlanetPaint =
                new RadialGradientPaint(
                        purplePlanetX - 13,
                        purplePlanetY - 15,
                        48,
                        new float[]{0.0f, 0.55f, 1.0f},
                        new Color[]{
                                new Color(245, 145, 255),
                                new Color(158, 65, 225),
                                new Color(55, 22, 115)
                        }
                );

        g.setPaint(purplePlanetPaint);
        g.fillOval(purplePlanetX - 32, purplePlanetY - 32, 64, 64);

        // Curved atmospheric bands
        g.setColor(new Color(255, 185, 245, 100));
        g.setStroke(new BasicStroke(4));
        g.drawArc(
                purplePlanetX - 25,
                purplePlanetY - 18,
                50,
                25,
                195,
                145
        );

        g.setColor(new Color(75, 25, 145, 120));
        g.drawArc(
                purplePlanetX - 26,
                purplePlanetY - 3,
                52,
                23,
                20,
                145
        );

        // Rotating surface markings
        int purpleSpotX =
                purplePlanetX + (int) (Math.cos(-spin) * 17);

        int purpleSpotY =
                purplePlanetY + (int) (Math.sin(-spin) * 9);

        g.setColor(new Color(255, 175, 235, 190));
        g.fillOval(purpleSpotX - 7, purpleSpotY - 5, 14, 10);

        int smallSpotX =
                purplePlanetX + (int) (Math.cos(-spin + 2.5) * 20);

        int smallSpotY =
                purplePlanetY + (int) (Math.sin(-spin + 2.5) * 12);

        g.setColor(new Color(95, 35, 165, 170));
        g.fillOval(smallSpotX - 4, smallSpotY - 3, 8, 6);

        // Bright front section of the ring
        Graphics2D frontRing = (Graphics2D) g.create();
        frontRing.rotate(-0.38, purplePlanetX, purplePlanetY);
        frontRing.setColor(new Color(245, 165, 45));
        frontRing.setStroke(new BasicStroke(
                8,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        frontRing.drawArc(
                purplePlanetX - 55,
                purplePlanetY - 19,
                110,
                38,
                180,
                180
        );
        frontRing.dispose();

        // Thin atmospheric outline
        g.setColor(new Color(220, 145, 255, 170));
        g.setStroke(new BasicStroke(2));
        g.drawOval(purplePlanetX - 32, purplePlanetY - 32, 64, 64);

        g.setStroke(new BasicStroke(1));

        // Mars destination
        drawMarsDestination(g);

        // Asteroids with different shapes
        for (int i = 0; i < asteroids.size(); i++) {
            Asteroid asteroid = asteroids.get(i);
            int size = asteroid.getSize();
            int centreX = (int) asteroid.getX();
            int centreY = (int) asteroid.getY();

            if (i % 3 == 0) {
                // Jagged asteroid
                int[] xPoints = {
                        centreX - size / 2,
                        centreX - size / 5,
                        centreX + size / 4,
                        centreX + size / 2,
                        centreX + size / 3,
                        centreX,
                        centreX - size / 3
                };

                int[] yPoints = {
                        centreY,
                        centreY - size / 2,
                        centreY - size / 3,
                        centreY,
                        centreY + size / 3,
                        centreY + size / 2,
                        centreY + size / 3
                };

                g.setColor(new Color(125, 105, 105));
                g.fillPolygon(xPoints, yPoints, xPoints.length);

            } else if (i % 3 == 1) {
                // Round asteroid
                g.setColor(new Color(105, 125, 145));
                g.fillOval(
                        centreX - size / 2,
                        centreY - size / 2,
                        size,
                        size
                );

            } else {
                // Triangular asteroid
                g.setColor(new Color(130, 95, 150));
                g.fillPolygon(new Polygon(
                        new int[]{
                                centreX,
                                centreX - size / 2,
                                centreX + size / 2
                        },
                        new int[]{
                                centreY - size / 2,
                                centreY + size / 2,
                                centreY + size / 3
                        },
                        3
                ));
            }

            // Asteroid crater
            g.setColor(new Color(55, 65, 80, 180));
            g.fillOval(
                    centreX - size / 7,
                    centreY - size / 7,
                    Math.max(5, size / 4),
                    Math.max(4, size / 5)
            );
        }

        // Player rocket
        if (stage != WIN && stage != LOSE) {
            drawSpaceRocket(
                    g,
                    (int) rocketX,
                    (int) rocketY
            );
        }

        if (stage == LOSE) {
            drawCentreMessage(
                    g,
                    "MISSION FAILED",
                    "A resource reached zero. Press N to retry"
            );
        }
    }

    private void drawMarsDestination(Graphics2D g) {
        g.setColor(new Color(95, 30, 42, 90));
        g.fillOval(925, 225, 290, 290);

        g.setColor(new Color(191, 72, 45));
        g.fillOval(960, 260, 210, 210);

        g.setColor(new Color(225, 105, 65, 120));
        g.fillOval(985, 280, 110, 95);

        g.setColor(new Color(125, 46, 35));
        g.fillOval(1000, 300, 48, 28);
        g.fillOval(1045, 390, 35, 24);
    }

    private void drawSpaceRocket(
            Graphics2D g,
            int x,
            int y
    ) {
        // Outer flame
        g.setColor(new Color(255, 100, 20));
        g.fillPolygon(new Polygon(
                new int[]{x - 34, x - 76, x - 34},
                new int[]{y - 15, y, y + 15},
                3
        ));

        // Inner flame
        g.setColor(new Color(255, 220, 55));
        g.fillPolygon(new Polygon(
                new int[]{x - 34, x - 62, x - 34},
                new int[]{y - 8, y, y + 8},
                3
        ));

        // Upper fin
        Polygon upperFin = new Polygon(
                new int[]{x - 23, x - 48, x - 18},
                new int[]{y - 17, y - 43, y - 9},
                3
        );
        g.setColor(new Color(220, 35, 65));
        g.fillPolygon(upperFin);
        g.setColor(new Color(35, 52, 67));
        g.setStroke(new BasicStroke(3));
        g.drawPolygon(upperFin);

        // Lower fin
        Polygon lowerFin = new Polygon(
                new int[]{x - 23, x - 48, x - 18},
                new int[]{y + 17, y + 43, y + 9},
                3
        );
        g.setColor(new Color(220, 35, 65));
        g.fillPolygon(lowerFin);
        g.setColor(new Color(35, 52, 67));
        g.drawPolygon(lowerFin);

        // Rocket body with pointed nose
        Polygon body = new Polygon(
                new int[]{x - 35, x + 25, x + 55, x + 25, x - 35},
                new int[]{y - 21, y - 21, y, y + 21, y + 21},
                5
        );

        g.setColor(new Color(240, 246, 248));
        g.fillPolygon(body);
        g.setColor(new Color(35, 52, 67));
        g.setStroke(new BasicStroke(3));
        g.drawPolygon(body);

        // Red rear section
        g.setColor(new Color(220, 35, 65));
        g.fillRect(x - 35, y - 18, 12, 36);

        // Rocket window frame
        g.setColor(new Color(26, 48, 62));
        g.fillOval(x - 2, y - 17, 34, 34);

        // Blue glass
        g.setColor(new Color(0, 166, 220));
        g.fillOval(x + 3, y - 12, 24, 24);

        // Window reflection
        g.setColor(new Color(215, 250, 255));
        g.fillOval(x + 8, y - 9, 8, 7);

        g.setStroke(new BasicStroke(1));
    }

    private void drawMars(Graphics2D g) {
        GradientPaint marsSky =
                new GradientPaint(
                        0,
                        0,
                        new Color(30, 14, 34),
                        0,
                        500,
                        new Color(128, 49, 50)
                );

        g.setPaint(marsSky);
        g.fillRect(0, 0, 1100, 700);

        g.setColor(
                new Color(255, 245, 230, 180)
        );

        for (int i = 0;
             i < starX.length;
             i++) {

            int starSize = 1 + i % 2;

            g.fillOval(
                    starX[i],
                    starY[i] / 2,
                    starSize,
                    starSize
            );
        }

        drawMarsMoons(g);
        drawMarsMountains(g);

        g.setColor(new Color(150, 55, 43));
        g.fillRect(0, 455, 1100, 245);

        Polygon foreground =
                new Polygon(
                        new int[]{
                                0, 150, 310, 470, 650,
                                820, 980, 1100, 1100, 0
                        },
                        new int[]{
                                590, 550, 600, 565, 605,
                                558, 600, 570, 700, 700
                        },
                        10
                );

        g.setColor(new Color(189, 75, 48));
        g.fillPolygon(foreground);

        drawMarsCrater(g, 150, 575, 150, 38);
        drawMarsCrater(g, 920, 610, 190, 48);
        drawMarsCrater(g, 340, 660, 100, 27);
        drawMarsCrater(g, 680, 645, 115, 30);

        drawMarsRock(g, 75, 625, 36, 25);
        drawMarsRock(g, 245, 530, 29, 20);
        drawMarsRock(g, 470, 630, 38, 26);
        drawMarsRock(g, 1015, 545, 46, 30);
        drawMarsRock(g, 855, 675, 31, 21);

        drawMarsLandingPlatform(g);

        // Rocket descends onto the Mars landing platform
        drawRocket(
                g,
                770,
                marsRocketY,
                !marsLanded
        );

        // Fading footprints left by the crew while exploring
        for (Footprint f : walker.getFootprints()) {
            int alpha = (int) (200 * f.getOpacity());
            g.setColor(new Color(85, 38, 26, Math.max(0, Math.min(255, alpha))));
            g.fillOval((int) f.getX() - 7, (int) f.getY(), 14, 5);
        }

        // Flag at the end of the exploration route
        g.setColor(new Color(225, 228, 230));
        g.setStroke(new BasicStroke(4));
        g.drawLine(FLAG_X, 562, FLAG_X, 656);
        g.setColor(new Color(210, 40, 45));
        g.fillPolygon(new Polygon(
                new int[]{FLAG_X, FLAG_X + 28, FLAG_X},
                new int[]{564, 587, 610},
                3
        ));
        g.setStroke(new BasicStroke(1));

        // Astronauts appear only after landing and a short pause
        if (marsLanded && marsExitTimer <= 0) {
            if (reachedExplore) {
                drawAstronaut(g, walker.getX(), walker.getY());
            } else {
                drawAstronaut(g, marsAstronautX, 610);
            }
        }

        if (stage == EXPLORE) {
            drawActiveMarker(g);
        }

        if (stage == WIN) {
            drawCentreMessage(
                    g,
                    "MISSION ACCOMPLISHED",
                    "You planted the flag on Mars! Press N to play again"
            );
        } else if (stage == LOSE) {
            drawCentreMessage(
                    g,
                    "MISSION FAILED",
                    "A resource reached zero. Press N to retry"
            );
        }
    }

    private void drawMarsMoons(Graphics2D g) {
        // Large moon with a soft glow
        g.setColor(new Color(255, 205, 150, 25));
        g.fillOval(845, 110, 105, 105);

        g.setColor(new Color(226, 190, 165));
        g.fillOval(860, 125, 75, 75);

        g.setColor(new Color(245, 218, 194));
        g.fillOval(870, 134, 28, 16);

        g.setColor(new Color(164, 116, 108));
        g.fillOval(887, 151, 28, 20);

        // Small distant planet
        g.setColor(new Color(115, 170, 185, 28));
        g.fillOval(963, 212, 62, 62);

        g.setColor(new Color(125, 177, 188));
        g.fillOval(975, 224, 38, 38);

        g.setColor(new Color(78, 123, 139));
        g.fillArc(978, 233, 32, 19, 180, 180);

        // Thin ring around the distant planet
        g.setColor(new Color(205, 190, 160, 170));
        g.setStroke(new BasicStroke(2));
        g.drawOval(967, 237, 54, 14);

        g.setStroke(new BasicStroke(1));
    }

    private void drawMarsMountains(Graphics2D g) {
        Polygon backMountains =
                new Polygon(
                        new int[]{
                                0, 130, 250, 390, 520,
                                660, 810, 940, 1100
                        },
                        new int[]{
                                470, 300, 455, 270, 460,
                                315, 455, 285, 470
                        },
                        9
                );

        g.setColor(new Color(99, 39, 47));
        g.fillPolygon(backMountains);

        g.setColor(new Color(157, 65, 56));

        Polygon leftHighlight =
                new Polygon(
                        new int[]{130, 250, 195},
                        new int[]{300, 455, 380},
                        3
                );

        g.fillPolygon(leftHighlight);

        Polygon middleHighlight =
                new Polygon(
                        new int[]{390, 520, 458},
                        new int[]{270, 460, 370},
                        3
                );

        g.fillPolygon(middleHighlight);

        Polygon rightHighlight =
                new Polygon(
                        new int[]{940, 1100, 1015},
                        new int[]{285, 470, 382},
                        3
                );

        g.fillPolygon(rightHighlight);

        g.setColor(
                new Color(67, 29, 40, 150)
        );

        g.fillPolygon(
                new Polygon(
                        new int[]{0, 130, 75},
                        new int[]{470, 300, 470},
                        3
                )
        );

        g.fillPolygon(
                new Polygon(
                        new int[]{660, 810, 735},
                        new int[]{315, 455, 455},
                        3
                )
        );
    }

    private void drawMarsCrater(
            Graphics2D g,
            int x,
            int y,
            int craterWidth,
            int craterHeight
    ) {
        g.setColor(
                new Color(99, 37, 36, 180)
        );

        g.fillOval(
                x - craterWidth / 2,
                y - craterHeight / 2,
                craterWidth,
                craterHeight
        );

        g.setColor(
                new Color(222, 104, 64, 115)
        );

        g.setStroke(new BasicStroke(3));

        g.drawOval(
                x - craterWidth / 2,
                y - craterHeight / 2,
                craterWidth,
                craterHeight
        );

        g.setColor(
                new Color(69, 29, 33, 110)
        );

        g.fillOval(
                x - craterWidth / 3,
                y - craterHeight / 4,
                craterWidth * 2 / 3,
                craterHeight / 2
        );
    }

    private void drawMarsRock(
            Graphics2D g,
            int x,
            int y,
            int rockWidth,
            int rockHeight
    ) {
        Polygon rock =
                new Polygon(
                        new int[]{
                                x - rockWidth / 2,
                                x - rockWidth / 4,
                                x + rockWidth / 4,
                                x + rockWidth / 2,
                                x + rockWidth / 3,
                                x - rockWidth / 3
                        },
                        new int[]{
                                y,
                                y - rockHeight,
                                y - rockHeight + 3,
                                y,
                                y + 4,
                                y + 4
                        },
                        6
                );

        g.setColor(new Color(104, 47, 43));
        g.fillPolygon(rock);

        g.setColor(
                new Color(221, 112, 68, 145)
        );

        g.setStroke(new BasicStroke(2));

        g.drawLine(
                x - rockWidth / 4,
                y - rockHeight + 3,
                x + rockWidth / 4,
                y - rockHeight + 6
        );
    }

    private void drawMarsLandingPlatform(
            Graphics2D g
    ) {
        g.setColor(
                new Color(54, 30, 32, 130)
        );

        g.fillOval(610, 530, 320, 80);

        g.setColor(new Color(94, 88, 87));

        g.fillOval(625, 520, 290, 67);

        g.setColor(new Color(43, 43, 47));

        g.fillOval(662, 535, 215, 40);

        g.setColor(new Color(236, 178, 64));
        g.setStroke(new BasicStroke(5));

        g.drawOval(635, 525, 270, 58);

        g.setColor(new Color(100, 225, 210));

        for (int x = 655;
             x <= 885;
             x += 46) {

            g.fillOval(x, 550, 7, 7);
        }
    }

    private void drawDashboard(Graphics2D g) {
        g.translate(0, -14);
        g.setColor(
                new Color(10, 30, 48, 225)
        );

        g.fillRoundRect(
                20,
                18,
                1060,
                72,
                16,
                16
        );

        g.setColor(Color.WHITE);

        // Smaller title prevents overlap with the fuel bar.
        g.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        18
                )
        );

        g.drawString(
                "SPACE SIMULATION SURVIVAL",
                42,
                49
        );

        g.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        // Status message (needed for photo, switch and repair feedback)
        g.drawString(message, 42, 74);

        drawBar(
                g,
                350,
                35,
                "FUEL",
                fuel,
                new Color(245, 170, 35)
        );

        drawBar(
                g,
                535,
                35,
                "OXYGEN",
                oxygen,
                new Color(70, 200, 235)
        );

        drawBar(
                g,
                720,
                35,
                "ENERGY",
                energy,
                new Color(95, 220, 125)
        );

        drawBar(
                g,
                905,
                35,
                "EQUIPMENT",
                equipment,
                new Color(205, 125, 230)
        );

        g.setColor(
                new Color(10, 30, 48, 215)
        );

        g.fillRoundRect(
                20,
                669,
                460,
                34,
                12,
                12
        );

        g.setColor(Color.WHITE);

        g.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        String controls;

        if (stage < SPACE) {
            controls =
                    "ENTER: Start     N: Restart";

        } else if (stage == SPACE) {
            controls =
                    "ARROWS: Fly   Q: Switch   R: Repair   P: Photo   N: Restart";

        } else if (stage == EXPLORE) {
            controls =
                    "ARROWS: Walk   SPACE: Jump   Q: Switch   P: Photo   N: Restart";

        } else if (stage == MARS) {
            controls =
                    "MARS LANDED     N: Restart";

        } else {
            controls =
                    "N: Restart";
        }

        g.drawString(
                controls,
                35,
                690
        );

        // Crew panel: who is active, photo count and (on Mars) movement state
        if (stage >= SPACE) {
            Astronaut active = activeAstronaut();
            String role = active == engineer ? "Engineer" : "Photographer";
            String stateText = stage == EXPLORE
                    ? "   State: " + walker.getStateName()
                    : "";

            g.setColor(
                    new Color(10, 30, 48, 215)
            );
            g.fillRoundRect(500, 669, 580, 34, 12, 12);

            g.setColor(Color.WHITE);
            g.drawString(
                    "Active: " + active.getName() + " (" + role + ")   Photos: "
                            + photographer.getPhotosTaken() + stateText,
                    518,
                    690
            );
        }

        g.translate(0, 14);
    }

    private void drawBar(
            Graphics2D g,
            int x,
            int y,
            String label,
            double value,
            Color colour
    ) {
        value =
                Math.max(
                        0,
                        Math.min(100, value)
                );

        g.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        11
                )
        );

        g.setColor(Color.WHITE);
        g.drawString(label, x, y - 5);

        g.setColor(new Color(55, 70, 80));

        g.fillRoundRect(
                x,
                y,
                145,
                14,
                8,
                8
        );

        g.setColor(colour);

        g.fillRoundRect(
                x,
                y,
                (int) (145 * value / 100),
                14,
                8,
                8
        );

        g.setColor(Color.WHITE);

        g.drawString(
                (int) value + "%",
                x + 55,
                y + 12
        );
    }

    private void drawCentreMessage(
            Graphics2D g,
            String title,
            String subtitle
    ) {
        g.setColor(
                new Color(8, 18, 36, 225)
        );

        g.fillRoundRect(
                280,
                260,
                540,
                150,
                22,
                22
        );

        g.setColor(Color.WHITE);

        g.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        34
                )
        );

        FontMetrics titleMetrics =
                g.getFontMetrics();

        g.drawString(
                title,
                550
                        - titleMetrics.stringWidth(title)
                        / 2,
                320
        );

        g.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        17
                )
        );

        FontMetrics subtitleMetrics =
                g.getFontMetrics();

        g.drawString(
                subtitle,
                550
                        - subtitleMetrics
                        .stringWidth(subtitle)
                        / 2,
                365
        );
    }

    private void restartGame() {
        resourceHistory.clear();
        resourceSampleTimer = 0;

        stage = READY;
        stageTimer = 0;
        score = 0;

        rocketX = 770;
        rocketY = 545;

        astronautX = 400;
        astronautY = 610;

        fuel = 100;
        oxygen = 100;
        energy = 100;
        equipment = 100;

        equipmentBroken = false;

        up = false;
        down = false;
        left = false;
        right = false;

        message =
                "Press ENTER to begin the Mars mission";

        marsRocketY = -280;
        marsLanded = false;
        marsExitTimer = 0;
        marsAstronautX = 750;
        reachedMars = false;
        reachedExplore = false;
        marsStepTimer = 0;
        walkTimer = 0;
        walker.reset();

        createCrew();
        panel = new SolarPanel();
        resetAsteroids();
        requestFocusInWindow();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int pressedKey =
                event.getKeyCode();

        if (pressedKey == KeyEvent.VK_ENTER
                && stage == READY) {

            stage = BOARDING;
            stageTimer = 100;
            message = "Sky and Rain are waving before boarding";
        }

        if (pressedKey == KeyEvent.VK_UP) {
            up = true;
        }

        if (pressedKey == KeyEvent.VK_DOWN) {
            down = true;
        }

        if (pressedKey == KeyEvent.VK_LEFT) {
            left = true;
        }

        if (pressedKey == KeyEvent.VK_RIGHT) {
            right = true;
        }

        // SPACE: jump on the Martian surface
        if (pressedKey == KeyEvent.VK_SPACE
                && stage == EXPLORE) {
            walker.jump();
        }

        // Q: switch which astronaut you control
        if (pressedKey == KeyEvent.VK_Q
                && stage >= SPACE) {
            activeIndex = (activeIndex + 1) % crew.size();
            oxygen = activeAstronaut().getOxygen();
            message = "Now controlling " + activeAstronaut().getName();
        }

        // R: only the Engineer can repair
        
        if (pressedKey == KeyEvent.VK_R && stage == SPACE) {

            if (activeAstronaut() != engineer) {
                message = "Press Q to select Sky (Engineer)";

            } else if (equipment >= 100) {
                message = "Equipment is already fully repaired";

            } else if (energy < 15) {
                message = "Not enough energy to repair";

            } else {
                energy -= 15;

                engineer.repairEquipment(panel);

                equipment = Math.min(100, equipment + 35);
                equipmentBroken = equipment <= 0;

                message = "Repair successful! Equipment: "
                        + (int) equipment + "%";
            }
        }
        // P: only the Photographer can take photos
        if (pressedKey == KeyEvent.VK_P
                && (stage == SPACE || stage == EXPLORE)) {

            if (activeAstronaut() != photographer) {
                message = "Switch to " + photographer.getName()
                        + " (Q) to take photos";

            } else if (energy >= 2) {
                energy -= 2;

                String subject;
                int photoX;
                int photoY;

                if (stage == EXPLORE) {
                    subject = "the Martian surface";
                    photoX = (int) walker.getX();
                    photoY = (int) walker.getY();
                } else {
                    subject = asteroids.stream()
                            .anyMatch(a -> Math.abs(a.getX() - rocketX) < 250)
                            ? "an asteroid field" : "deep space";
                    photoX = (int) rocketX;
                    photoY = (int) rocketY;
                }

                photographer.takePhoto(subject, photoX, photoY);
                message = photographer.getName() + " photographed " + subject
                        + " (photos: " + photographer.getPhotosTaken() + ")";

            } else {
                message = "Not enough energy to take a photo";
            }
        }

        if (pressedKey == KeyEvent.VK_N) {
            restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        int releasedKey =
                event.getKeyCode();

        if (releasedKey == KeyEvent.VK_UP) {
            up = false;
        }

        if (releasedKey == KeyEvent.VK_DOWN) {
            down = false;
        }

        if (releasedKey == KeyEvent.VK_LEFT) {
            left = false;
        }

        if (releasedKey == KeyEvent.VK_RIGHT) {
            right = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }
}