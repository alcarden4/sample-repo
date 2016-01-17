package loa;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Formatter;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Direction.*;

/**
 * Represents the state of a game of Lines of Action.
 *
 * @author Anna Cardenas
 */
class Board implements Iterable<Move> {

    /** Size of a board. */
    static final int M = 8;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /**
     * A Board whose initial contents are taken from INITIALCONTENTS and in
     * which the player playing TURN is to move. The resulting Board has
     * get(col, row) == INITIALCONTENTS[row-1][col-1] Assumes that PLAYER is not
     * null and INITIALCONTENTS is MxM.
     *
     * CAUTION: The natural written notation for arrays initializers puts the
     * BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        clear();
    }

    /**
     * A Board whose initial contents and state are copied from BOARD.
     */
    Board(Board board) {
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        _moves.clear();
        for (int r = 1; r <= M; r += 1) {
            for (int c = 1; c <= M; c += 1) {
                set(c, r, contents[r - 1][c - 1]);
            }
        }
        _turn = side;
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        _moves.clear();
        _moves.addAll(board._moves);
        _turn = board._turn;
        for (int i = 0; i < _pieceMatrix.length; i++) {
            for (int j = 0; j < _pieceMatrix[i].length; j++) {
                _pieceMatrix[i][j] = board._pieceMatrix[i][j];
            }
        }
    }

    /**
     * Return the contents of column C, row R, where 1 <= C,R <= 8, where column
     * 1 corresponds to column 'a' in the standard notation.
     */
    Piece get(int c, int r) {
        if (c > 8 || c < 1 || r > 8 || r < 1) {
            throw new Error("Invalid row or column on board.");
        }
        return _pieceMatrix[r - 1][c - 1];
    }

    /**
     * Return the contents of the square SQ. SQ must be the standard printed
     * designation of a square (having the form cr, where c is a letter from a-h
     * and r is a digit from 1-8).
     */
    Piece get(String sq) {
        return get(col(sq), row(sq));
    }

    /**
     * Return the column number (a value in the range 1-8) for SQ. SQ is as for
     * {@link get(String)}.
     */
    static int col(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(0) - 'a' + 1;
    }

    /**
     * Return the row number (a value in the range 1-8) for SQ. SQ is as for
     * {@link get(String)}.
     */
    static int row(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(1) - '0';
    }

    /**
     * Set the square at column C, row R to V, and make NEXT the next side to
     * move, if it is not null.
     */
    void set(int c, int r, Piece v, Piece next) {
        if (c > 8 || c < 1 || r > 8 || r < 1) {
            throw new Error("Invalid row or column on board.");
        }
        _pieceMatrix[r - 1][c - 1] = v;
        if (next != null) {
            _turn = next;
        }
    }

    /** Set the square at column C, row R to V. */
    void set(int c, int r, Piece v) {
        set(c, r, v, null);
    }

    /** Assuming isLegal(MOVE), make MOVE. */
    void makeMove(Move move) {
        if (isLegal(move)) {
            _moves.add(move);
            Piece replaced = move.replacedPiece();
            int c0 = move.getCol0(), c1 = move.getCol1();
            int r0 = move.getRow0(), r1 = move.getRow1();
            if (replaced != EMP) {
                set(c1, r1, EMP);
            }
            set(c1, r1, move.movedPiece());
            set(c0, r0, EMP);

            _turn = _turn.opposite();
        }

    }

