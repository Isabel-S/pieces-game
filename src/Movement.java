import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

public class Movement implements MouseListener, MouseMotionListener {

    private int x,y;
    private int pieceX, pieceY;
    Piece piece;

    public Movement(JPanel pnlPiece, Piece piece){
        this.piece = piece;
        pnlPiece.addMouseListener(this);
        pnlPiece.addMouseMotionListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent event) {
        x = event.getX();
        y = event.getY();

    }

    @Override
    public void mouseReleased(MouseEvent event) {
        try {
            piece.checkPosition();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (!piece.lock){
            event.getComponent().setLocation(event.getX()+event.getComponent().getX()-x,event.getY()+event.getComponent().getY()-y);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
