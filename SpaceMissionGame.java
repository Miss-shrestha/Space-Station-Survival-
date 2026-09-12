import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SpaceMissionGame extends JPanel implements ActionListener, KeyListener {
    private final Timer timer = new Timer(16, this);
    private final Random random = new Random();

    private static final int READY = 0;
    private static final int BOARDING = 1;
    private static final int COUNTDOWN = 2;
    private static final int LAUNCHING = 3;
    private static final int SPACE = 4;
    private static final int WIN = 5;
    private static final int LOSE = 6;

    private int stage = READY;
    private int stageTimer = 0;
    private int score = 0;

    private double rocketX = 770;
    private double rocketY = 545;
    private double astronautX = 350;
    private double astronautY = 610;

    private double fuel = 100;
    private double oxygen = 100;
    private double energy = 100;
    private double equipment = 100;

    private boolean up, down, left, right;
    private boolean equipmentBroken = false;
    private String message = "Press ENTER to begin the Mars mission";

    private final int[] starX = new int[90];
    private final int[] starY = new int[90];
    private final int[] asteroidX = new int[7];
    private final int[] asteroidY = new int[7];
    private final int[] asteroidSize = new int[7];

    private double cloud1 = -100;
    private double cloud2 = 340;
    private double cloud3 = 760;

    public SpaceMissionGame() {
        setPreferredSize(new Dimension(1100, 700));
        setFocusable(true);
        addKeyListener(this);

        for (int i = 0; i < starX.length; i++) {
            starX[i] = random.nextInt(1100);
            starY[i] = random.nextInt(700);
        }

        resetAsteroids();
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Mars Mission Simulation");
            SpaceMissionGame game = new SpaceMissionGame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    private void updateGame() {
        cloud1 += 0.35;
        cloud2 += 0.22;
        cloud3 += 0.28;
        if (cloud1 > 1200) cloud1 = -170;
        if (cloud2 > 1200) cloud2 = -170;
        if (cloud3 > 1200) cloud3 = -170;

        if (stage == BOARDING) {
            astronautX += 2.2;
            astronautY += (560 - astronautY) * 0.02;
            message = "Astronaut is boarding the rocket";
            if (astronautX >= 675) {
                stage = COUNTDOWN;
                stageTimer = 180;
            }
        } else if (stage == COUNTDOWN) {
            stageTimer--;
            message = "Launch in " + Math.max(1, (int)Math.ceil(stageTimer / 60.0));
            if (stageTimer <= 0) {
                stage = LAUNCHING;
                message = "LIFTOFF!";
            }
        } else if (stage == LAUNCHING) {
            rocketY -= 4.0;
            fuel -= 0.15;
            if (rocketY < -280) {
                stage = SPACE;
                rocketX = 120;
                rocketY = 350;
                message = "Fly to Mars and avoid the asteroids";
            }
        } else if (stage == SPACE) {
            updateSpaceMission();
        }

        if ((fuel <= 0 || oxygen <= 0 || energy <= 0 || equipment <= 0)
                && stage != WIN && stage != READY) {
            stage = LOSE;
            message = "MISSION FAILED - Press N to restart";
        }
    }

    private void updateSpaceMission() {
        double speed = equipmentBroken ? 1.8 : 4.0;

        if (up && rocketY > 95) rocketY -= speed;
        if (down && rocketY < 640) rocketY += speed;
        if (left && rocketX > 45) rocketX -= speed;
        if (right && rocketX < 1030) rocketX += speed;

        if (up || down || left || right) fuel -= 0.025;
        oxygen -= 0.006;
        energy -= 0.004;

        if (!equipmentBroken && random.nextInt(2400) == 0) {
            equipmentBroken = true;
            equipment -= 25;
            message = "Equipment failure! Press R to repair";
        }

        for (int i = 0; i < asteroidX.length; i++) {
            asteroidX[i] -= 2 + i % 3;
            if (asteroidX[i] < -60) {
                asteroidX[i] = 1120 + random.nextInt(400);
                asteroidY[i] = 110 + random.nextInt(500);
            }

            double distance = Math.hypot(rocketX - asteroidX[i], rocketY - asteroidY[i]);
            if (distance < asteroidSize[i] / 2.0 + 25) {
                equipment -= 0.8;
                energy -= 0.4;
                asteroidX[i] = 1150 + random.nextInt(300);
                message = "Asteroid collision! Equipment damaged";
            }
        }

        if (rocketX > 940) {
            stage = WIN;
            score = (int)(fuel + oxygen + energy + equipment);
            message = "MARS REACHED! Mission score: " + score;
        }
    }

    private void resetAsteroids() {
        for (int i = 0; i < asteroidX.length; i++) {
            asteroidX[i] = 420 + i * 145 + random.nextInt(100);
            asteroidY[i] = 120 + random.nextInt(500);
            asteroidSize[i] = 30 + random.nextInt(35);
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (stage <= LAUNCHING) {
            drawEarth(g);
        } else {
            drawSpace(g);
        }

        drawDashboard(g);
    }

    private void drawEarth(Graphics2D g) {
        GradientPaint sky = new GradientPaint(0, 0, new Color(35, 155, 230),
                0, 500, new Color(185, 225, 247));
        g.setPaint(sky);
        g.fillRect(0, 0, 1100, 500);

        g.setColor(new Color(255, 220, 65));
        g.fillOval(900, 60, 80, 80);
        drawCloud(g, (int)cloud1, 115, 1.0);
        drawCloud(g, (int)cloud2, 185, 0.7);
        drawCloud(g, (int)cloud3, 105, 0.9);

        g.setColor(new Color(76, 167, 105));
        g.fillOval(-150, 390, 520, 180);
        g.fillOval(260, 400, 580, 170);
        g.fillOval(710, 380, 560, 190);
        g.setColor(new Color(36, 150, 72));
        g.fillRect(0, 455, 1100, 245);

        drawControlRoom(g);
        drawFence(g);
        drawLaunchPad(g);
        drawLaunchTower(g);

        if (stage != SPACE) drawRocket(g, rocketX, rocketY, stage >= COUNTDOWN);
        if (stage == READY || stage == BOARDING) drawAstronaut(g, astronautX, astronautY);
    }

    private void drawCloud(Graphics2D g, int x, int y, double size) {
        g.setColor(new Color(255, 255, 255, 235));
        g.fillOval(x, y, (int)(130 * size), (int)(45 * size));
        g.fillOval((int)(x - 38 * size), (int)(y + 7 * size),
                (int)(75 * size), (int)(40 * size));
        g.fillOval((int)(x + 40 * size), (int)(y + 7 * size),
                (int)(78 * size), (int)(42 * size));
        g.fillOval((int)(x - 10 * size), (int)(y - 25 * size),
                (int)(70 * size), (int)(62 * size));
    }

    private void drawControlRoom(Graphics2D g) {
        g.setColor(new Color(225, 232, 230));
        g.fillRoundRect(55, 510, 270, 120, 10, 10);
        g.setColor(new Color(52, 67, 76));
        g.fillRoundRect(45, 490, 290, 35, 8, 8);
        g.setColor(new Color(244, 153, 35));
        g.fillRect(45, 518, 290, 7);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("CONTROL ROOM", 68, 514);

        for (int x = 78; x <= 202; x += 62) {
            g.setColor(new Color(48, 145, 190));
            g.fillRoundRect(x, 548, 48, 42, 5, 5);
            g.setColor(new Color(155, 220, 240));
            g.drawLine(x + 8, 551, x + 30, 551);
        }

        g.setColor(new Color(55, 70, 78));
        g.fillRoundRect(270, 548, 38, 82, 5, 5);
        g.setColor(new Color(240, 190, 60));
        g.fillOval(296, 587, 5, 5);
    }

    private void drawFence(Graphics2D g) {
        g.setColor(new Color(190, 200, 200));
        g.setStroke(new BasicStroke(2));
        for (int x = 0; x < 1100; x += 45) g.drawLine(x, 475, x, 535);
        g.drawLine(0, 490, 1100, 490);
        g.drawLine(0, 520, 1100, 520);
    }

    private void drawLaunchPad(Graphics2D g) {
        g.setColor(new Color(92, 98, 103));
        Polygon pad = new Polygon(new int[]{590, 965, 1060, 485},
                new int[]{555, 555, 700, 700}, 4);
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
        for (int y = 260; y < 505; y += 40) {
            g.drawLine(730, y, 820, y + 40);
            g.drawLine(820, y, 730, y + 40);
            g.drawLine(730, y, 820, y);
        }
    }

    private void drawRocket(Graphics2D g, double x, double y, boolean fire) {
        int rx = (int)x;
        int ry = (int)y;

        g.setColor(new Color(230, 235, 238));
        g.fillRoundRect(rx - 40, ry - 220, 80, 210, 20, 20);
        Polygon nose = new Polygon(new int[]{rx - 40, rx, rx + 40},
                new int[]{ry - 210, ry - 295, ry - 210}, 3);
        g.fillPolygon(nose);
        g.setColor(new Color(45, 155, 210));
        g.fillOval(rx - 24, ry - 175, 48, 48);
        g.setColor(new Color(195, 45, 50));
        g.fillRect(rx - 40, ry - 65, 80, 27);
        g.fillPolygon(new Polygon(new int[]{rx - 40, rx - 76, rx - 40},
                new int[]{ry - 70, ry, ry - 15}, 3));
        g.fillPolygon(new Polygon(new int[]{rx + 40, rx + 76, rx + 40},
                new int[]{ry - 70, ry, ry - 15}, 3));

        if (fire) {
            int flame = stage == LAUNCHING ? 135 : 75;
            g.setColor(new Color(255, 85, 15));
            g.fillPolygon(new Polygon(new int[]{rx - 30, rx, rx + 30},
                    new int[]{ry - 10, ry + flame, ry - 10}, 3));
            g.setColor(new Color(255, 225, 70));
            g.fillPolygon(new Polygon(new int[]{rx - 14, rx, rx + 14},
                    new int[]{ry - 10, ry + flame - 35, ry - 10}, 3));
        }
    }

    private void drawAstronaut(Graphics2D g, double x, double y) {
        int ax = (int)x;
        int ay = (int)y;
        int walk = stage == BOARDING ? (int)(Math.sin(System.currentTimeMillis() * 0.012) * 7) : 0;

        g.setColor(new Color(245, 247, 248));
        g.fillRoundRect(ax - 17, ay - 48, 34, 55, 10, 10);
        g.fillOval(ax - 23, ay - 88, 46, 46);
        g.setColor(new Color(50, 150, 205));
        g.fillOval(ax - 16, ay - 80, 32, 25);
        g.setColor(new Color(240, 243, 245));
        g.setStroke(new BasicStroke(9));
        g.drawLine(ax - 8, ay + 2, ax - 15 + walk, ay + 30);
        g.drawLine(ax + 8, ay + 2, ax + 15 - walk, ay + 30);
    }

    private void drawSpace(Graphics2D g) {
        g.setColor(new Color(5, 8, 28));
        g.fillRect(0, 0, 1100, 700);

        g.setColor(Color.WHITE);
        for (int i = 0; i < starX.length; i++) {
            int size = 1 + i % 3;
            g.fillOval(starX[i], starY[i], size, size);
        }

        // Mars destination
        g.setColor(new Color(191, 72, 45));
        g.fillOval(960, 260, 210, 210);
        g.setColor(new Color(125, 46, 35));
        g.fillOval(1000, 300, 48, 28);
        g.fillOval(1045, 390, 35, 24);

        for (int i = 0; i < asteroidX.length; i++) {
            int s = asteroidSize[i];
            g.setColor(new Color(115, 105, 100));
            g.fillOval(asteroidX[i] - s / 2, asteroidY[i] - s / 2, s, s);
            g.setColor(new Color(75, 70, 68));
            g.fillOval(asteroidX[i] - s / 5, asteroidY[i] - s / 5, s / 4, s / 5);
        }

        if (stage != WIN && stage != LOSE) {
            drawSpaceRocket(g, (int)rocketX, (int)rocketY);
        }

        if (stage == WIN) {
            drawCentreMessage(g, "MISSION ACCOMPLISHED", "You reached Mars! Press N to play again");
        } else if (stage == LOSE) {
            drawCentreMessage(g, "MISSION FAILED", "A resource reached zero. Press N to retry");
        }
    }

    private void drawSpaceRocket(Graphics2D g, int x, int y) {
        g.setColor(new Color(225, 232, 235));
        g.fillOval(x - 35, y - 20, 75, 40);
        g.setColor(new Color(195, 45, 50));
        g.fillPolygon(new Polygon(new int[]{x - 25, x - 50, x - 25},
                new int[]{y - 15, y - 35, y}, 3));
        g.setColor(new Color(55, 165, 215));
        g.fillOval(x + 5, y - 12, 22, 22);
        g.setColor(new Color(255, 155, 25));
        g.fillPolygon(new Polygon(new int[]{x - 35, x - 70, x - 35},
                new int[]{y - 11, y, y + 11}, 3));
    }

    private void drawDashboard(Graphics2D g) {
        g.setColor(new Color(10, 30, 48, 225));
        g.fillRoundRect(20, 18, 1060, 72, 16, 16);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 23));
        g.drawString("MARS MISSION", 42, 49);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString(message, 42, 72);

        drawBar(g, 350, 35, "FUEL", fuel, new Color(245, 170, 35));
        drawBar(g, 535, 35, "OXYGEN", oxygen, new Color(70, 200, 235));
        drawBar(g, 720, 35, "ENERGY", energy, new Color(95, 220, 125));
        drawBar(g, 905, 35, "EQUIPMENT", equipment, new Color(205, 125, 230));

        g.setColor(new Color(10, 30, 48, 215));
        g.fillRoundRect(20, 640, 470, 42, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String controls = stage < SPACE
                ? "ENTER: Start     N: Restart"
                : "ARROW KEYS: Fly     R: Repair     N: Restart";
        g.drawString(controls, 38, 666);
    }

    private void drawBar(Graphics2D g, int x, int y, String label, double value, Color colour) {
        value = Math.max(0, Math.min(100, value));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(Color.WHITE);
        g.drawString(label, x, y - 5);
        g.setColor(new Color(55, 70, 80));
        g.fillRoundRect(x, y, 145, 14, 8, 8);
        g.setColor(colour);
        g.fillRoundRect(x, y, (int)(145 * value / 100), 14, 8, 8);
        g.setColor(Color.WHITE);
        g.drawString((int)value + "%", x + 55, y + 12);
    }

    private void drawCentreMessage(Graphics2D g, String title, String subtitle) {
        g.setColor(new Color(8, 18, 36, 225));
        g.fillRoundRect(280, 260, 540, 150, 22, 22);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 34));
        FontMetrics titleMetrics = g.getFontMetrics();
        g.drawString(title, 550 - titleMetrics.stringWidth(title) / 2, 320);
        g.setFont(new Font("SansSerif", Font.PLAIN, 17));
        FontMetrics subtitleMetrics = g.getFontMetrics();
        g.drawString(subtitle, 550 - subtitleMetrics.stringWidth(subtitle) / 2, 365);
    }

    private void restartGame() {
        stage = READY;
        stageTimer = 0;
        score = 0;
        rocketX = 770;
        rocketY = 545;
        astronautX = 350;
        astronautY = 610;
        fuel = 100;
        oxygen = 100;
        energy = 100;
        equipment = 100;
        equipmentBroken = false;
        message = "Press ENTER to begin the Mars mission";
        resetAsteroids();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int key = event.getKeyCode();
        if (key == KeyEvent.VK_ENTER && stage == READY) stage = BOARDING;
        if (key == KeyEvent.VK_UP) up = true;
        if (key == KeyEvent.VK_DOWN) down = true;
        if (key == KeyEvent.VK_LEFT) left = true;
        if (key == KeyEvent.VK_RIGHT) right = true;

        if (key == KeyEvent.VK_R && stage == SPACE && equipmentBroken) {
            if (energy >= 15) {
                energy -= 15;
                equipment = Math.min(100, equipment + 35);
                equipmentBroken = false;
                message = "Engineer repaired the equipment";
            } else {
                message = "Not enough energy to repair equipment";
            }
        }

        if (key == KeyEvent.VK_N) restartGame();
    }

    @Override
    public void keyReleased(KeyEvent event) {
        int key = event.getKeyCode();
        if (key == KeyEvent.VK_UP) up = false;
        if (key == KeyEvent.VK_DOWN) down = false;
        if (key == KeyEvent.VK_LEFT) left = false;
        if (key == KeyEvent.VK_RIGHT) right = false;
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }
}
