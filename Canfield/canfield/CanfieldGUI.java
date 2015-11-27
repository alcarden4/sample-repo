package canfield;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

import java.awt.event.MouseEvent;

/**
 * A top-level GUI for Canfield solitaire.
 *
 * @author Anna Cardenas
 */
class CanfieldGUI extends TopLevel {
    /** Initiates cardCoords to keep track of coordinates. */
    private int[] cardCoords = null;
    /** Initiates destCoords to keep track of coordinates. */
    private int[] destCoords = null;

    /** A new window with given TITLE and displaying GAME. */
    CanfieldGUI(String title, Game game) {
        super(title, true);
        _game = game;
        _display = new GameDisplay(game);
        addMenuButton("Game->New Game", "newGame");
        addMenuButton("Game->Quit", "quit");
        addMenuButton("Game->Undo", "undo");
        add(_display, new LayoutSpec("y", 2, "width", 2));
        _display.setMouseHandler("click", this, "mouseClicked");
        _display.setMouseHandler("release", this, "mouseReleased");
        _display.setMouseHandler("drag", this, "mouseDragged");

        display(true);
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
        _game.deal();
        _display.repaint();
    }

    /** Responds to the Undo Button. */
    public void undo(String dummy) {
        _game.undo();
        _display.repaint();
    }

    /** MOUSE ACTIONS FOR GAME */

    /** Action in response to mouse-clicking event EVENT. */

    public synchronized void mouseClicked(MouseEvent event) {
        int[] click = { event.getX(), event.getY() };
        if (inBetween(click, sCoords)) {
            _game.stockToWaste();
        }
        _display.repaint();
    }

    /** Action in response to mouse-released event EVENT. */
    public synchronized void mouseReleased(MouseEvent event) {
        destCoords = new int[2];
        destCoords[0] = event.getX();
        destCoords[1] = event.getY();
        checkRtoF();
        checkTtoF();
        checkWtoF();
        checkRtoT();
        checkWtoT();
        checkTtoT();
        checkTtoT2();
        checkFtoT1();
        checkFtoT2();
        cardCoords = null;
        destCoords = null;

    }

    /** Method for checking Tableau to Foundation moves. */
    public void checkTtoF() {
        if (inBetween(destCoords, fCoords) && inBetween(cardCoords, t1Coords)) {
            _game.tableauToFoundation(1);
            _display.repaint();
        }
        if (inBetween(destCoords, fCoords) && inBetween(cardCoords, t2Coords)) {
            _game.tableauToFoundation(2);
            _display.repaint();
        }
        if (inBetween(destCoords, fCoords) && inBetween(cardCoords, t3Coords)) {
            _game.tableauToFoundation(3);
            _display.repaint();
        }
        if (inBetween(destCoords, fCoords) && inBetween(cardCoords, t4Coords)) {
            _game.tableauToFoundation(4);
            _display.repaint();
        }
    }

    /** Method for checking Reserve to Foundation. */
    public void checkRtoF() {
        if (inBetween(destCoords, fCoords) && inBetween(cardCoords, rCoords)) {
            _game.reserveToFoundation();
            _display.repaint();
        }
    }

    /** Method for checking waste to foundation. */
    public void checkWtoF() {
        if (inBetween(destCoords, fCoords) && inBetween(cardCoords, wCoords)) {
            _game.wasteToFoundation();
            _display.repaint();
        }
    }

    /** Method for checking Reserve to Tableau. */
    public void checkRtoT() {
        if (inBetween(destCoords, t1Coords) && inBetween(cardCoords, rCoords)) {
            _game.reserveToTableau(1);
            _display.repaint();
        }
        if (inBetween(destCoords, t2Coords) && inBetween(cardCoords, rCoords)) {
            _game.reserveToTableau(2);
            _display.repaint();
        }
        if (inBetween(destCoords, t3Coords) && inBetween(cardCoords, rCoords)) {
            _game.reserveToTableau(3);
            _display.repaint();
        }
        if (inBetween(destCoords, t4Coords) && inBetween(cardCoords, rCoords)) {
            _game.reserveToTableau(4);
            _display.repaint();
        }
    }

    /** Method for checking Waste to Tableau. */
    public void checkWtoT() {
        if (inBetween(destCoords, t1Coords) && inBetween(cardCoords, wCoords)) {
            _game.wasteToTableau(1);
            _display.repaint();
        }
        if (inBetween(destCoords, t2Coords) && inBetween(cardCoords, wCoords)) {
            _game.wasteToTableau(2);
            _display.repaint();
        }
        if (inBetween(destCoords, t3Coords) && inBetween(cardCoords, wCoords)) {
            _game.wasteToTableau(3);
            _display.repaint();
        }
        if (inBetween(destCoords, t4Coords) && inBetween(cardCoords, wCoords)) {
            _game.wasteToTableau(4);
            _display.repaint();
        }
    }

