import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.List;


public class ContPuzzle extends Puzzle{
    String contName;
    String statusString;
    private List<Piece> pieces = new ArrayList<Piece>();
    private boolean[] status;
    private int[][] goal;
    int xBoard;
    int yBoard;

    public ContPuzzle(Puzzle puzzle, String name, String statusString) throws IOException {
        super(puzzle);

        this.contName = name;
        this.statusString = statusString;

        status = new boolean[num];
        goal = new int[num][2];

        //coordinates to place the puzzle board
        xBoard = 320 - fullWidth/2;
        yBoard = 250 - fullHeight/2;
    }

    void displayPuzzle(JPanel pnlPuzzle) throws IOException {
        //cut puzzle based on user cut type selection
        if (cut == 0){
            cutGrid(pnlPuzzle);

        } else {
            cutTriangles(pnlPuzzle);
        }

        loadStatus(); //loads saved status of the pieces
    }

    private void loadStatus() throws IOException {
        //turn String array of status into boolean array
        String[] statusStringArray = statusString.split("\\.", -1);

        for(int i=0; i<statusStringArray.length; i++){
            System.out.println(statusStringArray[i]);
            if(statusStringArray[i].equals("true")){
                status[i] = true;
                pieces.get(i).lock();
            } else{
                status[i] = false;
            }
        }
    }

    private void cutTriangles(JPanel pnlPuzzle) throws IOException {
        //get pre-randomized data from file (pre-randomized so all versions are cut the same)
        String[] arrangementStringArray = arrangement.split("\\.", -1);
        int[][] arrangementArray = new int[(arrangementStringArray.length)/2][2];

        for(int i=0; i<(arrangementStringArray.length)/2; i++){
            arrangementArray[i][0] = Integer.parseInt(arrangementStringArray[i*2]);
            arrangementArray[i][1] = Integer.parseInt(arrangementStringArray[(i*2)+1]);
        }

        //create int array. [num] for the # of pieces, [3] for 3 points in a triangle, [2] for xy coordinates
        int[][][] cuts = new int[num][3][2];

        //set with first two triangles cutting the image in half diagonally
        cuts[0] = new int[][] {{0,0}, {fullWidth, fullHeight}, {0,fullHeight}};
        cuts[1] = new int[][] {{0,0}, {fullWidth, fullHeight},
                {fullWidth,0}};

        int pieceNum,pointANum,gradient,b;
        int[] pointB, pointC, pointD;

        for (int i = 2; i < num; i++) {
            //picking index numbers
            pieceNum = arrangementArray[i-2][0]; //picking a "random" piece between current pieces
            pointANum = arrangementArray[i-2][1]; //picks a "random" point within the piece

            //based on which point is picked, the other two points in the triangle are set as B and C
            if (pointANum == 0){
                pointB = cuts[pieceNum][1];
                pointC = cuts[pieceNum][2];

            } else if (pointANum == 1) {
                pointB = cuts[pieceNum][0];
                pointC = cuts[pieceNum][2];
            } else{
                pointB = cuts[pieceNum][0];
                pointC = cuts[pieceNum][1];
            }

            //creating a midpoint (pointD) to be placed in between point B and C
            pointD = new int[2];
            pointD[0] = (pointC[0] + pointB[0])/2; //setting a x-coordinate in the middle of B and C
            pointD[1] = (pointC[1] + pointB[1])/2; //setting a y-coordinate in the middle of B and C

            //setting new coordinates to cuts array (cutting old piece into two half pieces)
            cuts[i]= new int[][] {cuts[pieceNum][pointANum], pointD, pointC}; //adding a new piece to the array
            cuts[pieceNum] = new int[][] {cuts[pieceNum][pointANum], pointD, pointB}; //replace old piece w/ half piece
        }

        //using the coordinates to cut out pieces
        for (int i = 0; i < num; i++) { //loop through each piece in the cuts array
            //creating clipping mask
            GeneralPath clip = new GeneralPath();
            clip.moveTo(cuts[i][0][0], cuts[i][0][1]); //first point
            clip.lineTo(cuts[i][1][0], cuts[i][1][1]); //second point
            clip.lineTo(cuts[i][2][0], cuts[i][2][1]); //third point
            clip.closePath();

            //finding smallest xy coordinates
            int[] x = new int[] {cuts[i][0][0], cuts[i][1][0], cuts[i][2][0]}; //array of all x values
            int[] y = new int[] {cuts[i][0][1], cuts[i][1][1], cuts[i][2][1]}; //array of all y values
            Arrays.sort(x);
            Arrays.sort(y);
            int xOrigin = x[0]; //smallest x coordinate
            int yOrigin = y[0]; //smallest y coordinate

            //create image of piece
            Rectangle bounds = clip.getBounds(); //setting size of piece to the rectangle bounds of clip
            BufferedImage pieceImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = pieceImage.createGraphics();

            //moving the clip so it cuts the correct location
            clip.transform(AffineTransform.getTranslateInstance(-xOrigin, -yOrigin));
            g2d.setClip(clip);
            g2d.translate(-xOrigin, -yOrigin);

            g2d.drawImage(fullImage, 0, 0, null); //cutting out piece from the original image
            g2d.dispose();

            //adding the piece, setting (xOrigin, yOrigin) as target location for the piece on the puzzle board
            addPiece(pnlPuzzle, pieceImage, xOrigin, yOrigin);
        }
    }

    private void cutGrid(JPanel pnlPuzzle) throws IOException {
        int row, col;
        row = 1;

        //getting the ideal row and columns combo
        for (int i = (int) Math.sqrt(num); i >=2 ; i--) {
            if(num%i == 0) {
                row = i;
                break;
            }
        }
        col = num/row;

        int pieceWidth = fullWidth/col;
        int pieceHeight = fullHeight/row;

        // creating the pieces
        for (int y = 0; y < row; y++) { //go through rows
            for (int x = 0; x < col; x++) { //go through each column in row

                BufferedImage pieceImage = fullImage.getSubimage(x*pieceWidth, y*pieceHeight,
                        pieceWidth, pieceHeight); //cutting out piece from the original image

                addPiece(pnlPuzzle, pieceImage, (x*pieceWidth), (y*pieceHeight));
            }
        }
    }

    private void addPiece(JPanel pnlPuzzle, BufferedImage pieceImage, int xGoal, int yGoal) throws IOException {
        Piece piece = new Piece(this, pieceImage, xGoal, yGoal); //create new Piece class
        pnlPuzzle.add(piece.pnlPiece); //add piece to the screen
        pieces.add(piece); //add piece to ArrayList
        piece.setID(pieces.size()-1); //give piece an ID
    }

    public void updateStatus(int pieceID) throws IOException {
        status[pieceID] = true;
        PuzzleHelper.updateContPuzzleStatus(status, contName, id); //save puzzle changes to file
        checkComplete(); //call method to check if whole puzzle is complete
    }

    public void checkComplete() throws IOException {
        boolean complete = true;
        for(int i=0; i<status.length; i++){
            if(!status[i]){
               complete = false;
               break;
            }
        }
        if (complete){
            Object[] optionsArray = {"Home"};
            int result = PuzzleHelper.imageDialog("Congrats!",
                    "You completed the puzzle! \n A copy of your completed puzzle has been downloaded in the 'completed_puzzles' folder. \n This puzzle will now be deleted.",
                    file, optionsArray);
            PuzzleHelper.downloadCompletedPuzzle(this);
            PuzzleHelper.deleteContPuzzle(contName, id);
            if (result == 0) {
                PuzzleHelper.goHome(PlayScreen.frmPlay);
            }
        }
    }
}
