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
        Boolean up = false, down = false;

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
                        // Extend up off back tail if set exists
                        if (set_panes[set_count-(end*2) + cur].isVisible()) {
                            int top_y = set_panes[set_count-(end*2) + cur].getY() + (set_panes[set_count-(end*2) + cur].getHeight()/2);
                            g.drawLine(x_pos-set_gap, top_y, x_pos-set_gap, y_pos);
                            up = true;
                        }

                        // Extend down off back tail if set exists
                        if (set_panes[set_count-(end*2) + cur + 1].isVisible()) {
                            int bot_y = set_panes[set_count-(end*2) + cur+1].getY() + (set_panes[set_count-(end*2) + cur+1].getHeight()/2);
                            g.drawLine(x_pos-set_gap, y_pos, x_pos-set_gap, bot_y);
                            down = true;
                        }

                        if (up || down) {
                            g.drawLine(x_pos-set_gap, y_pos, x_pos, y_pos);
                        }
                    }
                }
                set_count++;
                // Reset up and down checks
                up = false;
                down = false;
            }
            tot += end;
            end /= 2;
        }

        // Draw loser's matches lines
        int round = 1;
        end = sq_entrants/4;
        while(tot < set_panes.length) {
            // Go to the end of this round
            for (int cur = 0; cur < end ; cur++) {
                // Only do for sets that are visible
                if (set_panes[set_count].isVisible()) {
                    // Extend forward if not loser's finals match
                    if (set_count < set_panes.length-1) {
                        x_pos = set_panes[set_count].getX() + set_panes[set_count].getWidth();
                        y_pos = set_panes[set_count].getY() + (set_panes[set_count].getHeight()/2);
                        g.drawLine(x_pos, y_pos, x_pos+set_gap, y_pos);
                    }
                    // Extend backwards
                    // If even round, line is straight back
                    if (round % 2 == 0) {
                        if (set_panes[set_count-end].isVisible()) {
                            x_pos = set_panes[set_count].getX();
                            y_pos = set_panes[set_count-end].getY() + (set_panes[set_count-end].getHeight()/2);
                            g.drawLine(x_pos-set_gap, y_pos, x_pos, y_pos);
                        }
                    }
                    // If odd round, condense like winners
                    else if (round % 2 == 1 && round != 1) {
                        x_pos = set_panes[set_count].getX();
                        y_pos = set_panes[set_count].getY() + (set_panes[set_count].getHeight()/2);
                        if (set_panes[set_count-(end*2) + cur].isVisible()) {
                            int top_y = set_panes[set_count-(end*2) + cur].getY() + (set_panes[set_count-(end*2) + cur].getHeight()/2);
                            g.drawLine(x_pos-set_gap, top_y, x_pos-set_gap, y_pos);
                            up = true;
                        }

                        if (set_panes[set_count-(end*2) + cur + 1].isVisible()) {
                            int bot_y = set_panes[set_count-(end*2) + cur + 1].getY() + (set_panes[set_count-(end*2) + cur + 1].getHeight()/2);
                            g.drawLine(x_pos-set_gap, y_pos, x_pos-set_gap, bot_y);
                            down = true;
                        }
                        if (up || down) {
                            g.drawLine(x_pos-set_gap, y_pos, x_pos, y_pos);
                        }
                    }
                }
                set_count++;
            }
            tot += end;
            round++;
            // Number of sets in loser's only cuts in half every other round
            if (round % 2 == 1) end /= 2;
        }
    }
}
