import au.com.bytecode.opencsv.CSVWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Puzzle {

    int id;
    String file;
    String name;
    int num;
    int cut;
    int fullWidth;
    int fullHeight;
    BufferedImage fullImage;
    String arrangement;

    //constructor for Grid Puzzles
    public Puzzle(int id, String file, String name, int cut, int num) throws IOException { //for grid puzzles
        this.id = id;
        this.file = file;
        this.name = name;
        this.cut = cut;
        this.num = num;

        this.fullImage = (BufferedImage) PuzzleHelper.scaleImage(file, 600, 400);
        this.fullWidth = fullImage.getWidth();
        this.fullHeight = fullImage.getHeight();
    }

    //constructor for Triangle Puzzles
    public Puzzle(int id, String file, String name, int cut, int num, String arrangement) throws IOException {
        this.id = id;
        this.file = file;
        this.name = name;
        this.cut = cut;
        this.num = num;
        this.arrangement = arrangement;

        this.fullImage = (BufferedImage) PuzzleHelper.scaleImage(file, 600, 400);
        this.fullWidth = fullImage.getWidth();
        this.fullHeight = fullImage.getHeight();
    }

    //constructor for when contPuzzle uses super(puzzle)
    public Puzzle(Puzzle puzzle) throws IOException {
        this.id = puzzle.id;
        this.file = puzzle.file;
        this.name = puzzle.name;
        this.cut = puzzle.cut;
        this.num = puzzle.num;

        this.fullImage = (BufferedImage) PuzzleHelper.scaleImage(file, 600, 400);
        this.fullWidth = fullImage.getWidth();
        this.fullHeight = fullImage.getHeight();

        if(puzzle.arrangement != null){
            this.arrangement = puzzle.arrangement;
        }
    }
}
