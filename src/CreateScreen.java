import au.com.bytecode.opencsv.CSVReader;
import com.sun.tools.javac.Main;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;

class CreateScreen {

    static JFrame frmCreate;
    static JPanel pnlCreate;
    private static JTabbedPane pnlTabs;
    private static JPanel pnlHeader;
    private JPanel pnlCustom;
    private JPanel pnlUpload;

    private JPanel pnlAttach1;
    private JPanel pnlAttach2;


    private JLabel lblImage = new JLabel("Puzzle Image:");
    private JLabel lblNum = new JLabel("Number of Pieces:");
    private JLabel lblCut = new JLabel("Puzzle Cut:");
    private JLabel lblName = new JLabel("Puzzle Name:");
    private JTextField txtAttach1 = new JTextField("Path Name");
    private JButton btnAttach1 = new JButton("Attach");
    private JTextField txtNum = new JTextField("#");
    private String[] cutTypes = {"Grid", "Triangles"};
    private JComboBox drpCut = new JComboBox(cutTypes);
    private JTextField txtName = new JTextField("Type Name Here..");
    private JButton btnGenerate = new JButton("Generate");

    private JLabel lblUpload = new JLabel("Puzzle Zip Folder:");
    private JTextField txtAttach2 = new JTextField("Open the puzzle zip file, and upload the folder.");
    private JLabel lblName2 = new JLabel("Puzzle Name:");
    private JTextField txtName2 = new JTextField("Use uploaded name or type name here.");

    private JButton btnAttach2 = new JButton("Attach");

    private JLabel lblTitle = new JLabel("Create New Puzzle");
    private JButton btnHome = new JButton();
    private JLabel imgHome = new JLabel();

