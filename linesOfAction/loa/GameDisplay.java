package loa;

import ucb.gui.Pad;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Anna Cardenas
 */
class GameDisplay extends Pad {
    /** Color of display field. */
    private static final Color BACKGROUND_COLOR = Color.white;
    /* Coordinates and lengths in pixels unless otherwise stated. */
    /** Preferred dimensions of the playing surface. */
    private static final int BOARD_WIDTH = 800, BOARD_HEIGHT = 800;
    /** Our current board. */
    private Board _board;
    /** Number ONE used in subtraction. */
    private static final int ONE = 1;
    /** Number ONEHUNDREDTWO used in subtraction. */
    private static final int ONEHUNDREDTWO = 102;
    /** Size of pawns. */
    private static final int PAWN_SIZE = 70;

    /** A graphical representation of GAME. */
    public GameDisplay(Game game) {
        _game = game;
        _board = game.getBoard();
        setPreferredSize(BOARD_WIDTH, BOARD_HEIGHT);
    }


    @Override
    public synchronized void paintComponent(Graphics2D g) {
        Color color = Color.white;
        for (int c = 1; c < BOARD_WIDTH; c += 100) {
            if (color == Color.gray) {
                color = Color.darkGray;
                g.setColor(color);
            } else {
                color = Color.gray;
                g.setColor(color);
            }
            for (int r = 1; r < BOARD_HEIGHT; r += 100) {
                if (color == Color.gray) {
                    color = Color.darkGray;
                    g.setColor(color);
                } else {
                    color = Color.gray;
                    g.setColor(color);
                }
                g.fillRect(r, c, 100, 100);
            }
        }

        for (int col = 1; col <= 8; col++) {
            for (int row = 1; row <= 8; row++) {
                int c = (col - ONE) * ONEHUNDREDTWO;
                int r = (row - ONE) * ONEHUNDREDTWO;
                if (_board.get(col, row).equals(Piece.WP)) {
                    g.setColor(Color.white);
                    g.fillOval(c, r, PAWN_SIZE, PAWN_SIZE);
                } else if (_board.get(col, row).equals(Piece.BP)) {
                    g.setColor(Color.black);
                    g.fillOval(c, r, PAWN_SIZE, PAWN_SIZE);
                }
            }
        }
    }

    /** Game I am displaying. */
    private Game _game;
}
