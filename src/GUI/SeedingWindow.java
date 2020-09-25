package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;
import DBase.DBManager;


// TODO REFACTOR!!!!!     
public class SeedingWindow {

    static JFrame window = new JFrame("Suggested Seeding");
    static JPanel match_panel = new JPanel(null);
    static JSplitPane [] set_panes;
    static JLabel [] w_round_labels;
    static JLabel [] l_round_labels;
    static JScrollPane matchup_view;
    static JScrollPane seeded_entrants;
    static JList<String> list;

    static int x_edge = 10;
    static int y_offset = 0;
    static int set_gap = 75;
    static Font font = new Font("Acumin", 0, 16);

    static int round;

    public void MakeSeedingWindow(String[] entrants, Set[] sets) {
        
        DefaultListModel<String> l1 = new DefaultListModel<>();
        for (int i = 0; i < entrants.length; i++) {
            l1.addElement((i+1) + ": " + entrants[i]);
        }
        list = new JList<>(l1);

        int sq_entrants = (sets.length+3)/2;
        int tot = 0;
        round = 1;

        // Pack winner matches       
        w_round_labels = new JLabel[(int) (Math.log10(sq_entrants)/Math.log10(2))];
        set_panes = new JSplitPane[sets.length];
        int set_count = 0;
        int max_win_rs = 0;
        while (tot < sq_entrants-1) {
            //System.out.println("\n" + sq_entrants + " WR " + round);
            
            w_round_labels[round-1] = new JLabel("Winner's Round " + round);
            w_round_labels[round-1].setBounds(200*(round-1)+x_edge,0,200,30);
            w_round_labels[round-1].setAlignmentX(Component.LEFT_ALIGNMENT);
            
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
    }

    public void Launch() {
        // Set font
        list.setFont(font);
        
        // Set window size
        window.setSize(1500,750);
       
        // Configure matchup panel
        match_panel.setBounds(0, 0, (int)(window.getWidth()*.125), window.getHeight()-40);
        match_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        match_panel.setMinimumSize(new Dimension(window.getWidth()-(int)(window.getWidth()*.125)-20, window.getHeight()-40));
        match_panel.setPreferredSize(new Dimension(200*(round-1), y_offset+60));

        // Configure matchup view
        matchup_view = new JScrollPane(match_panel);
        matchup_view.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        matchup_view.getVerticalScrollBar().setUnitIncrement(16);
        matchup_view.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        matchup_view.setBounds( (int)(window.getWidth()*.125), 0, window.getWidth()-(int)(window.getWidth()*.125)-20, window.getHeight()-40);

        // Attach list of entrants to scroll pane
        seeded_entrants = new JScrollPane(list);
        // Set scroll properties
        seeded_entrants.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        seeded_entrants.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        seeded_entrants.setBounds(0, 0, (int)(window.getWidth()*.125), window.getHeight()-40);

        // Attach components to window and display
        window.getContentPane().add(seeded_entrants); window.getContentPane().add(matchup_view);
        window.setLayout(null);
        /*window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });*/
        window.setVisible(true);
        ReadFile.clean_tmp_files();
    }
}