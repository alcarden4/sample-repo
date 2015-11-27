package db61b;
import org.junit.Test;
import static org.junit.Assert.*;

/** Class for testing Row
 * @author Anna Cardenas
 *  */

public class TestTable {

    Table t = Table.readTable("enrolled");
    @Test
    public void testReadTable() {
        assertEquals(t.title(0), "SID");
        assertEquals(t.title(1), "CCN");
        assertEquals(t.size(), 19);
        assertEquals(t.columnIndex("Grade"), 2);

        boolean noFile = false;
        try {
            Table t1 = Table.readTable("unicorns");
        } catch (DBException e) {
            noFile = true;
        }
        assertTrue(noFile);
    }

    @Test
    public void testPrintTable() {
        String[] columns = new String[] {"animal", "size", "weight"};
        Table newTable = new Table("table", columns);
        String[] s1 = new String[] {"dog", "small", "50"};
        Row r1 = new Row(s1);
        String[] s2 = new String[] {"bird", "medium", "5"};
        Row r2 = new Row(s2);
        String[] s3 = new String[] {"cat", "large", "40"};
        Row r3 = new Row(s3);
        newTable.add(r1);
        newTable.add(r2);
        newTable.add(r3);
        newTable.print();
    }

    @Test
    public void testWriteTable() {
        String[] columns = new String[] {"Animal", "Size", "Weight"};
        Table newTable = new Table("table", columns);
        String[] s1 = new String[] {"dog", "small", "50"};
        Row r1 = new Row(s1);
        String[] s2 = new String[] {"bird", "medium", "5"};
        Row r2 = new Row(s2);
        String[] s3 = new String[] {"cat", "large", "40"};
        Row r3 = new Row(s3);
        newTable.add(r1);
        newTable.add(r2);
        newTable.add(r3);
        newTable.writeTable("newTable");
    }
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(TestTable.class));
    }
}
