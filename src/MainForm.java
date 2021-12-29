import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainForm {
    private JButton btnCreate;
    private JLabel lblLogo;
    private JPanel pnlMain;
    private JLabel lblTitle;
    private JButton btnView;
    public JFrame frmMain;
    public static Font font;


    public MainForm() {

        Font ttfBase = null;
        InputStream myStream = null;
        String FONT_PATH = "Calibri Bold.TTF";

        try {
            myStream = new BufferedInputStream(
                    new FileInputStream(FONT_PATH));
            ttfBase = Font.createFont(Font.TRUETYPE_FONT, myStream);
            font = ttfBase.deriveFont(Font.PLAIN, 18f);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setupMainForm();

        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frmMain.setVisible(false);
                try {
                    new CreateScreen();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });

        btnView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frmMain.setVisible(false);
                try {
                    new ViewScreen();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });
    }

    private void setupMainForm() {

        btnCreate.setFont(font.deriveFont(Font.PLAIN, 30));
        btnCreate.setForeground(new Color(0,72,126));
        btnCreate.setMargin(new Insets(10, 2, 5, 2));

        btnView.setFont(font.deriveFont(Font.PLAIN, 30));
        btnView.setForeground(new Color(0,72,126));
        btnView.setMargin(new Insets(10, 2, 5, 2));

        frmMain = new JFrame("Pieces!");
        frmMain.setPreferredSize(new Dimension(800,600));
        pnlMain.setSize(frmMain.getSize());

        ImageIcon logo = new ImageIcon(new ImageIcon("images/logo.png").getImage().getScaledInstance(520/3, 294/3, Image.SCALE_DEFAULT));
        lblLogo.setIcon(logo);

        ImageIcon title = new ImageIcon(new ImageIcon("images/title.png").getImage().getScaledInstance(1422/4, 943/4, Image.SCALE_DEFAULT));
        lblTitle.setIcon(title);

        frmMain.add(pnlMain);
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmMain.pack();
        frmMain.setLocationRelativeTo(null);
        frmMain.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm();
            }
        });

    }
}
