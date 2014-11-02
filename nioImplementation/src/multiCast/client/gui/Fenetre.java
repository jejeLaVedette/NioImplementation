package multiCast.client.gui;

import multiCast.client.gui.console.ConsolePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Cirie on 02/11/2014.
 */
public class Fenetre extends JFrame{
    private final JTabbedPane onglet;
    private final ConsolePanel console;

    public Fenetre() {
        this.setTitle("Multicast Client");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setBackground(Color.white);

        console = new ConsolePanel();
        onglet = new JTabbedPane();
        onglet.add("console", console);
        this.add(onglet, BorderLayout.CENTER);
        this.add(new BarPanel(),BorderLayout.SOUTH);
        this.setVisible(true);
    }
}