    /** First method for checking Tableau to Tableau. */
    public void checkTtoT() {
        if (inBetween(destCoords, t1Coords)
                && inBetween(cardCoords, t2Coords)) {
            _game.tableauToTableau(2, 1);
            _display.repaint();
        }
        if (inBetween(destCoords, t1Coords)
                && inBetween(cardCoords, t3Coords)) {
            _game.tableauToTableau(3, 1);
            _display.repaint();
        }
        if (inBetween(destCoords, t1Coords)
                && inBetween(cardCoords, t4Coords)) {
            _game.tableauToTableau(4, 1);
            _display.repaint();
        }
        if (inBetween(destCoords, t2Coords)
                && inBetween(cardCoords, t1Coords)) {
            _game.tableauToTableau(1, 2);
            _display.repaint();
        }
        if (inBetween(destCoords, t2Coords)
                && inBetween(cardCoords, t3Coords)) {
            _game.tableauToTableau(3, 2);
            _display.repaint();
        }
        if (inBetween(destCoords, t2Coords)
                && inBetween(cardCoords, t4Coords)) {
            _game.tableauToTableau(4, 2);
            _display.repaint();
        }
    }
    /** Second method for checking Tableau to Tableau.*/
    public void checkTtoT2() {
        if (inBetween(destCoords, t3Coords)
                && inBetween(cardCoords, t1Coords)) {
            _game.tableauToTableau(1, 3);
            _display.repaint();
        }
        if (inBetween(destCoords, t3Coords)
                && inBetween(cardCoords, t2Coords)) {
            _game.tableauToTableau(2, 3);
            _display.repaint();
        }
        if (inBetween(destCoords, t3Coords)
                && inBetween(cardCoords, t4Coords)) {
            _game.tableauToTableau(4, 3);
            _display.repaint();
        }
        if (inBetween(destCoords, t4Coords)
                && inBetween(cardCoords, t1Coords)) {
            _game.tableauToTableau(1, 4);
            _display.repaint();
        }
        if (inBetween(destCoords, t4Coords)
                && inBetween(cardCoords, t2Coords)) {
            _game.tableauToTableau(2, 4);
            _display.repaint();
        }
        if (inBetween(destCoords, t4Coords)
                && inBetween(cardCoords, t3Coords)) {
            _game.tableauToTableau(3, 4);
            _display.repaint();
        }
    }

    /** Method that checks Foundation to Tableau. */
    public void checkFtoT1() {
        if (inBetween(destCoords, t1Coords)
                && inBetween(cardCoords, f1Coords)) {
            _game.foundationToTableau(1, 1);
            _display.repaint();
        }
        if (inBetween(destCoords, t2Coords)
                && inBetween(cardCoords, f1Coords)) {
            _game.foundationToTableau(1, 2);
            _display.repaint();
        }
        if (inBetween(destCoords, t3Coords)
                && inBetween(cardCoords, f1Coords)) {
            _game.foundationToTableau(1, 3);
            _display.repaint();
        }
        if (inBetween(destCoords, t4Coords)
                && inBetween(cardCoords, f1Coords)) {
            _game.foundationToTableau(1, 4);
            _display.repaint();
        }
        if (inBetween(destCoords, t1Coords)
                && inBetween(cardCoords, f2Coords)) {
            _game.foundationToTableau(2, 1);
            _display.repaint();
        }
        if (inBetween(destCoords, t2Coords)
                && inBetween(cardCoords, f2Coords)) {
            _game.foundationToTableau(2, 2);
            _display.repaint();
        }
        if (inBetween(destCoords, t3Coords)
                && inBetween(cardCoords, f2Coords)) {
            _game.foundationToTableau(2, 3);
            _display.repaint();
        }
        if (inBetween(destCoords, t4Coords)
                && inBetween(cardCoords, f2Coords)) {
            _game.foundationToTableau(2, 4);
            _display.repaint();
        }
        if (inBetween(destCoords, t1Coords)
                && inBetween(cardCoords, f3Coords)) {
            _game.foundationToTableau(3, 1);
            _display.repaint();
        }
    }

    /** Method for checking Foundation to Tableau part 2. */
    public void checkFtoT2() {
        if (inBetween(destCoords, t2Coords)
                && inBetween(cardCoords, f3Coords)) {
            _game.foundationToTableau(3, 2);
            _display.repaint();
        }
        if (inBetween(destCoords, t3Coords)
                && inBetween(cardCoords, f3Coords)) {
            _game.foundationToTableau(3, 3);
            _display.repaint();
        }
        if (inBetween(destCoords, t4Coords)
                && inBetween(cardCoords, f3Coords)) {
            _game.foundationToTableau(3, 4);
            _display.repaint();
        }
        if (inBetween(destCoords, t1Coords)
                && inBetween(cardCoords, f4Coords)) {
            _game.foundationToTableau(4, 1);
            _display.repaint();
        }
        if (inBetween(destCoords, t2Coords)
                && inBetween(cardCoords, f4Coords)) {
            _game.foundationToTableau(4, 2);
            _display.repaint();
        }
        if (inBetween(destCoords, t3Coords)
                && inBetween(cardCoords, f4Coords)) {
            _game.foundationToTableau(4, 3);
            _display.repaint();
        }
        if (inBetween(destCoords, t4Coords)
                && inBetween(cardCoords, f4Coords)) {
            _game.foundationToTableau(4, 4);
            _display.repaint();
        }
    }

    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {

        if (cardCoords == null) {
            cardCoords = new int[2];
            cardCoords[0] = event.getX();
            cardCoords[1] = event.getY();

        }

    }

