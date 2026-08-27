import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class SimulationPanel extends JPanel {

    private Station station;

    public SimulationPanel(Station station) {
        this.station = station;
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.drawString("SPACE STATION SURVIVAL", 240, 40);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(220, 130, 260, 180);

        g.setColor(Color.BLUE);
        g.fillOval(315, 170, 70, 70);

        g.setColor(Color.CYAN);
        g.fillRect(70, 160, 130, 100);
        g.fillRect(500, 160, 130, 100);

        g.setColor(Color.WHITE);
        int textY = 350;
        for (Equipment item : station.getEquipmentList()) {
            g.drawString(item.getName() + ": " + item.getCondition() + "%", 260, textY);
            textY = textY + 20;
        }
    }
}