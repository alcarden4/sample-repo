package canfield;

import ucb.gui.Pad;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author Anna Cardenas
 */
class GameDisplay extends Pad {
    /** Color of display field. */
    private static final Color BACKGROUND_COLOR = Color.white;
    /* Coordinates and lengths in pixels unless otherwise stated. */
    /** Preferred dimensions of the playing surface. */
    private static final int BOARD_WIDTH = 1200, BOARD_HEIGHT = 800;
    /** Displayed dimensions of a card image. */
    private static final int CARD_HEIGHT = 125, CARD_WIDTH = 90;
    /** A graphical representation of GAME. */
    public GameDisplay(Game game) {
        _game = game;
        setPreferredSize(BOARD_WIDTH, BOARD_HEIGHT);
    }
    /** Return an Image read from the resource named NAME. */
    private Image getImage(String name) {
        InputStream in = getClass().getResourceAsStream("/canfield/resources/"
            + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }
    /** Return an Image of CARD. */
    private Image getCardImage(Card card) {
        return getImage("playing-cards/" + card + ".png");
    }
    /** Return an Image of the back of a card. */
    private Image getBackImage() {
        return getImage("playing-cards/blue-back.png");
    }
    /** Draw CARD at X, Y on G. */
    private void paintCard(Graphics2D g, Card card, int x, int y) {
        /** Stroke for null case. */
        if (card != null) {
            g.drawImage(getCardImage(card), x, y, CARD_WIDTH, CARD_HEIGHT,
                    null);
        }
    }

    /** Draw card back at X, Y on G. */
    private void paintBack(Graphics2D g, int x, int y) {
        g.drawImage(getBackImage(), x, y, CARD_WIDTH, CARD_HEIGHT, null);
    }
    /** X coordinate to use for start of foundation. */
    private static final int XCOORD = 700;
    /** Y coordinate to use for start of foundation. */
    private static final int YCOORD = 100;
    /** Will draw foundation with Graphics2D G. */
    private void dFoundation(Graphics2D g) {
        int x = XCOORD;
        int y = YCOORD;
        for (int i = 1; i <= 4; i++) {
            paintCard(g, _game.topFoundation(i), x, y);
            if (_game.topFoundation(i) == null) {
                Stroke nStroke = new BasicStroke(1);
                g.setColor(Color.BLACK);
                g.setStroke(nStroke);
                g.setBackground(Color.WHITE);
                g.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 5, 5);
            }
            x += 100;
        }
    }

    /** The change for each new tableau card. */
    private static final int DELTA = 20;
    /** The y coordinate for tableau. */
    private static final int TYCOORD = 350;
    /** Will draw tableau with Graphics2D G. */
    private void dTableau(Graphics2D g) {
        int x = XCOORD;
        int delta = DELTA;
        for (int i = 1; i <= Game.TABLEAU_SIZE; i++) {
            int y = TYCOORD;

            for (int j = _game.tableauSize(i); j >= 1; j--) {
                paintCard(g, _game.getTableau(i, j), x, y);
                y += delta;

            }
            paintCard(g, _game.topTableau(i), x, y);
            x += 100;

        }
    }

    /** X coordinate for reserve. */
    private static final int RXCOORD = 100;
    /** Y coordinate for reserve. */
    private static final int RYCOORD = 350;
    /** Will draw top of reserve pile with G. */
    private void dReserve(Graphics2D g) {
        int x = RXCOORD;
        int y = RYCOORD;
        paintCard(g, _game.topReserve(), x, y);
        if (_game.topReserve() == null) {
            Stroke nStroke = new BasicStroke(1);
            g.setColor(Color.BLACK);
            g.setStroke(nStroke);
            g.setBackground(Color.WHITE);
            g.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 5, 5);
        }

    }

    /** X coordinate for stock. */
    private static final int SXCOORD = 100;
    /** Y coordinate for stock. */
    private static final int SYCOORD = 550;
    /** Will draw top of stock pile with G. */
    private void dStock(Graphics2D g) {
        int x = SXCOORD;
        int y = SYCOORD;
        if (!_game.stockEmpty()) {
            paintBack(g, x, y);
        }
        Stroke nStroke = new BasicStroke(1);
        g.setColor(Color.BLACK);
        g.setStroke(nStroke);
        g.setBackground(Color.WHITE);
        g.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 5, 5);
    }
    /** X coordinate for waste. */
    private static final int WXCOORD = 200;
    /** Y coordinate for waste. */
    private static final int WYCOORD = 550;
    /** Will draw waste pile with G. */
    private void dWaste(Graphics2D g) {
        int x = WXCOORD;
        int y = WYCOORD;
        Stroke nStroke = new BasicStroke(1);
        g.setColor(Color.BLACK);
        g.setStroke(nStroke);
        g.setBackground(Color.WHITE);
        g.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 5, 5);
        paintCard(g, _game.topWaste(), x, y);
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        g.setColor(BACKGROUND_COLOR);

        dTableau(g);
        dReserve(g);
        dStock(g);
        dWaste(g);
        dFoundation(g);
    }

    /** Game I am displaying. */
    private final Game _game;

}
