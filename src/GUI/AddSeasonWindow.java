package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Backend.API;

public class AddSeasonWindow extends TemplateWindow {

    JLabel input_label = new JLabel("Enter name for new season");
    JLabel error_label = new JLabel("Error! Season name already in use!");
    JTextField input_field = new JTextField();
    JButton submit_button = new JButton("Add Season");

    public AddSeasonWindow() {

        // Set Window Attributes
        window.setTitle("Add New Season");
        window.setLayout(null);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);

        input_label.setFont(helveticaB24);
        input_label.setForeground(fg_color);

        error_label.setForeground(fg_color);
        error_label.setFont(helveticaB16);

        input_field.setFont(helvetica30);
        input_field.setBackground(Color.GRAY);
        input_field.setForeground(fg_color);

        // Set component sizes
        input_label.setSize(getTextWidth(input_label), 20);
        error_label.setSize(getTextWidth(error_label), 20);
        input_field.setSize(input_field.getFontMetrics(input_field.getFont()).stringWidth("a".repeat(20)), 40);
        submit_button.setSize(input_field.getWidth(), 50);

        // Set window width
        window.setSize(input_label.getWidth()+(4*edge), 0);

        // Set component locations
        input_label.setLocation(setCenter(input_label), edge);
        input_field.setLocation(setCenter(input_field), setBelow(input_label) + edge);
        error_label.setLocation(setCenter(error_label), setBelow(input_field) + edge);
        submit_button.setLocation(setCenter(submit_button), setBelow(error_label) + edge);

        // Set window height
        window.setSize(window.getWidth(), setBelow(submit_button)+(3*edge));

        // Set misc attributes
        error_label.setVisible(false);

        // Pack items into window
        window.add(input_field);
        window.add(input_label);
        window.add(error_label);
        window.add(submit_button);
    }

    public void addCustomListener(ActionListener e) {
        submit_button.addActionListener(e);
        input_field.addActionListener(e);
    }

    public String action() {
        String new_season = input_field.getText().strip();
        // Season already exists: do nothing
        if (API.checkSeasonExists(new_season)) {
            error_label.setVisible(true);
            return "";
        }
        // New season: add and close window
        else {
            API.createSeason(new_season);
            window.dispose();
            return new_season;
        }
    }
}