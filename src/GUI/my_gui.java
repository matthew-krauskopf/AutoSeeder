package GUI;

import java.io.*;  
import java.awt.*;  
import java.awt.event.*;  
import javax.swing.*;  
import MyUtils.*;
import DBase.DBManager;

public class my_gui {

    public static void main(String args[]) {
        main_menu();
    }

    public static void URL_menu() {
        JFrame popup = new JFrame("GetUrl");


        JLabel label = new JLabel("Enter Challonge link");
        label.setBounds(0,0,200,30);
 
        JTextArea area = new JTextArea();
        area.setBounds(0, 30, 250, 40);
        
        JButton submit = new JButton("Submit");
        submit.setBounds(25, 70, 200, 40);
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String url = area.getText().trim();
                popup.setVisible(false);
                String [] entrants = WebData.grab_entrants(url);
                if (entrants.length == 0) return;
                int [] rankings = DBManager.grab_scores(entrants);
                Bracket.seed_bracket(entrants, rankings);
                // Show initial assignments
                Bracket.show_bracket(entrants);
            }
        });
        
        popup.add(label); popup.add(area); popup.add(submit);
        popup.setSize(300, 250);
        popup.setLayout(null);
        popup.setVisible(true);
    }

    public static void main_menu() {
        JFrame f = new JFrame("AutoBracket");
        
        // Create Buttons
        JButton b1 = new JButton("Seed Bracket");
        b1.setBounds(40,30,200, 40);//x axis, y axis, width, height  
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                URL_menu();
            }
        });
        JButton b2 = new JButton("Import Results");
        b2.setBounds(40,90,200, 40);//x axis, y axis, width, height 
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Works!");
            }
        }); 

        JButton b3 = new JButton("View Rankings");
        b3.setBounds(40, 150, 200, 40);
        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Works!");
            }
        });
          
        f.add(b1);//adding button in JFrame  
        f.add(b2);
        f.add(b3);
                
        f.setSize(300,250);
        f.setLayout(null);//using no layout managers  
        f.setVisible(true);//making the frame visible  
    }
}