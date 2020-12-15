package GUI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import javax.swing.*;

public class BracketPanel extends javax.swing.JPanel {

    JTable [] set_tables;
    int set_gap = 25;
    int line_gap = 7;

    public BracketPanel(JTable [] in_set_tables) {
        set_tables = in_set_tables;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        g2.setColor(Color.WHITE);

        int sq_entrants = (set_tables.length+3)/2;
        int end = sq_entrants/2;
        int tot = 0;
        int set_count = 0;
        int x_pos; int y_pos;
        Boolean up = false, down = false;

        // Draw winner's matches lines
        while (tot < sq_entrants-1) {
            for (int cur = 0; cur < end; cur++) {
                // Only do for sets that are visible
                if (set_tables[set_count].isVisible()) {
                    // Extend forward if not winner's finals match
                    if (tot < sq_entrants-2) {
                        x_pos = set_tables[set_count].getX() + set_tables[set_count].getWidth();
                        y_pos = set_tables[set_count].getY() + (set_tables[set_count].getHeight()/2);
                        g2.drawLine(x_pos+line_gap, y_pos, x_pos+set_gap, y_pos);
                    }
                    // Extend backwards if not round 1 match
                    if (set_count >= sq_entrants/2) {
                        x_pos = set_tables[set_count].getX();
                        y_pos = set_tables[set_count].getY() + (set_tables[set_count].getHeight()/2);

                        // Extend up off back tail if set exists
                        if (set_tables[set_count-(end*2) + cur].isVisible()) {
                            int top_y = set_tables[set_count-(end*2) + cur].getY() + (set_tables[set_count-(end*2) + cur].getHeight()/2);
                            g2.drawLine(x_pos-set_gap, top_y, x_pos-set_gap, y_pos);
                            up = true;
                        }

                        // Extend down off back tail if set exists
                        if (set_tables[set_count-(end*2) + cur + 1].isVisible()) {
                            int bot_y = set_tables[set_count-(end*2) + cur+1].getY() + (set_tables[set_count-(end*2) + cur+1].getHeight()/2);
                            g2.drawLine(x_pos-set_gap, y_pos, x_pos-set_gap, bot_y);
                            down = true;
                        }

                        // Extend backwards if set above or below exists
                        if (up || down) {
                            g2.drawLine(x_pos-set_gap, y_pos, x_pos-line_gap, y_pos);
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
        while(tot < set_tables.length) {
            // Go to the end of this round
            for (int cur = 0; cur < end ; cur++) {
                // Only do for sets that are visible
                if (set_tables[set_count].isVisible()) {
                    // Extend forward if not loser's finals match
                    if (set_count < set_tables.length-1) {
                        x_pos = set_tables[set_count].getX() + set_tables[set_count].getWidth();
                        y_pos = set_tables[set_count].getY() + (set_tables[set_count].getHeight()/2);
                        g2.drawLine(x_pos+line_gap, y_pos, x_pos+set_gap, y_pos);
                    }
                    // Extend backwards

                    // If even round, line is straight back
                    if (round % 2 == 0) {
                        if (set_tables[set_count-end].isVisible()) {
                            x_pos = set_tables[set_count].getX();
                            y_pos = set_tables[set_count-end].getY() + (set_tables[set_count-end].getHeight()/2);
                            g2.drawLine(x_pos-set_gap, y_pos, x_pos-line_gap, y_pos);
                        }
                    }
                    // If odd round, condense like winners
                    else if (round % 2 == 1 && round != 1) {
                        x_pos = set_tables[set_count].getX();
                        y_pos = set_tables[set_count].getY() + (set_tables[set_count].getHeight()/2);

                        // Extend up off back tail if set exists
                        if (set_tables[set_count-(end*2) + cur].isVisible()) {
                            int top_y = set_tables[set_count-(end*2) + cur].getY() + (set_tables[set_count-(end*2) + cur].getHeight()/2);
                            g2.drawLine(x_pos-set_gap, top_y, x_pos-set_gap, y_pos);
                            up = true;
                        }

                        // Extend down off back tail if set exists
                        if (set_tables[set_count-(end*2) + cur + 1].isVisible()) {
                            int bot_y = set_tables[set_count-(end*2) + cur + 1].getY() + (set_tables[set_count-(end*2) + cur + 1].getHeight()/2);
                            g2.drawLine(x_pos-set_gap, y_pos, x_pos-set_gap, bot_y);
                            down = true;
                        }

                        // Extend backwards if set above or below exists
                        if (up || down) {
                            g2.drawLine(x_pos-set_gap, y_pos, x_pos-line_gap, y_pos);
                        }
                    }
                }
                set_count++;
                // Reset up and down checks
                up = false;
                down = false;
            }
            tot += end;
            round++;
            // Number of sets in loser's only cuts in half every other round
            if (round % 2 == 1) end /= 2;
        }
    }
}
