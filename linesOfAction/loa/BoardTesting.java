package loa;

import org.junit.Test;
import static loa.Piece.BP;
import static loa.Piece.EMP;
import static loa.Piece.WP;
import static org.junit.Assert.*;
import java.util.Iterator;
/**
 * Testing for board methods.
 *
 * @author Anna Cardenas
 */

public class BoardTesting {

    @Test
    public void testcopyFrom() {
        Piece[][] somePieces = { { EMP, BP, BP, BP, BP, BP, BP, EMP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, BP, EMP, EMP, EMP, EMP, WP },
            { WP, WP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { EMP, BP, BP, BP, BP, BP, BP, EMP } };
        Piece[][] otherPieces = { { EMP, BP, BP, BP, BP, BP, BP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { EMP, BP, BP, BP, BP, BP, BP, EMP } };
        Board board = new Board(somePieces, BP);
        Board otherboard = new Board(otherPieces, BP);
        board.copyFrom(otherboard);
        for (int c = 1; c < 9; c++) {
            for (int r = 1; r < 9; r++) {
                assertEquals(board.get(c, r), board.get(c, r));
            }
        }

    }

    @Test
    public void testGet() {
        Piece[][] somePieces = { { EMP, BP, BP, BP, BP, BP, BP, EMP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, BP, EMP, EMP, EMP, EMP, WP },
            { WP, WP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { EMP, BP, BP, BP, BP, BP, BP, EMP } };
        Board board = new Board(somePieces, BP);
        assertEquals(board.get(1, 1), EMP);
        assertEquals(board.get(4, 8), BP);
        assertEquals(board.get(3, 5), WP);

    }

    @Test
    public void testHashCode() {
        Piece[][] firstMatrix = { { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP } };
        Board firstBoard = new Board(firstMatrix, BP);
        Piece[][] secondMatrix = { { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP } };
        Board secondBoard = new Board(secondMatrix, BP);
        assertEquals(firstBoard.hashCode(), secondBoard.hashCode());
        Piece[][] differentMatrix = {
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, WP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP } };
        Board differentBoard = new Board(differentMatrix, BP);
        assertFalse(secondBoard.hashCode() == differentBoard.hashCode());
        assertTrue(firstBoard.equals(secondBoard));
        assertFalse(secondBoard.equals(differentBoard));

    }

    @Test
    public void testSet() {
        Piece[][] somePieces = { { EMP, BP, BP, BP, BP, BP, BP, EMP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, BP, EMP, EMP, EMP, EMP, WP },
            { WP, WP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { EMP, BP, BP, BP, BP, BP, BP, EMP } };
        Board board = new Board(somePieces, BP);
        board.set(1, 1, BP);
        board.set(1, 2, BP);

        assertEquals(board.get(1, 1), BP);
        assertEquals(board.get(1, 2), BP);
    }

    @Test
    public void testBlocked() {
        Piece[][] somePieces = { { EMP, BP, BP, BP, BP, BP, BP, EMP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, BP, EMP, EMP, EMP, EMP, WP },
            { WP, WP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { EMP, BP, BP, BP, BP, BP, BP, EMP } };
        Board board = new Board(somePieces, BP);
        Move goodMove = Move.create(2, 1, 2, 4, board);
        Boolean isFalse = board.blocked(goodMove);
        assertFalse(isFalse);
        Move badMove = Move.create(3, 1, 3, 5, board);
        Boolean isTrue = board.blocked(badMove);
        assertTrue(isTrue);
        boolean isFalse2 = board.blocked(goodMove);
        assertFalse(isFalse2);
    }

    @Test
    public void testisLegal() {
        Piece[][] somePieces = { { EMP, BP, BP, BP, BP, BP, BP, EMP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, BP, EMP, EMP, EMP, EMP, WP },
            { WP, WP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { EMP, BP, BP, BP, BP, BP, BP, EMP } };
        Board boardwp = new Board(somePieces, WP);
        Board boardbp = new Board(somePieces, BP);
        Move badmove = Move.create(3, 3, 5, 3, boardbp);
        Move goodmove = Move.create(2, 4, 2, 7, boardwp);
        boolean goodtest = boardwp.isLegal(goodmove);
        boolean badtest = boardbp.isLegal(badmove);
        assertEquals(false, badtest);
        assertEquals(true, goodtest);
    }

    @Test
    public void testpiecesContiguous() {
        Piece[][] somePieces = { { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP } };
        Board board = new Board(somePieces, WP);
        boolean test = board.piecesContiguous(WP);
        assertEquals(true, test);
        Piece[][] otherPieces = { { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, BP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, BP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP } };
        Board board2 = new Board(otherPieces, BP);
        boolean test2 = board2.piecesContiguous(BP);
        assertEquals(false, test2);

        Piece[][] falseBoard = { { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, WP, EMP, EMP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP } };
        Board board3 = new Board(falseBoard, WP);
        boolean test3 = board3.piecesContiguous(WP);
        assertEquals(false, test3);
    }

    @Test
    public void testPiecesContiguousMore() {
        Piece[][] falseBoard1 = { { EMP, EMP, EMP, EMP, EMP, EMP, WP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, WP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP } };
        Board board4 = new Board(falseBoard1, WP);
        boolean test4 = board4.piecesContiguous(WP);
        assertEquals(false, test4);

        Piece[][] trueBoard = { { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, BP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP } };
        Board board5 = new Board(trueBoard, BP);
        boolean test5 = board5.piecesContiguous(BP);
        assertEquals(true, test5);

        Piece[][] emptyBoard = { { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP } };
        Board board6 = new Board(emptyBoard, BP);
        assert (!board6.piecesContiguous(BP));

        Piece[][] cornerBoard = { { BP, EMP, EMP, EMP, EMP, EMP, EMP, BP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { BP, EMP, EMP, EMP, EMP, EMP, EMP, BP } };
        Board board7 = new Board(cornerBoard, BP);
        assertFalse(board7.piecesContiguous(BP));
    }

    @Test
    public void testmoveIterator() {
        Piece[][] initialPieces = { { EMP, BP, BP, BP, BP, BP, BP, EMP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { EMP, BP, BP, BP, BP, BP, BP, EMP } };
        Board board = new Board(initialPieces, BP);
        Iterator<Move> mi = board.iterator();
        int counter = 0;
        while (mi.hasNext()) {
            counter = counter + 1;
            mi.next();
        }
        assertEquals(36, counter);
    }

    @Test
    public void testPieceCountAlong() {
        Piece[][] somePieces = { { EMP, BP, BP, BP, BP, BP, BP, EMP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, BP, EMP, EMP, EMP, EMP, WP },
            { WP, WP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, WP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { WP, EMP, EMP, EMP, EMP, EMP, EMP, WP },
            { EMP, BP, BP, BP, BP, BP, BP, BP } };
        Board board = new Board(somePieces, BP);
        Direction N = Direction.N;
        int piecesAlongNorth = board.pieceCountAlong(1, 1, N);
        assertEquals(6, piecesAlongNorth);
        Direction E = Direction.E;
        int piecesAlongEast = board.pieceCountAlong(1, 1, E);
        assertEquals(6, piecesAlongEast);
        Direction ne = Direction.NE;
        int piecesAlongNorthEast = board.pieceCountAlong(1, 1, ne);
        assertEquals(2, piecesAlongNorthEast);
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(BoardTesting.class));
    }
}
