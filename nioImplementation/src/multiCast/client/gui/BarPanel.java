package multiCast.client.gui;

import multiCast.client.kernel.EntitiesClientImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Cirie on 02/11/2014.
 */
public class BarPanel extends JPanel{
    private JButton submit;
    private JButton burst;
    private JTextField textArea;
    private EntitiesClientImpl client;

    public BarPanel(){

        textArea = new JTextField();
        textArea.setPreferredSize(new Dimension(400,30));

        submit = new JButton("submit");
        submit.setBackground(Color.white);
        submit.addActionListener(new SubmitListener());

        burst = new JButton("burst");
        burst.setBackground(Color.red);
        burst.addActionListener(new BurstListener());

        this.add(textArea,BorderLayout.EAST);
        this.add(submit,BorderLayout.CENTER);
        this.add(burst,BorderLayout.WEST);
    }

    private class SubmitListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            client.sendMessageToEveryBody(textArea.getText());
            textArea.setText("");
        }
    }

    private class BurstListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
}
