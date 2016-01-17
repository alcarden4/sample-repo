package loa;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/** An automated Player.
 *  @author Anna Cardenas */
class MachinePlayer extends Player {

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
        _piece = side;
        _game = game;
    }

    @Override
    Move makeMove() {
        Move move = findBestMove(_piece, _game.getBoard(),
                2, Integer.MIN_VALUE);
        System.out.println(move.movedPiece().abbrev().toUpperCase()
                + "::" + move.toString());
        return move;
    }

    /** RETURN the best move for PLAYER on BOARD at DEPTH with CUTOFF. */
    private Move findBestMove(Piece player, Board board, int depth,
            double cutoff) {
        Board newBoard = new Board(board);
        Move bestSoFar = newBoard.iterator().next();
        Iterator<Move> moveIterator = board.iterator();
        if (newBoard.piecesContiguous(player)) {
            return bestSoFar;
        } else if (newBoard.piecesContiguous(player.opposite())) {
            return guessBestMove(player, newBoard, cutoff);
        } else if (depth == 0) {
            return guessBestMove(player, newBoard, cutoff);
        }
        while (moveIterator.hasNext()) {
            Move nextMove = moveIterator.next();
            newBoard.makeMove(nextMove);
            int boardValue = eval(newBoard);
            Move response = findBestMove(player.opposite(),
                    newBoard, depth - 1, -boardValue);
            newBoard.makeMove(response);
            int responseVal = eval(newBoard);
            newBoard.retract();
            newBoard.retract();
            if (-responseVal > boardValue) {
                boardValue = -responseVal;
                bestSoFar = nextMove;
                if (boardValue >= cutoff) {
                    break;
                }
            }
        }
        return bestSoFar;
    }

    /** Static evaluation. RETURN guessed best move for PLAYER
     *  on BOARD with CUTOFF value. */
    private Move guessBestMove(Piece player, Board board, double cutoff) {
        Board newBoard = new Board(board);
        Move bestSoFar;
        Iterator<Move> moveIterator = board.iterator();
        bestSoFar = moveIterator.next();
        newBoard.retract();
        while (moveIterator.hasNext()) {
            Move nextMove = moveIterator.next();
            int val = eval(newBoard);
            newBoard.makeMove(nextMove);
            if (eval(newBoard) > val) {
                bestSoFar = nextMove;
                val = eval(newBoard);
                if (val >= cutoff) {
                    break;
                }
            }
            newBoard.retract();
        }
        return bestSoFar;
    }

    /** Returns an evaluation of the BOARD.
     *  Higher the number the better move. */
    private static int eval(Board board) {
        if (board.piecesContiguous(board.turn())) {
            return Integer.MAX_VALUE;
        } else if (board.piecesContiguous(board.turn().opposite())) {
            return Integer.MIN_VALUE;
        } else {
            Random r = new Random();
            return r.nextInt();
        }
    }

    /** The game. */
    private static Game _game;
    /** The player's piece. */
    private static Piece _piece;
    /** All the previous board states from prior moves. */
    private HashSet<Board> _boardStates = new HashSet<Board>();

}
