package loa;
import static loa.Piece.*;
import static loa.Main.*;

/**
 * Represents a GUI version of lines of action.
 *
 * @author Anna Cardenas
 */
class GuiGame extends Game {
    /**
     * @author Anna Cardenas
     */
    private LoaGui _display;

    /** A new series of Games. */
    GuiGame() {
        _board = new Board();
        _display = new LoaGui("loa", this);
    }

    @Override
    /** Return the current board. */
    public Board getBoard() {
        return _board;
    }

    /** The official game board. */
    private Board _board;
}
