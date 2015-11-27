package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import static db61b.Utils.*;

/** A single table in a database.
 *  @author Anna Cardenas
 */
class Table implements Iterable<Row> {
    /** A new Table named NAME whose columns are given by COLUMNTITLES,
     *  which must be distinct (else exception thrown). */
    Table(String name, String[] columnTitles) {
        _name = name;
        _rows = new HashSet<Row>();
        _titles = columnTitles;
        Set<String> cTitles = new HashSet<String>(Arrays.asList(_titles));
        if (_titles.length != cTitles.size()) {
            throw error("Error: columns not distinct");
        }
    }

    /** A new Table named NAME whose column names are give by COLUMNTITLES. */
    Table(String name, List<String> columnTitles) {
        this(name, columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    int numColumns() {
        return _titles.length;
    }

    /** Returns my name. */
    String name() {
        return _name;
    }

    /** Returns a TableIterator over my rows in an unspecified order. */
    TableIterator tableIterator() {
        return new TableIterator(this);
    }

    /** Returns an iterator that returns my rows in an unspecified order. */
    @Override
    public Iterator<Row> iterator() {
        return _rows.iterator();
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    String title(int k) {
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    int columnIndex(String title) {
        for (int i = 0; i < _titles.length; i++) {
            if (_titles[i].equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of Rows in this table. */
    int size() {
        return _rows.size();
    }

    /** Add ROW to THIS if no equal row already exists.  Return true if anything
     *  was added, false otherwise. */
    boolean add(Row row) {
        if (_rows.contains(row)) {
            return false;
        }
        if (row.size() != _titles.length) {
            throw error("Error: inserted row has wrong length");
        } else {
            _rows.add(row);
            return true;
        }
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(name, columnNames);

            while (input.ready()) {
                String rowLine = input.readLine();
                if (rowLine != null) {
                    String [] line = rowLine.split(",");
                    Row tableRows = new Row(line);
                    table.add(tableRows);
                }
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }
    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            int k = 0;
            for (int i = 0; i < _titles.length; i++) {
                output.printf(_titles[i]);
                k++;
                if (k < _titles.length) {
                    output.printf(",");
                }
            }
            output.println();
            Iterator<Row> rowItr = _rows.iterator();
            while (rowItr.hasNext()) {
                Row next = rowItr.next();
                int x = 0;
                for (int i = 0; i < _titles.length; i++) {
                    output.printf(next.get(i));
                    x++;
                    if (x < _titles.length) {
                        output.printf(",");
                    }
                }
                output.println();
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        Iterator<Row> rowItr = _rows.iterator();
        while (rowItr.hasNext()) {
            System.out.print("  ");
            Row next = rowItr.next();
            for (int i = 0; i < _titles.length; i++) {
                System.out.print(next.get(i) + " ");
            }
            System.out.println();
        }
    }
    /** Will return the set of rows at index I. */
    Row getRows(int i) {
        if (_rows.size() == 0) {
            throw error("This table has no rows to select");
        }
        Row [] rows = _rows.toArray(new Row[_rows.size()]);
        return rows[i];
    }

    /** Returns the size of the rows. */
    int sizeRows() {
        return _rows.size();
    }

    /** My name. */
    private final String _name;
    /** My column titles. */
    private String[] _titles;
    /** My rows in the table. */
    private Set<Row> _rows;
}

