package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;


public class SeedingWindow {

    JFrame window = new JFrame("Suggested Seeding");
    JPanel match_panel = new JPanel(null);
    JSplitPane [] set_panes;
    JLabel [] w_round_labels;
    JLabel [] l_round_labels;
    JScrollPane matchups_sc_pane;
    JScrollPane seeded_sc_pane;
    JList<String> list;

    String [] entrants;
    Set [] sets;

    Color bg_color = new Color(46, 52, 61);
    Font font = new Font("Acumin", 0, 16);
    Font rounds_font = new Font("Helvetica", Font.BOLD, 16);

    int x_edge = 10;
    int y_offset = 0;
    int set_gap = 75;

    int round;
    int max_win_rs;

    public void MakeSeedingList() {
        DefaultListModel<String> l1 = new DefaultListModel<>();
        for (int i = 0; i < entrants.length; i++) {
            l1.addElement((i+1) + ": " + entrants[i]);
        }
        list = new JList<>(l1);
        seeded_sc_pane = new JScrollPane(list);
    }

    public void MakeSeedingWindow() {
        // Set variables used over both winners and losers
        int sq_entrants = (sets.length+3)/2;
        int tot = 0;
        int set_count = 0;
        max_win_rs = sq_entrants/2;

        // Calculate how many labels and set panes are needed
        w_round_labels = new JLabel[(int) (Math.log10(sq_entrants)/Math.log10(2))];
        l_round_labels = new JLabel[2 * ( (int)(Math.log10(sq_entrants)/Math.log10(2)) - 1)];
        set_panes = new JSplitPane[sets.length];

        // Pack winner matches
        round = 1;
        int end = sq_entrants/2;

        while (tot < sq_entrants-1) {
            // Create label for this round and add it to panel
            w_round_labels[round-1] = GenerateLabel(round, 1);
            match_panel.add(w_round_labels[round-1]);

            int [] set_order = API.get_visual_order(0, end);
            // Go to the end of this round
            for (int cur = 0; cur < end ; cur++) {
                // Generate SplitPane (TODO: Replace with JTable)
                set_panes[set_count] = GenerateSpPlane(sets[set_count]);

                // Set location of SplitPane
                // X does not need to be adjusted
                int x_pos = 200*(round-1)+x_edge;
                int y_pos = (round == 1 ? set_gap*(cur+1)-50 : GetYLocation(set_panes, set_count, end, cur));
                set_panes[set_count].setLocation(x_pos, y_pos);

                // Add split pane to match panel if not a Bye match
                //if (!sets[tot+set_order[cur]].l_player.equals("Bye")) {
                if (!sets[set_count].l_player.equals("Bye")) {
                    match_panel.add(set_panes[set_count]);
                }
                set_count++;
            }
            tot += end;
            end /= 2;
            round++;
        }

        // Pack loser matches
        round = 1;
        end = sq_entrants/4;
        while(tot < sets.length) {
            l_round_labels[round-1] = GenerateLabel(round, -1);
            match_panel.add(l_round_labels[round-1]);
            // Go to the end of this round
            for (int cur = 0; cur < end ; cur++) {
                // Generate SplitPane (TODO: Replace with JTable)
                set_panes[set_count] = GenerateSpPlane(sets[tot+cur]);

                // Set location of SplitPane
                int x_pos = 200*(round-1)+x_edge;
                int y_pos = set_gap*(max_win_rs+cur+1);
                set_panes[set_count].setLocation(x_pos, y_pos);
                // Add split pane to match panel
                if (!sets[tot].l_player.equals("Bye")) {
                    match_panel.add(set_panes[set_count]);
                }
                set_count++;

                // Used to know how large to size window height (TODO Test to see if can replace with math)
                y_offset = (y_pos > y_offset ? y_pos : y_offset);
            }
            tot += end;
            round++;
            // Number of sets in loser's only cuts in half every other round
            if (round % 2 == 1) end /= 2;
        }
    }

    private JLabel GenerateLabel(int round, int side) {
        JLabel label = new JLabel(String.format("%s's Round %d", (side == 1 ? "Winner" : "Loser"), round));
        label.setBounds(200*(round-1)+x_edge,(side == 1 ? 0 : set_gap*max_win_rs+50),200,30);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        label.setFont(rounds_font);
        label.setForeground(Color.WHITE);

        return label;
    }

    private JSplitPane GenerateSpPlane(Set set_info) {
        JLabel winner = new JLabel(String.format("%3d:   %s",set_info.h_seed, set_info.h_player));
        JLabel loser = new JLabel(String.format("%3d:   %s",set_info.l_seed, set_info.l_player));
        JSplitPane sp_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, winner, loser);
        //System.out.println(String.format("%s vs %s", set_info.h_player, set_info.l_player));
        sp_pane.setSize(150, 50);
        return sp_pane;
    }

    private int GetYLocation(JSplitPane [] set_panes, int set_count, int end, int cur) {
        // Find locations of previous matches that lead into this one
        int pos_above = set_panes[(set_count-cur)-(end*2) + (cur*2)].getY();
        int pos_below = set_panes[(set_count-cur)-(end*2) + (cur*2) + 1].getY();
        return (pos_above+pos_below)/2;
    }

    public SeedingWindow(String [] fed_entrants, Set [] fed_sets) {
        // Attach fed in arguments
        entrants = fed_entrants;
        sets = fed_sets;

        // Call JComponent construction functions
        MakeSeedingList();
        MakeSeedingWindow();

        matchups_sc_pane = new JScrollPane(match_panel);

        // Set Window Attributes
        window.setLayout(null);

        // Set fonts and colors
        list.setFont(font);
        list.setBackground(bg_color);
        list.setForeground(Color.WHITE);

        match_panel.setBackground(bg_color);

        // Set component sizes
        window.setSize(1500,750);

        seeded_sc_pane.setSize((int)(window.getWidth()*.125), window.getHeight()-40);
        match_panel.setSize(seeded_sc_pane.getWidth(), seeded_sc_pane.getHeight());
        matchups_sc_pane.setSize(window.getWidth()-seeded_sc_pane.getWidth(), window.getHeight()-40);

        // Set component locations
        match_panel.setLocation(0, 0);
        seeded_sc_pane.setLocation(0, 0);
        matchups_sc_pane.setLocation(seeded_sc_pane.getX()+seeded_sc_pane.getWidth(), 0);

        // Set Misc.
        //match_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        match_panel.setMinimumSize(new Dimension(window.getWidth()-(int)(window.getWidth()*.125)-20, window.getHeight()-40));
        match_panel.setPreferredSize(new Dimension(200*(round-1), y_offset+60));

        // Set Scrollbar Policies
        matchups_sc_pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        matchups_sc_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        matchups_sc_pane.getVerticalScrollBar().setUnitIncrement(16);

        seeded_sc_pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        seeded_sc_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add action listeners
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });

        // Pack items into window
        window.getContentPane().add(seeded_sc_pane);
        window.getContentPane().add(matchups_sc_pane);
    }

    public void Launch() {
        window.setVisible(true);
    }
}
