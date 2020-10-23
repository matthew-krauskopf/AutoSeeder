package GUI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.Color;
import javax.swing.*;

public class BracketPanel extends javax.swing.JPanel {

    JSplitPane [] set_panes;
    int set_gap = 25;

    public BracketPanel(JSplitPane [] in_set_panes) {

        set_panes = in_set_panes;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        int sq_entrants = (set_panes.length+3)/2;
        int end = sq_entrants/2;
        int tot = 0;
        int set_count = 0;
        int x_pos; int y_pos;

        // Draw winner's matches lines
        while (tot < sq_entrants-1) {
            for (int cur = 0; cur < end; cur++) {
                // Only do for sets that are visible
                if (set_panes[set_count].isVisible()) {
                    // Extend forward if not winner's finals match
                    if (tot < sq_entrants-2) {
                        x_pos = set_panes[set_count].getX() + set_panes[set_count].getWidth();
                        y_pos = set_panes[set_count].getY() + (set_panes[set_count].getHeight()/2);
                        g.drawLine(x_pos, y_pos, x_pos+set_gap, y_pos);
                    }
                    // Extend backwards if not round 1 match
                    if (set_count >= sq_entrants/2) {
                        x_pos = set_panes[set_count].getX();
                        y_pos = set_panes[set_count].getY() + (set_panes[set_count].getHeight()/2);
                        g.drawLine(x_pos-set_gap, y_pos, x_pos, y_pos);

                        // Extend up off back tail if set exists
                        if (set_panes[set_count-(end*2) + cur].isVisible()) {
                            int top_y = set_panes[set_count-(end*2) + cur].getY() + (set_panes[set_count-(end*2) + cur].getHeight()/2);
                            g.drawLine(x_pos-set_gap, top_y, x_pos-set_gap, y_pos);
                        }

                        // Extend down off back tail if set exists
                        if (set_panes[set_count-(end*2) + cur + 1].isVisible()) {
                            int bot_y = set_panes[set_count-(end*2) + cur+1].getY() + (set_panes[set_count-(end*2) + cur+1].getHeight()/2);
                            g.drawLine(x_pos-set_gap, y_pos, x_pos-set_gap, bot_y);
                        }
                    }
                }
                set_count++;
            }
            tot += end;
            end /= 2;
        }
    }
}
