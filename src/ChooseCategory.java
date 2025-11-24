import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChooseCategory extends JFrame implements ActionListener {
    JFrame baseFrame = new JFrame("Quizkampen");
    JPanel centerPanel = new JPanel(new GridLayout(2, 2));
    JButton choice1 = new JButton();
    JButton choice2 = new JButton();
    JButton choice3 = new JButton();
    JButton choice4 = new JButton();






    public ChooseCategory() {
        baseFrame.add(chooseCenter(), BorderLayout.CENTER);


        baseFrame.pack();
        baseFrame.setVisible(true);
        baseFrame.setLocationRelativeTo(null);
        baseFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    JPanel chooseCenter() {
        choice1.setPreferredSize(new Dimension(200, 100));
        choice2.setPreferredSize(new Dimension(200, 100));
        choice3.setPreferredSize(new Dimension(200, 100));
        choice4.setPreferredSize(new Dimension(200, 100));

        centerPanel.add(choice1);
        centerPanel.add(choice2);
        centerPanel.add(choice3);
        centerPanel.add(choice4);

        choice1.addActionListener(this);
        choice2.addActionListener(this);
        choice3.addActionListener(this);
        choice4.addActionListener(this);

        return centerPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == choice1 || source == choice2 || source == choice3 || source == choice4) {
            GameRunning start = new GameRunning();
            baseFrame.dispose();
        }

    }

}