    /**
     * Retract (unmake) one move, returning to the state immediately before that
     * move. Requires that movesMade () > 0.
     */
    void retract() {
        if (movesMade() > 0) {
            Move move = _moves.remove(_moves.size() - 1);
            Piece replaced = move.replacedPiece();
            int c0 = move.getCol0(), c1 = move.getCol1();
            int r0 = move.getRow0(), r1 = move.getRow1();
            Piece movedPiece = move.movedPiece();
            set(c1, r1, replaced);
            set(c0, r0, movedPiece);
            _turn = _turn.opposite();
        }
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Sets the board's turn to piece P. */
    protected void setTurn(Piece p) {
        _turn = p;
    }

    /** Returns the winning player's side. */
    protected Piece getWinner() {
        return _winner;
    }

    /** Return true iff MOVE is legal for the player currently on move. */
    boolean isLegal(Move move) {
        if (move == (null)) {
            return false;
        } else if (_turn != move.movedPiece()) {
            return false;
        }
        return move.length() == pieceCountAlong(move) && !blocked(move);
    }

    /** Return a sequence of all legal moves from this position. */
    Iterator<Move> legalMoves() {
        return new MoveIterator();
    }

    @Override
    public Iterator<Move> iterator() {
        return legalMoves();
    }

    /**
     * Return true if there is at least one legal move for the player on move.
     */
    public boolean isLegalMove() {
        return iterator().hasNext();
    }

    /** Return true iff either player has all his pieces continguous. */
    boolean gameOver() {
        return piecesContiguous(BP) || piecesContiguous(WP);
    }

    /** Return true iff SIDE's pieces are contiguous. */
    boolean piecesContiguous(Piece side) {
        Set<Point> consideredSet = new HashSet<Point>();
        Set<Point> surroundingSet = new HashSet<Point>();
        ArrayList<Point> pieceList = new ArrayList<Point>();

        for (int cols = 1; cols <= this._pieceMatrix.length; cols++) {
            for (int rows = 1; rows <= this._pieceMatrix.length; rows++) {
                if (this._pieceMatrix[rows - 1][cols - 1].equals(side)) {
                    pieceList.add(new Point(cols, rows));
                }
            }
        }

        while (consideredSet.size() < pieceList.size()) {
            Point point = pieceInSurroundings(surroundingSet, pieceList,
                    consideredSet);
            if (point == null) {
                return false;
            }

            updateSurroundingSet(point, surroundingSet);
            consideredSet.add(point);

        }

        _winner = side;
        return true;
    }

    /** Returns the point iff it is contained in the SURROUNDINGSET by
     *  checking PIECELIST and CONSIDEREDSET. */
    private Point pieceInSurroundings(Set<Point> surroundingSet,
            ArrayList<Point> pieceList, Set<Point> consideredSet) {
        for (Point p : pieceList) {
            if (!consideredSet.contains(p)) {
                if (surroundingSet.contains(p) || surroundingSet.isEmpty()) {
                    return p;
                }
            }
        }
        return null;
    }

    /** Updates the SURROUNDINGSET to points that surround given POINT. */
    private void updateSurroundingSet(Point point, Set<Point> surroundingSet) {
        Direction dir = NOWHERE;
        dir = dir.succ();
        while (dir != null) {
            if (inRange(point.x + dir.dc, point.y + dir.dr)) {
                Point nPoint = new Point(point.x + dir.dc, point.y + dir.dr);
                surroundingSet.add(nPoint);
            }
            dir = dir.succ();
        }

    }

    /** RETURNS true if C and R are between 1 and 8. */
    private boolean inRange(int c, int r) {
        if (c >= 1 && c <= 8 && r >= 1 && r <= 8) {
            return true;
        }
        return false;
    }

    /**
     * Return the total number of moves that have been made (and not retracted).
     * Each valid call to makeMove with a normal move increases this number by
     * 1.
     */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        return b.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        /**
         * Hashcode that is based on everything inside the array. This should
         * therefore be unique
         */
        return Arrays.deepHashCode(_pieceMatrix);
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = M; r >= 1; r -= 1) {
            out.format("    ");
            for (int c = 1; c <= M; c += 1) {
                out.format("%s ", get(c, r).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return the number of pieces in the line of action indicated by MOVE. */
    public int pieceCountAlong(Move move) {

        int startCol = move.getCol0();
        int startRow = move.getRow0();
        int endCol = move.getCol1();
        int endRow = move.getRow1();

        int c = startCol;
        int r = startRow;
        Direction dir;
        if (endCol > startCol) {
            if (endRow == startRow) {
                dir = E;
            } else if (endRow > startRow) {
                dir = NE;

            } else {
                dir = SE;
            }
        } else if (startCol > endCol) {
            if (startRow == endRow) {
                dir = W;
            } else if (startRow > endRow) {
                dir = SW;
            } else {
                dir = NW;
            }
        } else if (startCol == endCol) {
            if (startRow > endRow) {
                dir = S;
            } else {
                dir = N;
            }
        } else {
            dir = NOWHERE;
        }

        return pieceCountAlong(c, r, dir);
    }

    /**
     * Return the number of pieces in the line of action in direction DIR and
     * containing the square at column C and row R.
     */
    public int pieceCountAlong(int c, int r, Direction dir) {
        if (dir == NOWHERE) {
            return 0;
        }
        int cdelta = dir.dc;
        int rdelta = dir.dr;
        int startcol = c;
        int startrow = r;
        int pieceCounter = 0;
        if (_pieceMatrix[r - 1][c - 1].equals(EMP)) {
            pieceCounter = 1;
        }
        while (c - 1 < _pieceMatrix.length && c - 1 >= 0
                && r - 1 < _pieceMatrix.length && r - 1 >= 0) {

            if (!_pieceMatrix[r - 1][c - 1].equals(EMP)) {
                pieceCounter += 1;
            }
            c += cdelta;
            r += rdelta;
        }
        c = startcol;
        r = startrow;
        while (c - 1 < _pieceMatrix.length && c - 1 >= 0
                && r - 1 < _pieceMatrix.length && r - 1 >= 0) {
            if (!_pieceMatrix[r - 1][c - 1].equals(EMP)) {
                pieceCounter += 1;
            }
            c -= cdelta;
            r -= rdelta;
        }
        return pieceCounter - 1;
    }

    /**
     * Return true iff MOVE is blocked by an opposing piece or by a friendly
     * piece on the target square.
     */
    public boolean blocked(Move move) {

        int endCol = move.getCol1();
        int endRow = move.getRow1();
        int startCol = move.getCol0();
        int startRow = move.getRow0();
        int moveLength = move.length();

        Direction dir;
        if (endCol > startCol) {
            if (endRow == startRow) {
                dir = E;
            } else if (endRow > startRow) {
                dir = NE;

            } else {
                dir = SE;
            }
        } else if (startCol > endCol) {
            if (startRow == endRow) {
                dir = W;
            } else if (startRow > endRow) {
                dir = SW;
            } else {
                dir = NW;
            }
        } else if (startCol == endCol) {
            if (startRow > endRow) {
                dir = S;
            } else {
                dir = N;
            }
        } else {
            dir = NOWHERE;
        }
        Piece friendlyPiece = move.movedPiece();
        boolean result = false;
        if (_pieceMatrix[endRow - 1][endCol - 1].equals(friendlyPiece)) {
            return true;
        }
        for (int i = 0; i < moveLength; i++) {
            if (_pieceMatrix[startRow - 1][startCol - 1].equals(friendlyPiece)
                    || _pieceMatrix[startRow - 1][startCol - 1].equals(EMP)) {
                result = false;
            } else {
                return true;
            }
            startCol += dir.dc;
            startRow += dir.dr;
        }
        return result;
    }

    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES = {
            { EMP, BP, BP, BP, BP, BP, BP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { EMP, BP, BP, BP, BP, BP, BP, EMP } };

    /** List of all unretracted moves on this board, in order. */
    private final ArrayList<Move> _moves = new ArrayList<>();
    /** Current side on move. */
    private Piece _turn;
    /** Board width. */
    private int _width = 8;
    /** Board height. */
    private int _height = 8;
    /** Our game board pieces. */
    private Piece[][] _pieceMatrix = new Piece[_width][_height];
    /** Winning player. */
    private Piece _winner;

    /** An iterator returning the legal moves from the current board. */
    private class MoveIterator implements Iterator<Move> {
        /** Current piece under consideration. */
        private int _c, _r;
        /** Next direction of current piece to return. */
        private Direction _dir;
        /** Next move. */
        private Move _move;

        /** A new move iterator for turn(). */
        MoveIterator() {
            _c = 1;
            _r = 1;
            _dir = NOWHERE;
            incr();
        }

        @Override
        public boolean hasNext() {
            return _move != null;
        }

        @Override
        public Move next() {
            if (_move == null) {
                throw new NoSuchElementException("no legal move");
            }
            Move move = _move;
            incr();
            return move;
        }

        @Override
        public void remove() {
        }

        /** Advance to the next legal move. */
        private void incr() {
            for (int c = _c; c <= _width; c++) {
                for (int r = _r; r <= _height; r++) {
                    if (get(c, r).equals(_turn)) {
                        while (_dir != null) {
                            int spaces = pieceCountAlong(c, r, _dir);
                            Move move = Move.create(c, r, spaces, _dir,
                                    Board.this);
                            _dir = _dir.succ();
                            if (isLegal(move)) {
                                _move = move;
                                _c = c;
                                _r = r;
                                return;
                            }
                        }
                        _dir = NOWHERE;
                    }
                }
                _dir = NOWHERE;
                _r = 1;
            }
            _move = null;
        }
    }
}
