package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class PreSeedingWindow extends GetLink {

    static String title = "Seed Bracket";

    SeedingWindow S_Window;
    GetAliasWindow GA_window;
    ConflictsWindow C_Window;
    PingingWindow ping_window;

    JCheckBox check_box = new JCheckBox("Shuffle seeding");
    SpinnerModel sp_model = new SpinnerNumberModel(2, //initial value
                                                   0, //minimum value
                                                   5, //maximum value
                                                   1); //step
    JSpinner rounds_val = new JSpinner(sp_model);
    JLabel shake_label = new JLabel("Reseed to avoid recent matchups?");
    JLabel rounds_label = new JLabel("Reseed through how many rounds?");

    public PreSeedingWindow() {
        // Set Window Attributes
        window.setTitle(title);

        // Set fonts and colors
        shake_label.setFont(helveticaB12);
        shake_label.setForeground(Color.WHITE);

        rounds_label.setFont(helveticaB12);
        rounds_label.setForeground(Color.WHITE);

        check_box.setBackground(bg_color);

        // Set component sizes
        shake_label.setSize(getTextWidth(shake_label),20);
        rounds_label.setSize(getTextWidth(rounds_label),20);
        check_box.setSize(20, 20);
        rounds_val.setSize(30, 20);

        // Set component locations
        shake_label.setLocation(getCenter(shake_label)-10,submit.getHeight()+submit.getY()+offset);
        check_box.setLocation(shake_label.getX()+shake_label.getWidth()+10, submit.getHeight()+submit.getY()+offset);
        rounds_label.setLocation(getCenter(rounds_label)-10,check_box.getY()+check_box.getHeight());
        rounds_val.setLocation(rounds_label.getX()+rounds_label.getWidth()+10, rounds_label.getY());

        // Add action listeners
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });

        check_box.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rounds_label.setVisible(!rounds_label.isVisible());
                rounds_val.setVisible(!rounds_val.isVisible());
            }
        });

        // Set misc settings
        ((JSpinner.DefaultEditor)rounds_val.getEditor()).getTextField().setEditable(false);

        // Pack items into window
        window.add(check_box);
        window.add(rounds_val);
        window.add(shake_label);
        window.add(rounds_label);

        // Set element visability
        rounds_label.setVisible(false);
        rounds_val.setVisible(false);
    }

    private void prepSeedingWindow(String [] entrants) {
        // Check number of rounds to reseeded around conflict s
        int shake_rounds = -1;
        if (check_box.isSelected()) {
            try {
                rounds_val.commitEdit();
                shake_rounds = (Integer) rounds_val.getValue();
            } catch (Exception e) {};
        }

        BracketData br_data = API.makeBracket(entrants, shake_rounds);
        // No entrants: Wrong URL?
        if (br_data.entrants.length<=1) {
            f_error.setVisible(false);
            error.setVisible(true);
            return;
        }
        // Close window
        window.dispose();
        // Get sets to be played
        Set[] sets = API.getSets(br_data.entrants);
        // Done with HTML data: clean tmp files
        API.cleanTmpFiles();

        S_Window = new SeedingWindow(br_data.entrants, sets);
        S_Window.launch();
        if (br_data.conflicts.length > 0) {
            C_Window = new ConflictsWindow(br_data);
            C_Window.launch();
        }
    }

    public void prepEntrants() {
        String [] entrants = API.getEntrants();
        String [] unknown_entrants = API.checkUnknownNames(entrants);
        if (unknown_entrants.length != 0) {
            GA_window = new GetAliasWindow(unknown_entrants);
            // Add action listener to GA window so this window closes at same time
            GA_window.window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    window.setVisible(false);
                    GA_window.dispose();
                    prepSeedingWindow(entrants);
                }
            });
            GA_window.launch();
        }
        else {
            window.setVisible(false);
            prepSeedingWindow(entrants);
        }
    }

    @Override
    public void action() {
        String url = field.getText().trim();
        // Check if URL seems to be valid
        if (!API.validURL(url)) {
            error.setVisible(false);
            f_error.setVisible(true);
            return;
        }
        // Generate needed HTML files
        ping_window = new PingingWindow(url, 1);
        ping_window.addVisibleListener(new ComponentListener () {
            @Override
            public void componentHidden(ComponentEvent e) {
                if (ping_window.finished) prepEntrants();
            }
            @Override
            public void componentShown(ComponentEvent e) {}
            @Override
            public void componentMoved(ComponentEvent e) {}
            @Override
            public void componentResized(ComponentEvent e) {}
        });
        ping_window.addCancelListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                ping_window.cancel_ping();
                ping_window.dispose();
                API.cleanTmpFiles();
            }
        });
        ping_window.launch();
    }
}