    CreateScreen() throws IOException {
        setupCreateScreen();

        btnHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PuzzleHelper.goHome(frmCreate);
            }
        });

        btnAttach1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                File f = chooser.getSelectedFile();
                String filepath = f.getAbsolutePath();
                txtAttach1.setText(filepath);
            }
        });

        btnAttach2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.showOpenDialog(null);
                File f = chooser.getSelectedFile();
                String filepath = f.getAbsolutePath();
                txtAttach2.setText(filepath);

                    try {
                        CSVReader reader = new CSVReader(new FileReader(txtAttach2.getText()+"/data.csv"));
                        List<String[]> allElements = reader.readAll();
                        txtName2.setText(allElements.get(0)[0]);
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                        PuzzleHelper.errorDialog("Please upload a correct puzzle folder.");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        PuzzleHelper.errorDialog("Please upload a correct puzzle folder.");
                    }
            }
        });

        btnGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if (pnlTabs.getSelectedIndex() == 0) //if user customized a puzzle
                    {
                        //calls method to check if inputs are valid
                        if (customCheck(txtAttach1.getText(), txtNum.getText(), txtName.getText())) {

                            generatePuzzle(PuzzleHelper.addPuzzle(txtAttach1.getText(), txtName.getText(),
                                    drpCut.getSelectedIndex(), txtNum.getText()));

                        } else {
                            String errorMessage = "Make sure to: \n - upload an image file " +
                                    "\n - insert an integer greater than 1 " +
                                    "\n - choose a new name for the puzzle";
                            System.out.println(errorMessage);
                            PuzzleHelper.errorDialog(errorMessage);
                        }

                    } else { //if user uploaded a shared puzzle
                        Puzzle puzzle = uploadCheck();
                        if (puzzle != null) {
                            Object[] optionsArray = {"OK"};
                            int result = PuzzleHelper.imageDialog("Generated!",
                                    "Go home and click View Puzzles to see uploaded puzzle(s)",
                                    puzzle.file, optionsArray);
                        }
                    }

                } catch (IOException e1){
                    e1.printStackTrace();
                }
            }

        });

    }

    private void generatePuzzle(Puzzle puzzle) throws IOException {
        Object[] optionsArray = {"Play", "Home"};
        int result = PuzzleHelper.imageDialog("Generated!", "", puzzle.file, optionsArray);

        if (result == 0) {
            PuzzleHelper.contPuzzleDialog(puzzle, "Enter a name for the puzzle version:", frmCreate);

        } else if (result == 1) {
            PuzzleHelper.goHome(frmCreate);
        }
    }

    private Puzzle uploadCheck(){
        String path = txtAttach2.getText();
        //checks if the folder cotains correct image, and a csv file and alerts otherwise
        if (!isImage(path+"/image.jpg")){
            PuzzleHelper.errorDialog("Please upload a correct puzzle folder.");
            return null;
        }
        if (!isCSV(path+"/data.csv")){
            PuzzleHelper.errorDialog("Please upload a correct puzzle folder.");
            return null;
        }

        CSVReader reader = null;
        try { // surround everything with try catch -- if there is an error the incorrect puzzle was uploaded
            //checks if the csv file is readable
            reader = new CSVReader(new FileReader(path+"/data.csv"));
            List<String[]> allElements = reader.readAll();

            //if there is an out of bounds exception, it will be catched
            String name = txtName2.getText(); // assigns first element as "name"
            String cutString = allElements.get(0)[1]; // assigns third element as the string for the cut type
            String numString = allElements.get(0)[2]; // assigns third element as the string for the # of piece

            // if there is an error, the incorrect puzzle was uploaded
            int cut = Integer.parseInt(cutString);
            int num = Integer.parseInt(numString);

            if (cut==0){ //GRID
                if(!PuzzleHelper.isNewName(name)){
                    PuzzleHelper.errorDialog("Please use a new puzzle name.");
                    return null;
                }

                //check if there is a second row, aka a continued puzzle, otherwise, add puzzle
                if (allElements.size()==1){ // check if it is only one row
                    return PuzzleHelper.addPuzzle(path+"/image.jpg", name, cut, num, "");
                } else{
                    String contName = allElements.get(1)[0]; // assigns first element as name
                    String status = allElements.get(1)[1]; // assigns second element as piece statuses
                    if (!status.equals("")){
                        String[] statusStringArray = status.split("\\.", -1);
                        for(int i=0; i<(statusStringArray.length); i++){
                            String a = statusStringArray[i];
                            if (!(a.equals("true")||a.equals("false"))) {
                                PuzzleHelper.errorDialog("Please upload a correct puzzle folder.");
                                return null;
                            }
                        }
                    }
                    Puzzle puzzle = PuzzleHelper.addPuzzle(path+"/image.jpg", name, cut, num, "");
                    PuzzleHelper.addContPuzzle(puzzle, contName, status);
                    return puzzle;
                }

            } else if (cut==1){ //Triangle
                String preRandom = allElements.get(0)[3]; // assigns fourth element as the string for the pre-random numbers
                String[] preRandomStringArray = preRandom.split("\\.", -1);

                //checks if each value in the preRandomStringArray is a number that is between 0-2
                for(int i=0; i<(preRandomStringArray.length); i++){
                    int a = Integer.parseInt(preRandomStringArray[i]);
                    if (!(a == 0|| a == 1 || a ==2)) {
                        PuzzleHelper.errorDialog("Please upload a correct puzzle folder.");
                        return null;
                    }
                }

                if(!PuzzleHelper.isNewName(name)){
                    PuzzleHelper.errorDialog("Please use a new puzzle name.");
                    return null;
                }

                //check if there is a second row, aka a continued puzzle, otherwise, add puzzle
                if (allElements.size()==1){ // check if it is only one row
                    return PuzzleHelper.addPuzzle(path+"/image.jpg", name, cut, num, preRandom);
                } else{
                    String contName = allElements.get(1)[0]; // assigns first element as name
                    String status = allElements.get(1)[1]; // assigns second element as piece statuses
                    if (!status.equals("")){
                        String[] statusStringArray = status.split("\\.", -1);
                        for(int i=0; i<(statusStringArray.length); i++){
                            String a = statusStringArray[i];
                            if (!(a.equals("true")||a.equals("false"))) {
                                PuzzleHelper.errorDialog("Please upload a correct puzzle folder.");
                                return null;
                            }
                        }
                    }
                    Puzzle puzzle = PuzzleHelper.addPuzzle(path+"/image.jpg", name, cut, num, preRandom);
                    PuzzleHelper.addContPuzzle(puzzle, contName, status);
                    return puzzle;
                }

            } else{ // the cut types can only be 1 or 0, so anything else is incorrect
                PuzzleHelper.errorDialog("Please upload a correct puzzle folder.");
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            PuzzleHelper.errorDialog("Please upload a correct puzzle folder.");
            return null;
        }
    }

    private Puzzle readCSV(String path) throws IOException {
        try {
            CSVReader reader = new CSVReader(new FileReader(path));
            List<String[]> allRows = reader.readAll();
            if (allRows.size()>0){
                Puzzle puzzle = PuzzleHelper.addPuzzle(path, allRows.get(0)[0],
                        Integer.parseInt(allRows.get(0)[1]), Integer.parseInt(allRows.get(0)[2]),
                        allRows.get(0)[2]);
                if (allRows.size()>1){
                    PuzzleHelper.addContPuzzle(puzzle, allRows.get(1)[0], allRows.get(1)[1]);
                }
                return puzzle;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean customCheck(String path, String num, String name) {
        //checks if (1) file is an image, (2) number of pieces is an integer, (3) puzzle name is new
        return (isImage(path) && isInteger(num) && PuzzleHelper.isNewName(name));
    }

    private boolean isInteger(String num) {
        try {
            int intNum = Integer.parseInt(num);
            return (intNum > 1); // if integer is greater than 1
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isCSV(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isImage(String path) {
        try{
            if (ImageIO.read(new File(path))==null){
                return false;
            } else {
                // checking if the file is an *accessible* image
                ImageIO.read(new File(path)).getWidth();
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private void setupCreateScreen() throws IOException {
        ImageIcon home = new ImageIcon(Thumbnails.of("images/home.png")
                .size(40,40)
                .asBufferedImage());
        btnHome.setIcon(home);

        lblTitle.setFont(MainForm.font.deriveFont(Font.PLAIN, 40));
        lblTitle.setForeground(new Color(0,72,126));
        btnGenerate.setFont(MainForm.font.deriveFont(Font.PLAIN, 22f));
        btnGenerate.setForeground(new Color(0,72,126));
        btnGenerate.setMargin(new Insets(5, 2, 0, 2));

        lblImage.setFont(MainForm.font.deriveFont(Font.PLAIN, 22f));
        lblImage.setForeground(Color.white);
        lblNum.setFont(MainForm.font.deriveFont(Font.PLAIN, 22f));
        lblNum.setForeground(Color.white);
        lblCut.setFont(MainForm.font.deriveFont(Font.PLAIN, 22f));
        lblCut.setForeground(Color.white);
        lblName.setFont(MainForm.font.deriveFont(Font.PLAIN, 22f));
        lblName.setForeground(Color.white);

        txtAttach1.setFont(MainForm.font);
        txtAttach1.setHorizontalAlignment(JTextField.CENTER);
        btnAttach1.setFont(MainForm.font);
        btnAttach1.setMargin(new Insets(3, 2, 0, 2));
        txtNum.setFont(MainForm.font);
        txtNum.setHorizontalAlignment(JTextField.CENTER);
        drpCut.setFont(MainForm.font);
        txtName.setFont(MainForm.font);
        txtName.setHorizontalAlignment(JTextField.CENTER);

        lblUpload.setFont(MainForm.font.deriveFont(Font.PLAIN, 22f));
        lblUpload.setForeground(Color.white);
        lblName2.setFont(MainForm.font.deriveFont(Font.PLAIN, 22f));
        lblName2.setForeground(Color.white);
        txtAttach2.setFont(MainForm.font);
        txtAttach2.setHorizontalAlignment(JTextField.CENTER);
        btnAttach2.setFont(MainForm.font);
        btnAttach2.setMargin(new Insets(3, 2, 0, 2));
        txtName2.setFont(MainForm.font);
        txtName2.setHorizontalAlignment(JTextField.CENTER);


        frmCreate = new JFrame();
        frmCreate.setSize(800,600);
        frmCreate.setLocationRelativeTo(null);

        pnlAttach1 = new JPanel();
        pnlAttach1.setOpaque(false);
        pnlAttach1.setLayout(new BoxLayout(pnlAttach1, BoxLayout.PAGE_AXIS));
        pnlAttach1.add(txtAttach1);
        pnlAttach1.add(btnAttach1);

        pnlAttach2 = new JPanel();
        pnlAttach2.setOpaque(false);
        pnlAttach2.setLayout(new BoxLayout(pnlAttach2, BoxLayout.PAGE_AXIS));
        pnlAttach2.add(txtAttach2);
        pnlAttach2.add(btnAttach2);

        pnlCustom = new JPanel();
        pnlCustom.setSize(frmCreate.getSize());
        pnlCustom.setLocation(0,0);
        pnlCustom.setBackground(frmCreate.getBackground());
        pnlCustom.setLayout(new GridLayout(4,2));
        pnlCustom.add(lblImage);
        pnlCustom.add(pnlAttach1);
        pnlCustom.add(lblNum);
        pnlCustom.add(txtNum);
        pnlCustom.add(lblCut);
        drpCut.setEditable(false);
        drpCut.addActionListener(drpCut);
        pnlCustom.add(drpCut);
        pnlCustom.add(lblName);
        pnlCustom.add(txtName);
        pnlCustom.setBackground(new Color(102, 91, 220));
        lblImage.setBorder(new CompoundBorder(lblImage.getBorder(), new
                EmptyBorder(0,100,2,0)));
        lblNum.setBorder(new CompoundBorder(lblNum.getBorder(), new
                EmptyBorder(0,100,0,0)));
        lblCut.setBorder(new CompoundBorder(lblCut.getBorder(), new
                EmptyBorder(0,100,0,0)));
        lblName.setBorder(new CompoundBorder(lblName.getBorder(), new
                EmptyBorder(0,100,0,0)));

        pnlUpload = new JPanel();
        pnlUpload.setSize(frmCreate.getSize());
        pnlUpload.setLocation(0,0);
        pnlUpload.setBackground(frmCreate.getBackground());
        pnlUpload.setLayout(new GridLayout(2,2));
        pnlUpload.add(lblUpload);
        pnlUpload.add(pnlAttach2);
        pnlUpload.add(lblName2);
        pnlUpload.add(txtName2);
        pnlUpload.setBackground(new Color(102, 91, 220));
        lblUpload.setBorder(new CompoundBorder(lblUpload.getBorder(), new
                EmptyBorder(0,100,0,0)));
        lblName2.setBorder(new CompoundBorder(lblName2.getBorder(), new
                EmptyBorder(0,100,0,0)));

        pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.X_AXIS));
        pnlHeader.setSize(800,-1);
        pnlHeader.setOpaque(false);
        pnlHeader.add(lblTitle);
        pnlHeader.add(Box.createRigidArea(new Dimension(330,0)));
        pnlHeader.add(btnHome);

        pnlTabs = new JTabbedPane();
        pnlTabs.addTab("CUSTOMIZE A PUZZLE", pnlCustom);
        pnlTabs.addTab("UPLOAD A PUZZLE", pnlUpload );
        pnlTabs.setForegroundAt(0, new Color(0,72,126));
        pnlTabs.setForegroundAt(1, new Color(0,72,126));
        pnlTabs.setForeground(Color.black);
        pnlTabs.setBackground(new Color(206, 192, 232));
        pnlTabs.setFont(MainForm.font.deriveFont(Font.PLAIN, 15));

        pnlCreate = new JPanel();
        pnlCreate.setLayout(new BoxLayout(pnlCreate, BoxLayout.Y_AXIS));
        pnlCreate.add(pnlHeader);
        pnlCreate.add(pnlTabs);
        pnlCreate.add(btnGenerate);
        pnlCreate.setBackground(new Color(206, 192, 232));

        frmCreate.add(pnlCreate);
        frmCreate.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmCreate.setVisible(true);
    }
}
