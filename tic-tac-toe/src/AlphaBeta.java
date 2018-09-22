import java.util.Vector;

/**
 * Created by Anton Stagge and Cristian Osorio Bretti on 2018-09-20.
 */
public class AlphaBeta {
    private static int originalPlayer;
    private static Deadline dl;

    public static GameState alphaBetaMinMax(GameState state, Vector<GameState> nextStates, Deadline deadline) {
        int maxDepth = 100;
        int init_alpha = -Integer.MAX_VALUE;
        int init_beta = Integer.MAX_VALUE;
        originalPlayer = state.getNextPlayer();
        dl = deadline;
        System.err.println("Time begin: " + deadline.timeUntil()/1000000);
        int max = -Integer.MAX_VALUE;
        int max_idx = -1;
        for (int child = 0; child < nextStates.size(); ++child) {
            int current = alphabeta(nextStates.get(child), maxDepth, init_alpha, init_beta, originalPlayer);
            if (current > max) {
                max = current;
                max_idx = child;
            }
        }
        System.err.println("Time left: " + deadline.timeUntil()/1000000);
        System.err.println("    CHOSE INDEX: " + max_idx);
        GameState best = nextStates.get(max_idx);
        return best;

    }

    private static int alphabeta(GameState state, int depth, int alpha, int beta, int player) {
        Vector<GameState> nextMoves = new Vector<>();
        state.findPossibleMoves(nextMoves);

        if (depth == 0 || nextMoves.size() == 0  || dl.timeUntil()/1000000 < 40) {
            // termial state or end of search depth
            int value = heuristic(state, originalPlayer) - heuristic(state, Player.otherPlayer(originalPlayer));
            return value;
        }

        if (player == Constants.CELL_X) {
            int v = -Integer.MAX_VALUE;
            for (int child = 0; child < nextMoves.size(); ++child) {
                int current = alphabeta(nextMoves.get(child), depth-1, alpha, beta, Constants.CELL_O);
                if (current > v) {
                    v = current;
                }
                if (v > alpha) {
                    alpha = v;
                }

                if (beta <= alpha) {
                    break;
                }
            }

            return v;
        }

        if (player == Constants.CELL_O) {
            int v = Integer.MAX_VALUE;
            for (int child = 0; child < nextMoves.size(); ++child) {
                int current = alphabeta(nextMoves.get(child), depth-1, alpha, beta, Constants.CELL_X);

                if (current < v) {
                    v = current;
                }
                if (v < beta) {
                    beta = v;
                }

                if (beta <= alpha) {
                    break;
                }
            }

            return v;
        }

        // never going to happen!
        throw new IndexOutOfBoundsException("GOT TO WHERE NO MAN GOT BEFORE!");
    }

    public static int heuristic(GameState state, int player) {
        //System.err.println("Heu for : " + player);
        // For every Row
        int rowTotal = 0;
        for (int row = 0; row < GameState.BOARD_SIZE; ++row) {
            int rowPoints = 0;
            int rowCount = 0;
            for (int col = 0; col < GameState.BOARD_SIZE; ++col) {
                int marked = state.at(row,col);
                if (marked == player) {
                    rowPoints += (rowCount+1)*2;
                    ++rowCount;
                } else if (marked == Player.otherPlayer(player)) {
                    rowPoints = 0;
                    break;
                }
            }
            //System.err.println("row " + row + " got " + rowPoints);
            rowTotal += rowPoints;
        }
        //System.err.println("total row: " + rowTotal);

        // For every Column
        int colTotal = 0;
        for (int col = 0; col < GameState.BOARD_SIZE; ++col) {
            int colPoints = 0;
            int colCount = 0;
            for (int row = 0; row < GameState.BOARD_SIZE; ++row) {
                int marked = state.at(row,col);
                if (marked == player) {
                    colPoints += (colCount+1)*2;
                    ++colCount;
                } else if (marked == Player.otherPlayer(player)) {
                    colPoints = 0;
                    break;
                }
            }
            //System.err.println("col " + col + " got " + colPoints);
            colTotal += colPoints;

        }
        //System.err.println("total col: " + colTotal);

        // For one diagonal
        int diaOne = 0;
        int diaCount = 0;
        for (int i = 0; i < GameState.BOARD_SIZE; ++i) {
            if (state.at(i,i) == player) {
                diaOne += (diaCount+1)*2;
                ++diaCount;
            } else if (state.at(i, i) == Player.otherPlayer(player)) {
                diaOne = 0;
                break;
            }
        }
        //System.err.println("total diaOne: " + diaOne);

        // For the other diagonal
        int diaTwo = 0;
        diaCount = 0;
        for (int i = 0; i < GameState.BOARD_SIZE; ++i) {
            if (state.at((GameState.BOARD_SIZE-1)-i, i) == player) {
                diaTwo += (diaCount+1)*2;
                ++diaCount;
            } else if (state.at((GameState.BOARD_SIZE-1)-i, i) == Player.otherPlayer(player)) {
                diaTwo = 0;
                break;
            }
        }
        //System.err.println("total diaTwo: " + diaTwo);

        return rowTotal + colTotal + diaOne + diaTwo;
    }

    private static class ReturnTuple {
        public int value;
        public int cameFrom;

        ReturnTuple(int v, int c) {
            value = v;
            cameFrom = c;
        }
    }


}
