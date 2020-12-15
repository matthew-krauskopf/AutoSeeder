package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Backend.API;

public class GetLink extends TemplateWindow {
    JLabel label = new JLabel("Enter Challonge.com URL");
    JLabel example = new JLabel("Format: https://challonge.com/tourney_id");
    JLabel error = new JLabel("Error! Entered url is invalid. Please try again");
    JLabel f_error = new JLabel("Error! URL is formatted incorrectly.");
    JButton submit = new JButton("Submit");
    JTextField field = new JTextField();

    public GetLink() {
        // Set Window Attributes
        window.setTitle("");
        window.getContentPane().setBackground(bg_color);
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        field.setFont(helvetica16);
        field.setBackground(Color.GRAY);
        field.setForeground(fg_color);

        label.setFont(helveticaB16);
        label.setForeground(fg_color);

        example.setFont(helveticaB12);
        example.setForeground(fg_color);

        error.setFont(helveticaB12);
        error.setForeground(fg_color);

        f_error.setFont(helveticaB12);
        f_error.setForeground(fg_color);

        submit.setFont(helveticaB16);

        // Set component sizes
        field.setSize(250, 24);
        label.setSize(getTextWidth(label),20);
        example.setSize(getTextWidth(example), 20);
        error.setSize(getTextWidth(error), 20);
        f_error.setSize(getTextWidth(f_error), 20);
        submit.setSize(field.getWidth(), 40);
        window.setSize((2*offset)+field.getWidth()+edge, 250);

        // Set component locations
        field.setLocation(offset,label.getHeight()+(2*offset));
        label.setLocation(setCenter(label), offset);
        example.setLocation(setCenter(example), 70);
        error.setLocation(setCenter(error), setBelow(example));
        f_error.setLocation(setCenter(f_error), setBelow(example));
        submit.setLocation(setCenter(submit), setBelow(error));

        // Add action listeners
        ActionListener submit_action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setEnabled(false);
                action();
                window.setEnabled(true);
            }
        };

        field.addActionListener(submit_action);
        submit.addActionListener(submit_action);

        // Pack items into window
        window.add(label);
        window.add(field);
        window.add(submit);
        window.add(example);
        window.add(error);
        window.add(f_error);

        // Set starting visibility
        error.setVisible(false);
        f_error.setVisible(false);
    }

    public void action() {
        System.out.println("Error! Action was not overwritted!");
    }
}
