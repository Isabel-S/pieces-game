import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Piece{
    JPanel pnlPiece;
    public int pieceID;
    boolean lock;
    private ContPuzzle contPuzzle;
    private int xGoal,yGoal;

    public Piece(ContPuzzle contPuzzle, BufferedImage image, int xGoal, int yGoal) throws IOException {
        this.contPuzzle = contPuzzle;
        this.xGoal = xGoal;
        this.yGoal = yGoal;

        //creating the piece
        pnlPiece = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0) );
        pnlPiece.setOpaque(false);
        JLabel lblImage = new JLabel();
        lblImage.setIcon(new ImageIcon(image));
        pnlPiece.add(lblImage);
        pnlPiece.setSize(image.getWidth(), image.getHeight());

        //random location
        Random rand = new Random();
        pnlPiece.setLocation(rand.nextInt(700-image.getWidth()) ,
                rand.nextInt(501-image.getHeight()));

        //adding drag-drop movement
        lock = false;
        new Movement(pnlPiece, this);
    }

    public void setID(int id){
        this.pieceID = id;
    }

    public int getX(){
        return pnlPiece.getX() - contPuzzle.xBoard;
    }

    public int getY(){
        return pnlPiece.getY() - contPuzzle.yBoard;
    }

    public void lock() {
        pnlPiece.setLocation(xGoal, yGoal);
        PlayScreen.lockPiece(pnlPiece); //display piece on pnlBoard

        lock = true;
    }

    //check if piece is in the correct place and lock the piece if it is
    public void checkPosition() throws IOException {
        int xDifference = Math.abs(getX() - xGoal);
        int yDifference = Math.abs(getY() - yGoal);

        if (xDifference < 20 && yDifference < 20) { //if the piece is near
            lock();
            contPuzzle.updateStatus(pieceID);
        }
    }
}
