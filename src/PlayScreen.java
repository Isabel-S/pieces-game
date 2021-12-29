import com.sun.tools.javac.Main;
import net.coobird.thumbnailator.Thumbnails;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PlayScreen {
    static JFrame frmPlay;
    private static JPanel pnlHeader;
    private JPanel pnlPlay;
    private JPanel pnlPuzzle;
    private static JPanel pnlBoard;

    private JLabel lblTitle = new JLabel("Solve");
    private JButton btnHome = new JButton();

    private JLabel lblName = new JLabel("");

    private JPanel pnlGuide;
    private JLabel imgGuide = new JLabel();
    private JButton btnGuide = new JButton();


    PlayScreen(ContPuzzle contPuzzle) throws IOException {
        setupCreateScreen(contPuzzle);

        btnHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PuzzleHelper.goHome(frmPlay);
            }
        });
    }

    private void setupCreateScreen(ContPuzzle contPuzzle) throws IOException {
        ImageIcon home = new ImageIcon(Thumbnails.of("images/home.png")
                .size(40,40)
                .asBufferedImage());
        btnHome.setIcon(home);
        lblTitle.setFont(MainForm.font.deriveFont(Font.PLAIN, 40));
        lblTitle.setForeground(Color.white);
        lblName.setFont(MainForm.font.deriveFont(Font.PLAIN, 25));
        lblName.setForeground(Color.white);

        frmPlay = new JFrame();
        frmPlay.setSize(800,600);
        frmPlay.setLocationRelativeTo(null);

        pnlHeader = new JPanel();
        pnlHeader.setOpaque(false);
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.X_AXIS));
        pnlHeader.add(lblTitle);
        pnlHeader.add(Box.createRigidArea(new Dimension(350,0)));
        pnlHeader.add(btnHome);

        lblName.setText(contPuzzle.name + " - " + contPuzzle.contName);

        pnlPuzzle = new JPanel();
        pnlPuzzle.setLayout(null);
        pnlPuzzle.setSize(800,pnlPuzzle.getHeight());
        pnlPuzzle.setBackground(new Color(206, 192, 232));

        pnlBoard = new JPanel();
        pnlGuide = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0) );

        contPuzzle.displayPuzzle(pnlPuzzle);

        pnlPlay = new JPanel();
        pnlPlay.setLayout(new BoxLayout(pnlPlay, BoxLayout.Y_AXIS));
        pnlPlay.add(pnlHeader);
        pnlPlay.add(lblName);
        pnlPlay.add(pnlPuzzle);
        pnlPlay.setBackground(new Color(102, 91, 220));
        pnlHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlPuzzle.add(pnlBoard);
        pnlPuzzle.add(btnGuide);

        pnlBoard.setSize(contPuzzle.fullWidth, contPuzzle.fullHeight);
        pnlBoard.setLayout(null);
        pnlBoard.setLocation(contPuzzle.xBoard,contPuzzle.yBoard);

        int widthGuide = 800-(contPuzzle.xBoard+ contPuzzle.fullWidth)-30;
        ImageIcon guide = new ImageIcon(Thumbnails.of(contPuzzle.fullImage)
                .size(widthGuide, 600)
                .asBufferedImage());
        int xGuide = contPuzzle.xBoard+ contPuzzle.fullWidth+15;

        pnlGuide.setOpaque(true);
        pnlGuide.add(imgGuide);
        btnGuide.add(pnlGuide);

        imgGuide.setIcon(guide);
        imgGuide.setAlignmentY(Component.CENTER_ALIGNMENT);
        imgGuide.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnGuide.setSize(guide.getIconWidth()+5,guide.getIconHeight()+5);
        btnGuide.setLocation(xGuide,contPuzzle.yBoard);

        frmPlay.add(pnlPlay);
        frmPlay.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmPlay.setVisible(true);

    }

    public static void lockPiece(JPanel piece){
        piece.setVisible(false);
        pnlBoard.add(piece);
        piece.setVisible(true);
    }
}
