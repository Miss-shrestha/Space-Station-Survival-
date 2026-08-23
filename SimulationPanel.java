import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class SimulationPanel extends JPanel {

    public SimulationPanel() {
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Title
        g.setColor(Color.WHITE);
        g.drawString("SPACE STATION SURVIVAL", 240, 40);

        // Main station
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(220, 130, 260, 180);

        // Station window
        g.setColor(Color.BLUE);
        g.fillOval(315, 170, 70, 70);

        // Solar panels
        g.setColor(Color.CYAN);
        g.fillRect(70, 160, 130, 100);
        g.fillRect(500, 160, 130, 100);

        // Resource information
        g.setColor(Color.WHITE);
        g.drawString("Oxygen: 100%", 250, 350);
        g.drawString("Energy: 100%", 400, 350);
        g.drawString("Equipment: Working", 280, 390);
    }
}