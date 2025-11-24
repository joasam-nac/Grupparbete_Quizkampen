import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartMenu extends JFrame implements ActionListener {

    JFrame baseFrame = new JFrame("Quizkampen");
    JPanel northPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel southPanel = new JPanel();
    JLabel banner = new JLabel("\uD835\uDCAC\uD835\uDCCA\uD835\uDCF2\uD835\uDCCF\uD835\uDCC0\uD835\uDCEA\uD835\uDCC2\uD835\uDCC5\uD835\uDCEE\uD835\uDCC3");
    JLabel noGame = new JLabel();
    JButton newGame = new JButton("START GAME");


    public StartMenu() {
        baseFrame.add(startMenuNorth(), BorderLayout.NORTH);
        baseFrame.add(startMenuCenter(), BorderLayout.CENTER);
        baseFrame.add(startMenuSouth(), BorderLayout.SOUTH);



        baseFrame.pack();
        scaleWindow(1.5);
        baseFrame.setVisible(true);
        baseFrame.setLocationRelativeTo(null);
        baseFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void scaleWindow(double scale) {
        Dimension currentSize = baseFrame.getSize();
        int newWidth = (int) (currentSize.width * scale);
        int newHeight = (int) (currentSize.height * scale);
        baseFrame.setSize(newWidth, newHeight);
    }



    JPanel startMenuNorth() {
        Font font = new Font("", Font.PLAIN, 30);
        banner.setFont(font);
        banner.setPreferredSize(new Dimension(200, 50));
        northPanel.setLayout(new FlowLayout());
        northPanel.add(banner);
        return northPanel;
    }


    JPanel startMenuCenter() {
        noGame.setPreferredSize(new Dimension(400, 400));
        centerPanel.setLayout(new FlowLayout());
        centerPanel.add(noGame);
        return centerPanel;
    }

    JPanel startMenuSouth() {
        newGame.setPreferredSize(new Dimension(200, 50));
        southPanel.setLayout(new FlowLayout());
        southPanel.add(newGame);
        newGame.addActionListener(this);
        return southPanel;
    }

    public String checkUsername() {

        String input = JOptionPane.showInputDialog(null, "Enter Username: ");
        if (input == null) {
            System.out.println("User pressed quit");
        } else {
            System.out.println("User wrote: " + input);
        }
        return input;
    }




    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == newGame) {
            checkUsername();
            ChooseCategory start = new ChooseCategory();
            baseFrame.dispose();
        }

    }
}
