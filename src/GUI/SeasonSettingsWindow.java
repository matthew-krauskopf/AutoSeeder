package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;

public class SeasonSettingsWindow extends TemplateWindow {

    JLabel title_label = new JLabel("Title: ");
    JLabel created_label = new JLabel("Created on: ");
    JLabel tournies_label = new JLabel("Tournaments recorded: ");
    JLabel error_label = new JLabel("Error! Season name already in use!");
    JTextField input_field = new JTextField();
    JButton update_button = new JButton("Update");
    JButton delete_button = new JButton("Delete Season");

    int max_name_length = 20;
    String season_name;

    public SeasonSettingsWindow(String fed_season_name) {
        season_name = fed_season_name;
        input_field.setText(season_name);

        // Set Window Attributes
        window.setTitle("Settings");
        window.setLayout(null);

        // Set fonts and colors
        window.getContentPane().setBackground(bg_color);

        title_label.setFont(acumin16);
        title_label.setForeground(Color.WHITE);

        created_label.setFont(acumin16);
        created_label.setForeground(Color.WHITE);

        tournies_label.setFont(acumin16);
        tournies_label.setForeground(Color.WHITE);
        
        error_label.setFont(acumin12);
        error_label.setForeground(Color.WHITE);

        input_field.setFont(acumin16);
        input_field.setBackground(Color.GRAY);
        input_field.setForeground(Color.WHITE);

        update_button.setMargin(new Insets(0,0,0,0));

        // Set component sizes
        title_label.setSize(getTextWidth(title_label), 20);
        error_label.setSize(getTextWidth(error_label), 16);
        created_label.setSize(getTextWidth(created_label), 20);
        tournies_label.setSize(getTextWidth(tournies_label), 20);
        input_field.setSize(input_field.getFontMetrics(input_field.getFont()).stringWidth("a".repeat(max_name_length)), 20);
        
        update_button.setSize(getTextWidth(update_button)+10, 20);
        delete_button.setSize(input_field.getWidth(), 30);

        // Set window width
        window.setSize(title_label.getWidth()+input_field.getWidth()+update_button.getWidth()+(2*edge)+25, 0);

        // Set component locations
        title_label.setLocation(5, 10);
        input_field.setLocation(title_label.getX() + title_label.getWidth() + 10, title_label.getY()+2);
        error_label.setLocation(input_field.getX(), input_field.getY()+input_field.getHeight() + 2);
        created_label.setLocation(title_label.getX(), error_label.getY()+error_label.getHeight()+10);
        tournies_label.setLocation(title_label.getX(), created_label.getY()+created_label.getHeight()+10);
        
        update_button.setLocation(input_field.getX() + input_field.getWidth() + 10, input_field.getY());
        delete_button.setLocation(getCenter(delete_button)-(offset*2), tournies_label.getY()+tournies_label.getHeight() + 10);

        // Set window height
        window.setSize(window.getWidth(), delete_button.getY() + delete_button.getHeight()+(3*edge));

        // Set misc attributes
        error_label.setVisible(false);

        // Pack items into window
        window.add(input_field);
        window.add(title_label);
        window.add(created_label);
        window.add(tournies_label);
        window.add(error_label);
        window.add(update_button);
        window.add(delete_button);
    }

    public void addCustomUpdateListener(ActionListener e) {
        update_button.addActionListener(e);
    }

    public void addCustomDeleteListener(ActionListener e) {
        delete_button.addActionListener(e);
    }

    public String [] updateName() {
        String new_name = input_field.getText().strip();
        if (!new_name.equals("")) {
            String [] in_use = API.getSeasons();
            if (!API.isIn(new_name, in_use)) {
                error_label.setVisible(false);
                String old_name = season_name;
                season_name = new_name;
                API.updateSeasonName(old_name, season_name);
                return new String [] {old_name, season_name};
            } else {
                error_label.setVisible(true);
            }
        }
        return new String [2];
    }

    public String delete() {
        // TODO: Add sanity check to ensure correct action
        API.deleteSeason(season_name);
        window.dispose();
        return season_name;
    }
}