import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameRunning extends JFrame implements ActionListener {

    JFrame baseFrame = new JFrame("Quizkampen");
    JPanel northPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel southPanel = new JPanel(new GridLayout(2, 2));
    JPanel eastPanel = new JPanel();
    JPanel westPanel = new JPanel();
    JLabel banner = new JLabel("\uD835\uDCAC\uD835\uDCCA\uD835\uDCF2\uD835\uDCCF\uD835\uDCC0\uD835\uDCEA\uD835\uDCC2\uD835\uDCC5\uD835\uDCEE\uD835\uDCC3");
    JLabel labelForQuiz = new JLabel("Space for quiz questions");
    JButton quizAnswer1 = new JButton();
    JButton quizAnswer2 = new JButton();
    JButton quizAnswer3 = new JButton();
    JButton quizAnswer4 = new JButton();
    JLabel eastChatWindow = new JLabel();
    JLabel westFillout = new JLabel();
    JLabel southFillout = new JLabel("\uD835\uDCA2\uD835\uDCF8\uD835\uDCF8\uD835\uDCED \uD835\uDCDB\uD835\uDCFE\uD835\uDCEC\uD835\uDCF4！");



    public GameRunning() {
        baseFrame.add(gameRunningNorth(), BorderLayout.NORTH);
        baseFrame.add(gameRunningCenter(), BorderLayout.CENTER);
        baseFrame.add(gameRunningSouth(), BorderLayout.SOUTH);
        baseFrame.add(gameRunningEast(), BorderLayout.EAST);
        baseFrame.add(gameRunningWest(), BorderLayout.WEST);


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


    JPanel gameRunningNorth() {
        Font font = new Font("", Font.PLAIN, 30);
        banner.setFont(font);
        banner.setPreferredSize(new Dimension(200, 50));
        northPanel.setLayout(new FlowLayout());
        northPanel.add(banner);
        return northPanel;
    }


    JPanel gameRunningCenter() {
        centerPanel.setLayout(new BorderLayout(50, 150));


        labelForQuiz.setPreferredSize(new Dimension(200, 75));
        labelForQuiz.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(labelForQuiz, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        quizAnswer1.setPreferredSize(new Dimension(100, 50));
        quizAnswer2.setPreferredSize(new Dimension(100, 50));
        quizAnswer3.setPreferredSize(new Dimension(100, 50));
        quizAnswer4.setPreferredSize(new Dimension(100, 50));

        buttonPanel.add(quizAnswer1);
        buttonPanel.add(quizAnswer2);
        buttonPanel.add(quizAnswer3);
        buttonPanel.add(quizAnswer4);

        quizAnswer1.addActionListener(this);
        quizAnswer2.addActionListener(this);
        quizAnswer3.addActionListener(this);
        quizAnswer4.addActionListener(this);

        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        return centerPanel;
    }

    JPanel gameRunningSouth() {
        Font font = new Font("", Font.PLAIN, 30);
        southFillout.setFont(font);
        southFillout.setPreferredSize(new Dimension(200, 50));
        southPanel.setBackground(Color.CYAN);
        southPanel.setLayout(new FlowLayout());
        southPanel.add(southFillout);
        return southPanel;
    }


    JPanel gameRunningEast() {
        eastChatWindow.setPreferredSize(new Dimension(180, 400));
        eastPanel.setBackground(Color.RED);
        eastPanel.setLayout(new FlowLayout());
        eastPanel.add(eastChatWindow);
        return eastPanel;
    }

    JPanel gameRunningWest() {
        westFillout.setPreferredSize(new Dimension(20, 400));
        westPanel.setBackground(Color.BLUE);
        westPanel.setLayout(new FlowLayout());
        westPanel.add(westFillout);
        return westPanel;
    }







    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
