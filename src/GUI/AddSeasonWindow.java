package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class AddSeasonWindow extends TemplateWindow {

    JLabel input_label = new JLabel("Enter name for new season");
    JLabel error_label = new JLabel("Error! Season name already in use!");
    JTextField input_field = new JTextField();
    JButton submit_button = new JButton("Add Season");

    int max_name_length = 20;

    public AddSeasonWindow() {

        // Set Window Attributes
        window.setTitle("Add New Season");
        window.setLayout(null);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);

        input_label.setFont(helveticaB24);
        input_label.setForeground(Color.WHITE);

        error_label.setForeground(Color.WHITE);
        error_label.setFont(helveticaB16);

        input_field.setFont(helvetica30);
        input_field.setBackground(Color.GRAY);
        input_field.setForeground(Color.WHITE);

        // Set component sizes
        input_label.setSize(getTextWidth(input_label), 20);
        error_label.setSize(getTextWidth(error_label), 20);
        input_field.setSize(input_field.getFontMetrics(input_field.getFont()).stringWidth("a".repeat(max_name_length)), 40);
        submit_button.setSize(input_field.getWidth(), 50);

        // Set window width
        window.setSize(input_label.getWidth()+(4*edge), 0);

        // Set component locations
        input_label.setLocation(getCenter(input_label)-(offset*2), 10);
        input_field.setLocation(getCenter(input_field)-(offset*2), input_label.getY()+input_field.getHeight() + 10);
        error_label.setLocation(getCenter(error_label)-(offset*2), input_field.getY()+input_field.getHeight() + 10);
        submit_button.setLocation(getCenter(submit_button)-(offset*2), error_label.getY()+error_label.getHeight() + 10);

        // Set window height
        window.setSize(window.getWidth(), submit_button.getY() + submit_button.getHeight()+(3*edge));

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