package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Backend.API;
import Backend.BracketData;
import Backend.Set;

public class PreSeedingWindow extends GetLink {

    static String title = "Seed Bracket";

    SeedingWindow seeding_window;
    GetAliasWindow get_alias_window;
    ConflictsWindow conflicts_window;
    PingingWindow ping_window;

    JCheckBox check_box = new JCheckBox("Shuffle seeding");
    SpinnerModel rounds_model = new SpinnerNumberModel(2, //initial value
                                                   0, //minimum value
                                                   5, //maximum value
                                                   1); //step
    SpinnerModel tournies_model = new SpinnerNumberModel(1, //initial value
                                                   1, //minimum value
                                                   4, //maximum value
                                                   1); //step
    JSpinner rounds_val = new JSpinner(rounds_model);
    JSpinner tournies_val = new JSpinner(tournies_model);
    JLabel shake_label = new JLabel("Reseed to avoid recent matchups?");
    JLabel rounds_label = new JLabel("Reseed through how many rounds?");
    JLabel tournies_label = new JLabel("Reference how many brackets ago?");

    public PreSeedingWindow() {
        // Set Window Attributes
        window.setTitle(title);

        // Set fonts and colors
        shake_label.setFont(helveticaB12);
        shake_label.setForeground(fg_color);

        rounds_label.setFont(helveticaB12);
        rounds_label.setForeground(fg_color);

        tournies_label.setFont(helveticaB12);
        tournies_label.setForeground(fg_color);

        check_box.setBackground(bg_color);

        // Set component sizes
        shake_label.setSize(getTextWidth(shake_label),20);
        rounds_label.setSize(getTextWidth(rounds_label),20);
        tournies_label.setSize(getTextWidth(tournies_label),20);
        check_box.setSize(20, 20);
        rounds_val.setSize(30, 20);
        tournies_val.setSize(rounds_val.getWidth(), rounds_val.getHeight());

        // Set component locations
        shake_label.setLocation(setCenter(shake_label)-offset, setBelow(submit)+offset);
        check_box.setLocation(setRight(shake_label)+offset, shake_label.getY());
        rounds_label.setLocation(setCenter(rounds_label)-offset,setBelow(check_box));
        tournies_label.setLocation(setCenter(tournies_label)-offset, setBelow(rounds_label));
        rounds_val.setLocation(check_box.getX(), rounds_label.getY());
        tournies_val.setLocation(check_box.getX(), tournies_label.getY());

        // Set window height
        window.setSize(window.getWidth(), setBelow(tournies_val)+(edge*3));

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
                tournies_label.setVisible(!tournies_label.isVisible());
                tournies_val.setVisible(!tournies_val.isVisible());
            }
        });

        // Set misc settings
        ((JSpinner.DefaultEditor)rounds_val.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor)tournies_val.getEditor()).getTextField().setEditable(false);

        // Pack items into window
        window.add(check_box);
        window.add(rounds_val);
        window.add(shake_label);
        window.add(rounds_label);
        window.add(tournies_label);
        window.add(tournies_val);

        // Set element visability
        rounds_label.setVisible(false);
        rounds_val.setVisible(false);
        tournies_label.setVisible(false);
        tournies_val.setVisible(false);
    }

    private void prepSeedingWindow(String [] entrants) {
        // Check number of rounds to reseeded around conflict s
        int shake_rounds = -1;
        int tournies_back = 0;
        if (check_box.isSelected()) {
            try {
                rounds_val.commitEdit();
                tournies_val.commitEdit();
                shake_rounds = (int) rounds_val.getValue();
                tournies_back = (int) tournies_val.getValue();
            } catch (Exception e) {};
        }

        BracketData br_data = API.makeBracket(entrants, shake_rounds, tournies_back);
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

        seeding_window = new SeedingWindow(br_data.entrants, sets, shake_rounds, tournies_back);
        seeding_window.launch();
        if (br_data.conflicts.length > 0) {
            conflicts_window = new ConflictsWindow(br_data, shake_rounds);
            conflicts_window.launch();
        }
    }

    public void prepEntrants() {
        String [] entrants = API.getEntrants();
        String [] unknown_entrants = API.checkUnknownNames(entrants);
        if (unknown_entrants.length != 0) {
            get_alias_window = new GetAliasWindow(unknown_entrants);
            // Add action listener to GA window so this window closes at same time
            get_alias_window.addCustomWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    window.setVisible(false);
                    get_alias_window.dispose();
                    prepSeedingWindow(entrants);
                }
            });
            get_alias_window.launch();
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
        if (!Utils.validURL(url)) {
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
                window.setEnabled(true);
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
                ping_window.cancelPing();
                ping_window.dispose();
                API.cleanTmpFiles();
                window.setEnabled(true);
            }
        });
        ping_window.launch();
    }
}
