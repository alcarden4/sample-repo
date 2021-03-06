package loa;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static loa.Piece.*;
import static loa.Main.*;

/** Represents one game of Lines of Action.
 *  @author Anna Cardenas */
class Game {

    /** A new series of Games. */
    Game() {
        _randomSource = new Random();

        _players = new Player[2];
        _input = new BufferedReader(new InputStreamReader(System.in));
        _players[0] = new HumanPlayer(BP, this);
        _players[1] = new MachinePlayer(WP, this);
        _playing = false;
    }

    /** Return the current board. */
    Board getBoard() {
        return _board;
    }

    /** Quit the game. */
    private void quit() {
        System.exit(0);
    }

    /** Return a move.  Processes any other intervening commands as
     *  well.  Exits with null if the value of _playing changes. */
    Move getMove() {
        try {
            boolean playing0 = _playing;
            while (_playing == playing0) {
                prompt();

                String line = _input.readLine();
                if (line == null) {
                    quit();
                }

                line = line.trim();
                if (!processCommand(line)) {
                    Move move = Move.create(line, _board);
                    if (move == null) {
                        error("invalid move: %s%n", line);
                    } else if (!_playing) {
                        error("game not started");
                    } else if (!_board.isLegal(move)) {
                        error("illegal move: %s%n", line);
                    } else {
                        return move;
                    }
                }
            }
        } catch (IOException excp) {
            error(1, "unexpected I/O error on input");
        }
        return null;
    }

    /** Print a prompt for a move. */
    private void prompt() {
        Piece turn = _board.turn();
        String abrev = turn.abbrev();
        if (_playing) {
            System.out.print(abrev + "> ");
            System.out.flush();
        } else {
            System.out.print("-> ");
            System.out.flush();
        }
    }

    /** Describes a command with up to two arguments. */
    private static final Pattern COMMAND_PATN =
        Pattern.compile("(#|\\S+)\\s*(\\S*)\\s*(\\S*).*");

    /** If LINE is a recognized command other than a move, process it
     *  and return true.  Otherwise, return false. */
    private boolean processCommand(String line) {
        if (line.length() == 0) {
            return true;
        }
        Matcher command = COMMAND_PATN.matcher(line);
        if (command.matches()) {
            switch (command.group(1).toLowerCase()) {
            case "#":
                return true;
            case "manual":
                manualCommand(command.group(2).toLowerCase());
                return true;
            case "auto":
                autoCommand(command.group(2).toLowerCase());
                return true;
            case "seed":
                seedCommand(command.group(2));
                return true;
            case "clear":
                _playing = false; _board.clear();
                return true;
            case "start":
                _playing = true;
                return true;
            case "set":
                String colRow = command.group(2);
                try {
                    _playing = false;
                    int c = Board.col(colRow); int r = Board.row(colRow);
                    Piece piece = Piece.setValueOf(command.group(3));
                    _board.set(c, r, piece);
                    Piece oppositeTurn = piece.opposite();
                    if (!piece.equals(EMP)) {
                        _board.setTurn(oppositeTurn);
                    }
                    return true;
                } catch (IllegalArgumentException excp) {
                    error("Error: invalid arguments to set: %s %s", colRow,
                        command.group(3));
                    System.out.println();
                }
                return true;
            case "dump":
                System.out.println(_board);
                return true;
            case "quit":
                quit();
                return true;
            case "?":
                help();
                return true;
            case "help":
                help();
                return true;
            default:
                return false;
            }
        }
        return false;
    }

    /** Set player PLAYER ("white" or "black") to be a manual player. */
    private void manualCommand(String player) {
        try {
            Piece s = Piece.playerValueOf(player);
            _playing = false;
            _players[s.ordinal()] = new HumanPlayer(s, this);
        } catch (IllegalArgumentException excp) {
            error("unknown player: %s", player);
        }
    }

    /** Set player PLAYER ("white" or "black") to be an automated player. */
    private void autoCommand(String player) {
        try {
            Piece s = Piece.playerValueOf(player);
            _playing = false;
            _players[s.ordinal()] = new MachinePlayer(s, this);
        } catch (IllegalArgumentException excp) {
            error("unknown player: %s", player);
        }
    }

    /** Seed random-number generator with SEED (as a long). */
    private void seedCommand(String seed) {
        try {
            _randomSource.setSeed(Long.parseLong(seed));
        } catch (NumberFormatException excp) {
            error("Invalid number: %s", seed);
        }
    }

    /** Play this game, printing any results. */
    public void play() {
        HashSet<Board> positionsPlayed = new HashSet<Board>();
        _board = new Board();

        while (true) {
            int playerInd = _board.turn().ordinal();
            Move next;
            if (_playing) {
                if (_board.gameOver()) {
                    announceWinner();
                    _playing = false;
                    continue;
                }
                next = _players[playerInd].makeMove();
                assert !_playing || next != null;
            } else {
                getMove();
                next = null;
            }
            if (next != null) {
                assert _board.isLegal(next);
                _board.makeMove(next);
                if (_board.gameOver()) {
                    announceWinner();
                    _playing = false;
                }
            }
        }
    }

    /** Print an announcement of the winner. */
    private void announceWinner() {
        String winner = null;
        if (_board.getWinner().equals(WP)) {
            winner = "White";
        } else if (_board.getWinner().equals(BP)) {
            winner = "Black";
        }
        System.out.println(winner + " wins.");
    }

    /** Return an integer r, 0 <= r < N, randomly chosen from a
     *  uniform distribution using the current random source. */
    int randInt(int n) {
        return _randomSource.nextInt(n);
    }

    /** Print a help message. */
    void help() {
        String command =
              "Commands: Commands are whitespace-delimited.  Other trailing"
              + " text on a line"
            + "\n          is ignored. Comment lines begin with # and are"
            + " ignored."
            + "\n  start     Start playing from the current position."
            + "\n  uv-xy     A move from square uv to square xy.  Here u and v"
            + " are column"
            + "\n            designations (a-h) and v and y are row"
            + " designations (1-8): "
            + "\n  clear     Stop game and return to initial position."
            + "\n  seed N    Seed the random number with integer N."
            + "\n  auto P    P is white or black; makes P into an AI. Stops"
            + " game."
            + "\n  manual P  P is white or black; takes moves for P from"
            + " terminal. Stops game."
            + "\n  set cr P  Put P ('w', 'b', or empty) into square cr."
            + " Stops game."
            + "\n  dump      Display the board in standard format."
            + "\n  quit      End program."
            + "\n  help"
            + "\n?         This text.";
        System.out.println(command);
    }

    /** The official game board. */
    private Board _board;

    /** The _players of this game. */
    private Player[] _players = new Player[2];

    /** A source of random numbers, primed to deliver the same sequence in
     *  any Game with the same seed value. */
    private Random _randomSource;

    /** Input source. */
    private BufferedReader _input;

    /** True if actually playing (game started and not stopped or finished).
     */
    private boolean _playing;
}
