package db61b;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

/** Class for testing Row
 * @author Anna Cardenas
 *  */

public class TestRow {

    @Test
    public void testRowSize() {
        Literal l = new Literal("first");
        Literal l2 = new Literal("second");
        Literal l3 = new Literal("third");
        ArrayList<Column> list = new ArrayList<Column>();
        list.add(l);
        list.add(l2);
        list.add(l3);
        Row r = new Row(list);
        assertEquals(3, r.size());
        assertEquals("first", r.get(0));
    }

    @Test
    public void testRow() {
        Row r = new Row(new String[]{"My", "cats",
                                     "are", "the", "best."});
        Row r1 = new Row(new String[]{"This", "is", "how", "I", "pass."});
        Row r2 = new Row(new String[]{"Plz", "hope", "I", "pass"});
        assertEquals(5, r.size());
        assertEquals("cats", r.get(1));
        assertEquals(true, r.equals(r));
        assertEquals(false, r.equals(r1));
        assertEquals(false, r.equals(r2));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(TestRow.class));
    }
}
