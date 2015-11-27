package canfield;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests of the Game class.
 *
 * @author Anna Cardenas
 */

public class GameTest {

    /** Example. */
    @Test
    public void testInitialScore() {
        Game g = new Game();
        g.deal();
        assertEquals(5, g.getScore());
    }

    @Test
    public void testStockToWasteUndo() {
        Game g = new Game();
        g.deal();
        g.stockToWaste();
        g.stockToWaste();
        Game prev = Game.gstates.get(Game.gstates.size() - 1);
        g.undo();

        assertTrue(prev.topWaste().equals(g.topWaste()));

    }

    @Test
    public void testReserveToFoundationUndo() {
        Game g = new Game();

        g.setFoundation(Card.C10, Card.H10);
        g.setReserve(Card.D10);
        g.reserveToFoundation();

        Game prev = Game.gstates.get(Game.gstates.size() - 1);
        g.undo();

        assertTrue(g.topReserve().equals(prev.topReserve()));

    }

    @Test
    public void testWasteToFoundation() {
        Game g = new Game();
        g.setFoundation(Card.C10, Card.H10);
        g.setWaste(Card.D10);
        g.wasteToFoundation();
        Game prev = Game.gstates.get(Game.gstates.size() - 1);
        g.undo();

        assertTrue(g.topWaste().equals(prev.topWaste()));

    }
}
