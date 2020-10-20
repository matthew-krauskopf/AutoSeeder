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

    public void MakeSeedingList() {
        DefaultListModel<String> l1 = new DefaultListModel<>();
        for (int i = 0; i < entrants.length; i++) {
            l1.addElement((i+1) + ": " + entrants[i]);
        }
        list = new JList<>(l1);
        seeded_sc_pane = new JScrollPane(list);
    }

    public void MakeSeedingWindow() {

        int sq_entrants = (sets.length+3)/2;
        int tot = 0;
        round = 1;

        // Pack winner matches
        w_round_labels = new JLabel[(int) (Math.log10(sq_entrants)/Math.log10(2))];
        set_panes = new JSplitPane[sets.length];
        int set_count = 0;
        int max_win_rs = 0;
        while (tot < sq_entrants-1) {
            w_round_labels[round-1] = new JLabel("Winner's Round " + round);
            w_round_labels[round-1].setBounds(200*(round-1)+x_edge,0,200,30);
            w_round_labels[round-1].setAlignmentX(Component.LEFT_ALIGNMENT);

            // Set label font and color
            w_round_labels[round-1].setFont(rounds_font);
            w_round_labels[round-1].setForeground(Color.WHITE);

            match_panel.add(w_round_labels[round-1]);
            int end = (sq_entrants-tot)/2;
            int skipped = 0;
            for (int cur = 0; cur < end ; cur++) {
                if (!sets[tot].l_player.equals("Bye")) {
                    JLabel h_seed = new JLabel(String.format("%3d:   %s",sets[tot].h_seed, sets[tot].h_player));
                    JLabel l_seed = new JLabel(String.format("%3d:   %s",sets[tot].l_seed, sets[tot].l_player));
                    set_panes[set_count] = new JSplitPane(JSplitPane.VERTICAL_SPLIT, h_seed, l_seed);
                    set_panes[set_count].setBounds(200*(round-1)+x_edge,set_gap*(cur+1-skipped)-50, 150, 50);
                    match_panel.add(set_panes[set_count]);
                    set_count++;
                }
                else {
                    skipped++;
                }
                tot++;
            }
            max_win_rs = (end-skipped > max_win_rs ? end-skipped : max_win_rs);
            round++;
        }

        // Pack loser matches
        round = 1;
        // Just accept it. It works
        l_round_labels = new JLabel[2 * ( (int)(Math.log10(sq_entrants)/Math.log10(2)) - 1)];
        int end = sq_entrants/4;
        while(tot < sets.length) {
            l_round_labels[round-1] = new JLabel("Loser's Round " + round);
            l_round_labels[round-1].setBounds(200*(round-1)+x_edge,set_gap*(max_win_rs)+50,200,30);
            l_round_labels[round-1].setAlignmentX(Component.LEFT_ALIGNMENT);

            // Set label font and color
            l_round_labels[round-1].setFont(rounds_font);
            l_round_labels[round-1].setForeground(Color.WHITE);

            match_panel.add(l_round_labels[round-1]);
            int skipped = 0;
            for (int cur = 0; cur < end ; cur++) {
                if (!sets[tot].l_player.equals("Bye")) {
                    JLabel h_seed = new JLabel(String.format("%3d:   %s",sets[tot].h_seed, sets[tot].h_player));
                    JLabel l_seed = new JLabel(String.format("%3d:   %s",sets[tot].l_seed, sets[tot].l_player));
                    set_panes[set_count] = new JSplitPane(JSplitPane.VERTICAL_SPLIT, h_seed, l_seed);
                    int x_pos = 200*(round-1)+x_edge;
                    int y_pos = set_gap*(max_win_rs+cur+1-skipped);
                    // Used to know how large to size window
                    y_offset = (y_pos > y_offset ? y_pos : y_offset);
                    set_panes[set_count].setBounds(x_pos, y_pos, 150, 50);
                    match_panel.add(set_panes[set_count]);
                    set_count++;
                }
                else {
                    skipped++;
                }
                tot++;
            }
            round++;
            if (round % 2 == 1) end /= 2;
        }
        matchups_sc_pane = new JScrollPane(match_panel);
    }

    public SeedingWindow(String [] fed_entrants, Set [] fed_sets) {
        // Attach fed in arguments
        entrants = fed_entrants;
        sets = fed_sets;

        // Call JComponent construction functions
        MakeSeedingList();
        MakeSeedingWindow();

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
        //match_panel.setMinimumSize(new Dimension(window.getWidth()-(int)(window.getWidth()*.125)-20, window.getHeight()-40));
        //match_panel.setPreferredSize(new Dimension(200*(round-1), y_offset+60));

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
