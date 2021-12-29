import net.coobird.thumbnailator.Thumbnails;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.Scrollable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class ViewScreen {
    static JFrame frmView;
    private static JPanel pnlView;
    private static JPanel pnlHeader;
    private JPanel pnlOptions;
    private JPanel pnlPuzzles = new JPanel();
    private JPanel pnlVersions = new JPanel();
    private JScrollPane pnlScroll = new JScrollPane(pnlPuzzles);


    private JLabel lblTitle = new JLabel("Puzzles");
    private JButton btnHome = new JButton();
    private JButton btnBack = new JButton();

    private JTextField txtName = new JTextField(10);
    private JButton btnRename = new JButton();
    private JButton btnShare = new JButton();
    private JButton btnDelete = new JButton();
    private JButton btnNew = new JButton();
    private JButton btnSaved = new JButton("View Saved");
    private JButton btnContinue = new JButton("Continue");

    Puzzle selectedPuzzle;
    ContPuzzle selectedContPuzzle;

    ViewScreen() throws IOException {
        setupViewScreen();
        /*
        * go through puzzle csv
        * for each one, get name, image and row number
        * call on thumbnail
         */
        displayThumbnailsMain(true);

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayThumbnailsMain(false);
            }
        });

        btnHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PuzzleHelper.goHome(frmView);
            }
        });

        btnSaved.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayThumbnailsSaved(selectedPuzzle);
            }
        });

        btnNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PuzzleHelper.contPuzzleDialog(selectedPuzzle, "Enter a name for the puzzle version:", frmView);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        btnRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (pnlPuzzles.isVisible()) {
                        if (PuzzleHelper.isNewName(txtName.getText())) {
                            PuzzleHelper.renamePuzzle(txtName.getText(), selectedPuzzle.id);
                            displayThumbnailsMain(true);
                        } else {
                            PuzzleHelper.errorDialog(txtName.getText() +
                                    "' is already used. Please write a new name for the puzzle version");
                        }
                    } else {
                        if (PuzzleHelper.isNewNameCont(txtName.getText(), selectedContPuzzle.id)) {
                            PuzzleHelper.renameContPuzzle(txtName.getText(), selectedPuzzle.id,
                                    selectedContPuzzle.contName);
                            displayThumbnailsSaved(selectedContPuzzle);
                        } else {
                            PuzzleHelper.errorDialog(txtName.getText() +
                                    "' is already used. Please write a new name for the puzzle version");
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (pnlPuzzles.isVisible()) {
                        PuzzleHelper.deletePuzzle(selectedPuzzle.id);
                        displayThumbnailsMain(true);
                    } else {
                        PuzzleHelper.deleteContPuzzle(selectedContPuzzle.contName,
                                selectedContPuzzle.id);
                        displayThumbnailsSaved(selectedContPuzzle);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        btnShare.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (pnlPuzzles.isVisible()) {
                        PuzzleHelper.sharePuzzle(selectedPuzzle);
                    } else {
                        PuzzleHelper.shareContPuzzle(selectedContPuzzle);
                    }
                    Object[] optionsArray = {"OK"};
                    PuzzleHelper.imageDialog("Downloaded!",
                            "This puzzle's zip file is located in the 'shareable_puzzles' folder"
                            , selectedPuzzle.file, optionsArray);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        btnContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frmView.setVisible(false);
                try {
                    new PlayScreen(selectedContPuzzle);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    private void setupViewScreen() throws IOException {
        ImageIcon home = new ImageIcon(Thumbnails.of("images/home.png")
                .size(40,40)
                .asBufferedImage());
        btnHome.setIcon(home);

        ImageIcon rename = new ImageIcon(Thumbnails.of("images/rename.png")
                .size(20,20)
                .asBufferedImage());
        btnRename.setIcon(rename);

        ImageIcon share = new ImageIcon(Thumbnails.of("images/share.png")
                .size(25,25)
                .asBufferedImage());
        btnShare.setIcon(share);

        ImageIcon delete = new ImageIcon(Thumbnails.of("images/delete.png")
                .size(25,25)
                .asBufferedImage());
        btnDelete.setIcon(delete);

        ImageIcon newPuzzle = new ImageIcon(Thumbnails.of("images/new.png")
                .size(25,25)
                .asBufferedImage());
        btnNew.setIcon(newPuzzle);

        ImageIcon back = new ImageIcon(Thumbnails.of("images/back.png")
                .size(40,40)
                .asBufferedImage());
        btnBack.setIcon(back);

        lblTitle.setFont(MainForm.font.deriveFont(Font.PLAIN, 40));
        lblTitle.setForeground(new Color(0,72,126));
        btnSaved.setFont(MainForm.font);
        btnSaved.setMargin(new Insets(5, 2, 0, 2));
        btnSaved.setForeground(new Color(0,72,126));
        txtName.setFont(MainForm.font);
        btnContinue.setFont(MainForm.font);
        btnContinue.setMargin(new Insets(5, 2, 0, 2));
        btnContinue.setForeground(new Color(0,72,126));

        frmView = new JFrame();
        frmView.setSize(800,600);
        frmView.setLocationRelativeTo(null);

        pnlHeader = new JPanel();
        pnlHeader.setOpaque(false);
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.X_AXIS));
        pnlHeader.add(lblTitle);
        pnlHeader.add(Box.createRigidArea(new Dimension(330,0)));
        pnlHeader.add(btnBack);
        pnlHeader.add(btnHome);
        btnBack.setVisible(false);

        txtName.setMaximumSize( txtName.getPreferredSize() );

        pnlOptions = new JPanel();
        pnlOptions.setOpaque(false);
        pnlOptions.setLayout(new BoxLayout(pnlOptions, BoxLayout.X_AXIS));
        pnlOptions.add(txtName);
        pnlOptions.add(btnRename);
        pnlOptions.add(Box.createRigidArea(new Dimension(70,0)));
        pnlOptions.add(btnShare);
        pnlOptions.add(Box.createRigidArea(new Dimension(20,0)));
        pnlOptions.add(btnDelete);
        pnlOptions.add(Box.createRigidArea(new Dimension(20,0)));
        pnlOptions.add(btnNew);
        pnlOptions.add(Box.createRigidArea(new Dimension(70,0)));
        pnlOptions.add(btnSaved);
        pnlOptions.add(Box.createRigidArea(new Dimension(20,0)));
        pnlOptions.add(btnContinue);
        btnContinue.setVisible(false);
        pnlOptions.setVisible(false);

        pnlPuzzles.setBackground(new Color(102, 91, 220));
        pnlPuzzles.setLayout(new GridLayout(0,3));

        pnlVersions.setBackground(new Color(102, 91, 220));
        pnlVersions.setLayout(new GridLayout(0,3));
        pnlVersions.setVisible(false);

        pnlScroll.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        pnlView = new JPanel();
        pnlView.setLayout(new BoxLayout(pnlView, BoxLayout.Y_AXIS));
        pnlView.add(pnlHeader);
        pnlView.add(pnlOptions);
        pnlView.add(pnlScroll);
        pnlView.setBackground(new Color(206, 192, 232));

        frmView.add(pnlView);
        frmView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmView.setVisible(true);

    }

    private void displayThumbnailsMain(boolean refresh) {
        if(refresh) {
            pnlPuzzles.removeAll();
            try {
                String line = "";
                String splitBy = ",";
                BufferedReader br = new BufferedReader(new FileReader("puzzles.csv"));

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(splitBy, -1);
                    if (data.length > 5){
                        Puzzle puzzle = new Puzzle(Integer.parseInt(data[0]), data[1], data[2],
                                Integer.parseInt(data[3]), Integer.parseInt(data[4]), data[5]);
                        Thumbnail thumbnail = new Thumbnail(puzzle);
                        pnlPuzzles.add(thumbnail.btnThumbnail);
                    } else if (data.length > 1) {
                        Puzzle puzzle = new Puzzle(Integer.parseInt(data[0]), data[1], data[2],
                                Integer.parseInt(data[3]), Integer.parseInt(data[4]));
                        Thumbnail thumbnail = new Thumbnail(puzzle);
                        pnlPuzzles.add(thumbnail.btnThumbnail);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pnlVersions.setVisible(false);
        pnlPuzzles.setVisible(true);
        pnlScroll.setViewportView(pnlPuzzles);
        btnSaved.setVisible(true);
        btnNew.setVisible(true);
        btnContinue.setVisible(false);
        btnBack.setVisible(false);
        pnlOptions.setVisible(false);
    }

    private void displayThumbnailsSaved(Puzzle puzzle) {
        pnlVersions.removeAll();

        String line = "";
        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new FileReader("contPuzzles.csv"));
            //int lineNum = 0;
            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(splitBy, -1);
                if (data.length>0) {

                    if (data[0].equals(Integer.toString(puzzle.id))) {
                        ContPuzzle contPuzzle = new ContPuzzle(puzzle, data[1], data[2]);
                        ThumbnailSaved thumbnail = new ThumbnailSaved(contPuzzle);
                        pnlVersions.add(thumbnail.btnThumbnail);
                    }
                }
            }
            pnlVersions.setVisible(true);
            pnlPuzzles.setVisible(false);
            pnlScroll.setViewportView(pnlVersions);
            btnSaved.setVisible(false);
            btnNew.setVisible(false);
            btnContinue.setVisible(true);
            btnBack.setVisible(true);
            pnlOptions.setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Thumbnail {
        JButton btnThumbnail = new JButton();
        JPanel pnlThumbnail;
        JLabel lblImage = new JLabel();
        JLabel lblName = new JLabel();
        Thumbnail(Puzzle puzzle) throws IOException {

            BufferedImage image = (BufferedImage) PuzzleHelper.scaleImage(puzzle.file, 200, 200);
            lblImage.setIcon(new ImageIcon(image));
            lblName.setFont(MainForm.font);
            lblName.setText(puzzle.name);
            lblName.setForeground(new Color(0,72,126));
            lblName.setBorder(new CompoundBorder(lblName.getBorder(), new
                    EmptyBorder(10,0,0,0)));

            pnlThumbnail = new JPanel();
            pnlThumbnail.setLayout(new BoxLayout(pnlThumbnail, BoxLayout.Y_AXIS));
            pnlThumbnail.add(lblImage);
            pnlThumbnail.add(lblName);
            pnlThumbnail.setOpaque(false);
            lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

            btnThumbnail.add(pnlThumbnail);

            btnThumbnail.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    pnlOptions.setVisible(true);
                    txtName.setText(puzzle.name);
                    selectedPuzzle = puzzle;
                }
            });
        }
    }

    private class ThumbnailSaved {
        JButton btnThumbnail = new JButton();
        JPanel pnlThumbnail;
        JLabel lblImage = new JLabel();
        JLabel lblName = new JLabel();
        ThumbnailSaved(ContPuzzle contPuzzle) throws IOException {

            BufferedImage image = (BufferedImage) PuzzleHelper.scaleImage(contPuzzle.file, 200, 200);
            lblImage.setIcon(new ImageIcon(image));
            lblName.setFont(MainForm.font);
            lblName.setText(contPuzzle.contName);
            lblName.setForeground(new Color(0,72,126));
            lblName.setBorder(new CompoundBorder(lblName.getBorder(), new
                    EmptyBorder(10,0,0,0)));

            pnlThumbnail = new JPanel();
            pnlThumbnail.setLayout(new BoxLayout(pnlThumbnail, BoxLayout.Y_AXIS));
            pnlThumbnail.add(lblImage);
            pnlThumbnail.add(lblName);
            pnlThumbnail.setOpaque(false);
            lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

            btnThumbnail.add(pnlThumbnail);

            btnThumbnail.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    pnlOptions.setVisible(true);
                    txtName.setText(contPuzzle.contName);
                    selectedContPuzzle = contPuzzle;
                }
            });
        }
    }
}