    /**
     * Returns True if CLICK coordinates are in between
     * CARD coordinates.
     */
    private boolean inBetween(int[] click, int[][] card) {
        if (click[0] >= card[0][0] && click[0] <= card[0][1]
                && click[1] >= card[1][0] && click[1] <= card[1][1]) {
            return true;
        }
        return false;
    }

    /** The board widget. */
    private final GameDisplay _display;

    /** The game I am consulting. */
    private final Game _game;

    /**
     * Pile Coordinates with first internal list as x coords and second as y
     * coordinates.
     */
    private static final int CARD_HEIGHT = 125;
    /** Card Width. */
    private static final int CARD_WIDTH = 90;
    /** The sCoords x coordinates. */
    private static final int SXCOORD = 102;
    /** The sCoords y coordinates. */
    private static final int SYCOORD = 554;
    /** The wCoords x and y coordinates. */
    private static final int WXCOORD = 202, WYCOORD = 554;
    /** The rCoords x and y coordinates. */
    private static final int RXCOORD = 103, RYCOORD = 353;
    /** The t1Coords x and y coordinates. */
    private static final int T1XCOORD = 703, T1YCOORD = 353;
    /** The t2Coords x and y coordinates. */
    private static final int T2XCOORD = 803, T2YCOORD = 353;
    /** The t3Coords x and y coordinates. */
    private static final int T3XCOORD = 903, T3YCOORD = 353;
    /** The t4Coords x and y coordinates. */
    private static final int T4XCOORD = 1003, T4YCOORD = 353;
    /** The fCoord's y coordinate. */
    private static final int FYCOORD = 103;
    /** All of fCoords x coordinates. */
    private static final int F1XCOORD = 704, F2XCOORD = 804, F3XCOORD = 904,
        F4XCOORD = 1004;
    /** The sCoords coordinates. */
    private int[][] sCoords = { { SXCOORD, SXCOORD + CARD_WIDTH },
        { SYCOORD, SYCOORD + CARD_HEIGHT } };
    /** The wCoords coordinates. */
    private int[][] wCoords = { { WXCOORD, WXCOORD + CARD_WIDTH },
        { WYCOORD, WYCOORD + CARD_HEIGHT } };
    /** The rCoords coordinates. */
    private int[][] rCoords = { { RXCOORD, RXCOORD + CARD_WIDTH },
        { RYCOORD, RYCOORD + CARD_WIDTH } };
    /** The t1Coords coordinates. */
    private int[][] t1Coords = { { T1XCOORD, T1XCOORD + CARD_WIDTH },
        { T1YCOORD, T1YCOORD + CARD_HEIGHT } };
    /** The t2Coords coordinates. */
    private int[][] t2Coords = { { T2XCOORD, T2XCOORD + CARD_WIDTH },
        { T2YCOORD, T2YCOORD + CARD_HEIGHT } };
    /** The t3Coords coordinates. */
    private int[][] t3Coords = { { T3XCOORD, T3XCOORD + CARD_WIDTH },
        { T3YCOORD, T3YCOORD + CARD_HEIGHT } };
    /** The t4Coords coordinates. */
    private int[][] t4Coords = { { T4XCOORD, T4XCOORD + CARD_WIDTH },
        { T4YCOORD, T4YCOORD + CARD_HEIGHT } };
    /** The f1Coords coordinates. */
    private int[][] f1Coords = { { F1XCOORD, F1XCOORD + CARD_WIDTH },
        { FYCOORD, FYCOORD + CARD_HEIGHT } };
    /** The f2Coords coordinates. */
    private int[][] f2Coords = { { F2XCOORD, F2XCOORD + CARD_WIDTH },
        { FYCOORD, FYCOORD + CARD_HEIGHT } };
    /** The f3Coords coordinates. */
    private int[][] f3Coords = { { F3XCOORD, F3XCOORD + CARD_WIDTH },
        { FYCOORD, FYCOORD + CARD_HEIGHT } };
    /** The f4Coords coordinates. */
    private int[][] f4Coords = { { F4XCOORD, F4XCOORD + CARD_WIDTH },
        { FYCOORD, FYCOORD + CARD_HEIGHT } };
    /** The f3Coords coordinates. */
    private int[][] fCoords = { { F1XCOORD, F4XCOORD + CARD_WIDTH },
        { FYCOORD, FYCOORD + CARD_HEIGHT } };
}
