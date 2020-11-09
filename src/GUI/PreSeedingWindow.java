package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class PreSeedingWindow extends GetLink {

    static String title = "Seed Bracket";

    SeedingWindow S_Window;
    JCheckBox check_box = new JCheckBox("Shuffle seeding");
    SpinnerModel sp_model = new SpinnerNumberModel(2, //initial value
                                                   1, //minimum value
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
        API.makeHTMLFiles(url, 1);

        int shake_rounds = 0;
        if (check_box.isSelected()) {
            try {
                rounds_val.commitEdit();
                shake_rounds = (Integer) rounds_val.getValue();
            } catch (Exception e) {};
        }
        String [] entrants = API.getBracket(shake_rounds);
        // No entrants: Wrong URL?
        if (entrants.length<=1) {
            f_error.setVisible(false);
            error.setVisible(true);
            return;
        }
        // Close window
        window.dispose();
        // Show initial assignments
        Set[] sets = API.getSets(entrants);
        // Done with HTML data: clean tmp files
        API.cleanTmpFiles();
        S_Window = new SeedingWindow(entrants, sets);
        S_Window.launch();
    }
}
