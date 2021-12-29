import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PuzzleHelper {

    public static void errorDialog(String message){
        javax.swing.UIManager.put("OptionPane.messageFont", MainForm.font);
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static int imageDialog(String title, String message, String file, Object[] optionsArray) throws IOException {
        javax.swing.UIManager.put("OptionPane.messageFont", MainForm.font);
        ImageIcon icon = new ImageIcon(scaleImage(file, 200, 200));

        return JOptionPane.showOptionDialog(null, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                icon, optionsArray, optionsArray[0]);

    }
    public static void contPuzzleDialog(Puzzle puzzle, String message, JFrame frame) throws IOException {
        javax.swing.UIManager.put("OptionPane.messageFont", MainForm.font);
        ImageIcon icon = new ImageIcon(scaleImage(puzzle.file, 200, 200));

        Object input = JOptionPane.showInputDialog(null,
                message, "",
                JOptionPane.INFORMATION_MESSAGE,icon,null, "");

        if (input != null){
            if (isNewNameCont(input.toString(), puzzle.id)){
                String statusString = "";
                frame.setVisible(false);
                new PlayScreen(addContPuzzle(puzzle, input.toString(), statusString));
            } else {
                contPuzzleDialog(puzzle, "- '"+ input +
                        "' is already used. Please write a new name for the puzzle version", frame);
            }
        }
    }

    public static Image scaleImage(String file, int maxW, int maxH) throws IOException {
        BufferedImage ogImage = ImageIO.read(new File(file));

        BufferedImage scaledImage = Thumbnails.of(ogImage)
                .size(maxW, maxH)
                .asBufferedImage();

        return scaledImage;
    }


    public static Puzzle addPuzzle(String file, String name, int cut, String num) throws IOException {
        //integer and text version of ID
        int idNum;
        String idText;

        int numInt = Integer.parseInt(num); //number of pieces

        //access tracker.csv to get an ID number for the puzzle
        String line = "";
        BufferedReader reader = new BufferedReader(new FileReader("tracker.csv"));
        idText = reader.readLine();
        idNum = Integer.parseInt(idText);

        CSVWriter writer2 = new CSVWriter(new FileWriter("puzzles.csv", true),
                ',', CSVWriter.NO_QUOTE_CHARACTER);
        String [] record;

        Puzzle puzzle;

        if (cut == 1){ //1 = triangle cut
            int[] arrangement = new int[numInt*2]; //int array with 2 x number of pieces
            for (int i = 2; i < numInt; i++) { //for each new piece
                Random rand = new Random();
                arrangement[((i-2)*2)] = rand.nextInt(i); //select a random piece
                arrangement[((i-2)*2)+1] = rand.nextInt(3); //select random point in the piece out of 3
            }

            //turn int array into String array
            String arrangementString = Arrays.toString(arrangement);
            arrangementString = arrangementString.replace(", ", "."); //use dots to divide
            arrangementString = arrangementString.substring( 1, arrangementString.length() - 1 ); //remove []

            record = new String[] {idText, file,name, Integer.toString(cut), num, arrangementString};

            puzzle = new Puzzle(idNum, file, name, cut, numInt, arrangementString);

        } else{

            record = new String[] {idText, file,name, Integer.toString(cut), num};
            puzzle = new Puzzle(idNum, file, name, cut, numInt);
        }

        //writing new puzzle data to puzzles.csv
        writer2.writeNext(record);
        writer2.close();

        //adding +1 to the tracker.csv for the next ID
        BufferedWriter writer1 = new BufferedWriter(new FileWriter("tracker.csv"));
        writer1.write(Integer.toString(idNum+1));
        writer1.close();

        return puzzle;
    }

    public static Puzzle addPuzzle(String file, String name, int cut, int num, String preRandom) throws IOException {
        int idNum;
        String idText;

        String line = "";
        BufferedReader reader = new BufferedReader(new FileReader("tracker.csv"));
        idText = reader.readLine();
        idNum = Integer.parseInt(idText);

        CSVWriter writer2 = new CSVWriter(new FileWriter("puzzles.csv", true),
                ',', CSVWriter.NO_QUOTE_CHARACTER);

        String [] record;
        Puzzle puzzle;

        if (cut==1){
            record = new String[] {idText, file, name, Integer.toString(cut), Integer.toString(num), preRandom};
            puzzle = new Puzzle(idNum, file, name, cut, num, preRandom);
        }
        else{
            record = new String[] {idText, file, name, Integer.toString(cut), Integer.toString(num)};
            puzzle = new Puzzle(idNum, file, name, cut, num);
        }

        writer2.writeNext(record);
        writer2.close();

        BufferedWriter writer1 = new BufferedWriter(new FileWriter("tracker.csv"));
        writer1.write(Integer.toString(idNum+1));
        writer1.close();

        return puzzle;
    }

    public static ContPuzzle addContPuzzle(Puzzle puzzle, String contName, String statusString) throws IOException {

        CSVWriter writer = new CSVWriter(new FileWriter("contPuzzles.csv", true),
                ',', CSVWriter.NO_QUOTE_CHARACTER);
        String [] record = {Integer.toString(puzzle.id),contName, statusString}; //puzzle ID, name of contPuzzle, status
        writer.writeNext(record);
        writer.close();

        return new ContPuzzle(puzzle, contName, statusString);
    }

    public static void goHome(JFrame frame){
        frame.setVisible(false);
        new MainForm();
    }

    public static boolean isNewName(String name){
        String line = "";
        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new FileReader("puzzles.csv"));
            while ((line = br.readLine()) != null) { //reads through csv file

                String[] puzzle = line.split(splitBy, -1);

                if(puzzle.length>0) {
                    if (name.equals(puzzle[2])) { //check if name is used before
                        return false;
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isNewNameCont(String name, int id){
        String line = "";
        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new FileReader("contPuzzles.csv"));
            while ((line = br.readLine()) != null)
            {
                String[] puzzleData = line.split(splitBy);
                if ((Integer.toString(id)).equals(puzzleData[0]) && name.equals(puzzleData[1])){ // check if name is used before
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

     static void renamePuzzle(String newName, int id) throws IOException {
        CSVReader reader = new CSVReader(new FileReader("puzzles.csv"));
        List<String[]> allPuzzles = reader.readAll();

        int index = locatePuzzle(id, allPuzzles, 0, allPuzzles.size()-1); //get row of puzzle data
        String[] puzzleArray = allPuzzles.get(index);

        //rename puzzle
        puzzleArray[2] = newName;
        allPuzzles.set(index, puzzleArray);

        //write data to file
        CSVWriter writer = new CSVWriter(new FileWriter("puzzles.csv"),
                ',', CSVWriter.NO_QUOTE_CHARACTER);
        writer.writeAll(allPuzzles);
        writer.close();
    }

    public static void renameContPuzzle(String newName, int id, String oldName) throws IOException {
        CSVReader reader = new CSVReader(new FileReader("contPuzzles.csv"));
        List<String[]> allElements = reader.readAll();

        int i =0;

        while (i<allElements.size()){
            if ((id == Integer.parseInt(allElements.get(i)[0]))
                    && (oldName.equals(allElements.get(i)[1]))){
                break;
            }
            i++;
        }

        String[] contPuzzleArray = allElements.get(i);
        contPuzzleArray[1] = newName;

        allElements.set(i, contPuzzleArray);

        CSVWriter writer = new CSVWriter(new FileWriter("contPuzzles.csv"),
                ',', CSVWriter.NO_QUOTE_CHARACTER);
        writer.writeAll(allElements);
        writer.close();
    }

    public static void deletePuzzle(int id) throws IOException {
        CSVReader reader1 = new CSVReader(new FileReader("puzzles.csv"));
        List<String[]> allPuzzles = reader1.readAll();

        allPuzzles.remove(locatePuzzle(id, allPuzzles, 0, allPuzzles.size()-1));

        CSVWriter writer = new CSVWriter(new FileWriter("puzzles.csv"),
                ',', CSVWriter.NO_QUOTE_CHARACTER);
        writer.writeAll(allPuzzles);
        writer.close();

        CSVReader reader2 = new CSVReader(new FileReader("contPuzzles.csv"));
        List<String[]> allContPuzzles = reader2.readAll();

        int i =0;

        while (i<allContPuzzles.size()){
            if ((id == Integer.parseInt(allContPuzzles.get(i)[0]))){
                allContPuzzles.remove(i);
            }
            i++;
        }

        writer = new CSVWriter(new FileWriter("contPuzzles.csv"),
                ',', CSVWriter.NO_QUOTE_CHARACTER);
        writer.writeAll(allContPuzzles);
        writer.close();
    }

    private static int locatePuzzle(int id, List<String[]> allPuzzles, int min, int max) throws IOException {
        if (min<=max) {
            int avg = (max+min)/2;
            if (Integer.parseInt(allPuzzles.get(avg)[0]) == id) { //if puzzleID is found
                return avg;
            } else if (Integer.parseInt(allPuzzles.get(avg)[0]) > id) { //if the id is higher than puzzleID
                return locatePuzzle(id, allPuzzles, min, avg - 1); //bottom half
            } else { //if the id is lower than
                return locatePuzzle(id, allPuzzles, avg + 1, max); //top half
            }
        }
        return 0;
    }


    public static void deleteContPuzzle(String name, int id) throws IOException {
        CSVReader reader = new CSVReader(new FileReader("contPuzzles.csv"));
        List<String[]> allElements = reader.readAll();

        int i =0;

        while (i<allElements.size()){
            if ((id == Integer.parseInt(allElements.get(i)[0]))
                    && (name.equals(allElements.get(i)[1]))){
                break;
            }
            i++;
        }

        allElements.remove(i);

        CSVWriter writer = new CSVWriter(new FileWriter("contPuzzles.csv"),
                ',', CSVWriter.NO_QUOTE_CHARACTER);
        writer.writeAll(allElements);
        writer.close();
    }

    public static void updateContPuzzleStatus(boolean[] status, String name, int id) throws IOException {

        String statusString = Arrays.toString(status);
        statusString = statusString.replace(", ", ".");
        statusString = statusString.substring( 1, statusString.length() - 1 );

        CSVReader reader = new CSVReader(new FileReader("contPuzzles.csv"));
        List<String[]> allElements = reader.readAll();

        int i =0;

        while (i<allElements.size()){
            if ((id == Integer.parseInt(allElements.get(i)[0]))
                    && (name.equals(allElements.get(i)[1]))){
                break;
            }
            i++;
        }

        String[] contPuzzleArray = allElements.get(i);
        contPuzzleArray[2] = statusString;

        allElements.set(i, contPuzzleArray);

        CSVWriter writer = new CSVWriter(new FileWriter("contPuzzles.csv"),
                ',', CSVWriter.NO_QUOTE_CHARACTER);
        writer.writeAll(allElements);
        writer.close();
    }

    public static void sharePuzzle(Puzzle puzzle) throws IOException {
        File path = new File("../shareable_puzzles");
        if (!path.exists()){
            path.mkdirs();
        }

        File data = new File("../shareable_puzzles/data.csv");
        CSVWriter writer = new CSVWriter(new FileWriter("../shareable_puzzles/data.csv", true),
                ',', CSVWriter.NO_QUOTE_CHARACTER);
        String[] record = {puzzle.name, Integer.toString(puzzle.cut), Integer.toString(puzzle.num),
                puzzle.arrangement};
        writer.writeNext(record);
        writer.close();

        File image = new File("../shareable_puzzles/image.jpg");

        Thumbnails.of(new File(puzzle.file))
                .size(700, 400)
                .toFile(new File("../shareable_puzzles/image.jpg"));

        File zip = new File("../shareable_puzzles/"+puzzle.name+".zip");
        File files[] = {data, image};
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zip));

        zipFile(files, zip);

        data.delete();
        image.delete();
    }



    public static void shareContPuzzle(ContPuzzle contPuzzle) throws IOException {
        File path = new File("../shareable_puzzles");
        if (!path.exists()){
            path.mkdirs();
        }

        File data = new File("../shareable_puzzles/data.csv");
        CSVWriter writer = new CSVWriter(new FileWriter("../shareable_puzzles/data.csv", true),
                ',', CSVWriter.NO_QUOTE_CHARACTER);
        String[] line1 = {contPuzzle.name, Integer.toString(contPuzzle.cut), Integer.toString(contPuzzle.num),
                contPuzzle.arrangement};
        writer.writeNext(line1);
        String[] line2 = {contPuzzle.contName, contPuzzle.statusString};
        writer.writeNext(line2);
        writer.close();

        File image = new File("../shareable_puzzles/image.jpg");

        Thumbnails.of(new File(contPuzzle.file))
                .size(700, 400)
                .toFile(new File("../shareable_puzzles/image.jpg"));

        File zip = new File("../shareable_puzzles/"+contPuzzle.name+"_"+contPuzzle.contName+".zip");
        File[] files = {data, image};

        zipFile(files, zip);

        data.delete();
        image.delete();
    }

    public static void zipFile(File[] files, File zip) throws FileNotFoundException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zip));

        // Create the ZIP file first
        try (ZipOutputStream out = new ZipOutputStream(bos)) {
            // Write files/copy to archive
            for (File file : files) {
                // Put a new ZIP entry to output stream for every file
                out.putNextEntry(new ZipEntry(file.getName()));
                Files.copy(file.toPath(), out);
                out.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadCompletedPuzzle(ContPuzzle contPuzzle) throws IOException {
        File path = new File("../completed_puzzles");
        if (!path.exists()){
            path.mkdirs();
        }

        //get date
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        String dateString = date.format(dateFormat);

        //get the template
        BufferedImage congrats = ImageIO.read(new File("images/completed.png")); //read existing file

        Graphics g = congrats.createGraphics();
        g.setColor(Color.BLACK);

        //add puzzle name
        FontMetrics metrics = g.getFontMetrics(MainForm.font.deriveFont(Font.PLAIN, 70)); //font detail for sizing
        String name = contPuzzle.name+": "+contPuzzle.contName; //name
        int xName = (congrats.getWidth()/2)-(metrics.stringWidth(name)/2); //calculate x
        int yName = 540; //calculate y
        g.setFont(MainForm.font.deriveFont(Font.PLAIN, 70)); //set font
        g.drawString(name, xName, yName); //draw name

        //add date
        int xDate = 600;
        int yDate = 1468;
        g.drawString(dateString, xDate, yDate); //draw date

        //add puzzle image
        BufferedImage puzzle = (BufferedImage) PuzzleHelper.scaleImage(contPuzzle.file, 1000, 700); //get image
        int XPuzzle = (congrats.getWidth()/2)-(puzzle.getWidth()/2); //calculate x
        int YPuzzle = (congrats.getHeight()/2)-(puzzle.getHeight()/2)+50; //calculate y
        g.drawImage(puzzle, XPuzzle, YPuzzle, null); //add image
        g.dispose();

        //download
        ImageIO.write(congrats, "png", new File("../completed_puzzles/completed_"+contPuzzle.name+
                "_"+contPuzzle.contName+".png"));
    }
}
