package GUI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import MyUtils.*;
import DBase.DBManager;

public class GetLink {

    String title = "";
    
    JLabel label = new JLabel("Enter Challonge link");
    JLabel example = new JLabel("Format: https://challonge.com/tournament_name");
    JLabel error = new JLabel("Entered url is invalid. Please try again");
    JButton submit = new JButton("Submit");
    JTextArea area = new JTextArea();
    JFrame window = new JFrame(title);

    public void Launch(DBManager db) {
        label.setBounds(0,0,200,30);
        area.setBounds(0, 30, 300, 40);
        example.setBounds(0, 70, 500, 20);
        error.setBounds(0, 95, 250, 20);

        submit.setBounds(25, 115, 200, 40);
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(db);
            }
        });

        window.add(label); window.add(area); window.add(submit); window.add(example); window.add(error);
        window.setSize(500, 250);
        window.setLayout(null);
        error.setVisible(false);
        window.setVisible(true);
    }

    public void action(DBManager db) {
        System.out.println("Error! Action was not overwritted!");
    }
}