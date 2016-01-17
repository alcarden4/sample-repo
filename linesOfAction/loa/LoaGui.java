package loa;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

/**
 * A top-level GUI for Canfield solitaire.
 *
 * @author Anna Cardenas
 */
class LoaGui extends TopLevel {

    /** A new window with given TITLE and displaying GAME. */
    LoaGui(String title, GuiGame game) {
        super(title, true);
        _game = game;
        _display = new GameDisplay(game);
        addMenuButton("Game->New Game", "newGame");
        addMenuButton("Game->Quit", "quit");
        addMenuButton("AI->Play against AI", "playAI");
        add(_display, new LayoutSpec("y", 2, "width", 2));
        _display.setMouseHandler("click", this, "mouseClicked");
        _display.setMouseHandler("release", this, "mouseReleased");
        _display.setMouseHandler("drag", this, "mouseDragged");

        display(true);
        _board = _game.getBoard();
        for (int c = 1; c <= 8; c++) {
            for (int r = 1; r <= 8; r++) {
                int col = (c - 1) * 100;
                int row = (r - 1) * 100;
                SQUARES[c - 1][r - 1] = new Rectangle(col, row, 100, 100);
            }
        }
    }

    /** RETURN the piece corresponding to the rectangle's COORDINATES. */
    private Piece getPiece(int[] coordinates) {
        int col = coordinates[0];
        int row = coordinates[1];
        return _board.get(col, row);
    }

    /** Returns the indices of the rectangle that contains the point P. */
    private int[] getRectangleIndices(Point p) {
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 8; r++) {
                if (SQUARES[c][r].contains(p)) {
                    System.out.println("square contained this");
                    return new int[] {c + 1, r + 1};
                }
            }
        }
        return null;
    }

    /** Respond to "Quit" button. */
    public void quit(String dummy) {
        if (showOptions("Really quit?", "Quit?", "question", "Yes", "Yes",
                "No") == 0) {
            System.exit(1);
        }
    }

    /** Respond to New Game button. */
    public void newGame(String dummy) {
        _board.clear();
        _display.repaint();
    }
    /** Begins the Ai player if aiPlaying is true. */
    public void playAI(String dummy) {
        newGame("newGame");
        aiPlaying = true;
    }
    /** MOUSE ACTIONS FOR GAME */

    /** Action in response to mouse-clicking event EVENT. */

    public synchronized void mouseClicked(MouseEvent event) {
    }

    /** Action in response to mouse-released event EVENT. */
    public synchronized void mouseReleased(MouseEvent event) {
        destinationClick = new int[2];
        Point p = new Point(event.getX(), event.getY());
        destinationClick = getRectangleIndices(p);
        Move move = Move.create(firstClick[0], firstClick[1],
                destinationClick[0], destinationClick[1], _board);
        _board.makeMove(move);
        _display.repaint();
        if (aiPlaying && _board.turn().equals(Piece.WP)) {
            MachinePlayer ai =
                    new MachinePlayer(_board.turn().opposite(), _game);
            Move aiMove = ai.makeMove();
            if (_board.piecesContiguous(_board.turn())
                    || _board.piecesContiguous(_board.turn().opposite())) {
                String winner = new String(_board.getWinner().fullName()
                        + " won!");
                int playAgain = JOptionPane.showConfirmDialog(null,
                        winner, "Do you want to play again?",
                        JOptionPane.YES_NO_OPTION);
                if (playAgain == JOptionPane.YES_OPTION) {
                    newGame("dummy");
                    aiPlaying = false;
                } else {
                    System.exit(1);
                }
            }
            _board.makeMove(aiMove);
            _display.repaint();
        }
        destinationClick = null;
        firstClick = null;
    }


    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {
        if (firstClick == null) {
            firstClick = new int[2];
            Point p = new Point(event.getX(), event.getY());
            firstClick = getRectangleIndices(p);
        }
    }

    /** Rectangles that correspond to the squares on my board. */
    private static final Rectangle[][] SQUARES = new Rectangle[8][8];
    /** The board widget. */
    private GameDisplay _display;
    /** The game I am consulting. */
    private GuiGame _game;
    /** The board we are getting. */
    private Board _board;
    /** Flag for if AI is being played. */
    private boolean aiPlaying = false;
    /** First clicked coordinates. */
    private int[] firstClick = null;
    /** Destination coordinates. */
    private int[] destinationClick = null;
}


