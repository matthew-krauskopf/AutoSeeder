package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

import MyUtils.*;

public class GetLink {

    String title = "";
    
    static Font font = new Font("Helvetica", Font.BOLD, 16);
    static Font font2 = new Font("Helvetica", 0, 16);
    static Font font3 = new Font("Helvetica", Font.BOLD, 12);
    JLabel label = new JLabel("Enter Challonge.com URL");
    JLabel example = new JLabel("Format: https://challonge.com/tourney_id");
    JLabel error = new JLabel("Error! Entered url is invalid. Please try again");
    JButton submit = new JButton("Submit");
    JTextField field = new JTextField();
    JFrame window = new JFrame(title);
    Color bg_color = new Color(46, 52, 61);

    int edge = 17;
    int offset = 10;

    public GetLink() {
        // Set Window Attributes
        window.getContentPane().setBackground(bg_color);
        window.setLayout(null);
        window.setResizable(false);

        // Set fonts and colors
        field.setFont(font2);
        field.setBackground(Color.GRAY);
        field.setForeground(Color.WHITE);

        label.setFont(font);
        label.setForeground(Color.WHITE);

        example.setFont(font3);
        example.setForeground(Color.WHITE);

        error.setFont(font3);
        error.setForeground(Color.WHITE);

        submit.setFont(font);

        // Set component sizes
        field.setSize(250, 24);
        label.setSize(get_text_width(label),20);
        example.setSize(get_text_width(example), 20);
        error.setSize(get_text_width(error), 20);
        submit.setSize(field.getWidth(), 40);

        // Set component locations
        field.setLocation(offset,label.getHeight()+(2*offset));
        label.setLocation(get_center(label), offset);
        example.setLocation(get_center(example), 70);
        error.setLocation(get_center(error), example.getHeight()+example.getY());
        submit.setLocation(get_center(submit), error.getHeight()+error.getY());

        // Add action listeners
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action();
            }
        });

        // Pack items into window
        window.add(label);
        window.add(field);
        window.add(submit);
        window.add(example);
        window.add(error);
    }

    public void Launch() {
        // Set elements to visible
        window.setSize((2*offset)+field.getWidth()+edge, 250);
        error.setVisible(false);
        window.setVisible(true);
    }

    public int get_text_width(JLabel l) {
        return l.getFontMetrics(l.getFont()).stringWidth(l.getText());
    }

    public int get_center(JLabel l) {
        return offset+((int)(field.getWidth()/2)) - (int)(l.getWidth()/2);
    }

    public int get_center(JButton b) {
        return offset+((int)(field.getWidth()/2)) - (int)(b.getWidth()/2);
    }

    public void action() {
        System.out.println("Error! Action was not overwritted!");
    }
}