import java.util.Vector;

/**
 * Created by anton on 2018-09-20.
 */
public class AlphaBeta {
    private static int originalPlayer;
    private static Deadline dl;

    public static GameState alphaBetaMinMax(GameState state, Vector<GameState> nextStates, Deadline deadline) {
        int maxDepth = 1;
        int init_alpha = -Integer.MAX_VALUE;
        int init_beta = Integer.MAX_VALUE;
        originalPlayer = state.getNextPlayer();
        dl = deadline;
        System.err.println("Time begin: " + deadline.timeUntil()/1000000);
        int nextMoveIndex = alphabeta(state, maxDepth, init_alpha, init_beta, originalPlayer, -1).cameFrom;
        System.err.println("Time left: " + deadline.timeUntil()/1000000);
        System.err.println("    CHOSE INDEX: " + nextMoveIndex);
        GameState best = nextStates.get(nextMoveIndex);
        return best;

    }

    private static ReturnTuple alphabeta(GameState state, int depth, int alpha, int beta, int player, int cameFrom) {
        Vector<GameState> nextMoves = new Vector<>();
        state.findPossibleMoves(nextMoves);

        if (depth == 0 || nextMoves.size() == 0  /*|| dl.timeUntil()/1000000 < 50*/) {
            // termial state or end of search depth
            if (dl.timeUntil()/1000000 < 50) {
                //System.err.println("dl (" + dl.timeUntil()/1000000 + ") ran out on depth: " + depth);
            }
            int value = heuristic(state, originalPlayer) - heuristic(state, Player.otherPlayer(originalPlayer));
            return new ReturnTuple(value, cameFrom);
        }

        if (player == Constants.CELL_X) {
            ReturnTuple v = new ReturnTuple(-Integer.MAX_VALUE, -1);
            for (int child = 0; child < nextMoves.size(); ++child) {
                ReturnTuple current = alphabeta(nextMoves.get(child), depth-1, alpha, beta, Constants.CELL_O, child);
                System.err.println("FOR CHILD ");
                System.err.println(nextMoves.get(child).toString(player));
                System.err.println("got heuristic: " + current.value);
                if (current.value > v.value) {
                    v = current;
                }
                if (v.value > alpha) {
                    alpha = v.value;
                }

                if (beta <= alpha) {
                    break;
                }
            }

            return v;
        }

        if (player == Constants.CELL_O) {
            ReturnTuple v = new ReturnTuple(Integer.MAX_VALUE, -1);
            for (int child = 0; child < nextMoves.size(); ++child) {
                ReturnTuple current = alphabeta(nextMoves.get(child), depth-1, alpha, beta, Constants.CELL_X, child);

                if (current.value < v.value) {
                    v = current;
                }
                if (v.value < beta) {
                    beta = v.value;
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
        int row = 0;
        for (int y = 0; y < GameState.BOARD_SIZE; ++y) {
            int rowPoints = 0;
            for (int x = 0; x < GameState.BOARD_SIZE; ++x) {
                int marked = state.at(x,y);
                if (marked == player) {
                    rowPoints += (x+1)*2;
                } else if (marked == Player.otherPlayer(player)) {
                    rowPoints = 0;
                    break;
                }
            }
            row += rowPoints;
        }
        int col = 0;
        for (int y = 0; y < GameState.BOARD_SIZE; ++y) {
            int colPoints = 0;
            for (int x = 0; x < GameState.BOARD_SIZE; ++x) {
                int marked = state.at(x,y);
                if (marked == player) {
                    colPoints += (x+1)*2;
                } else if (marked == Player.otherPlayer(player)) {
                    colPoints = 0;
                    break;
                }
            }
            col += colPoints;

        }

        int diaOne = 0;
        int diaTwo = 0;
        for (int i = 0; i < GameState.BOARD_SIZE; ++i) {

            if (state.at(i,i) == player) {
                diaOne += (i+1)*2;
            } else if (state.at(i, i) == Player.otherPlayer(player)) {
                diaOne = 0;
                break;
            }
        }
        for (int i = 0; i < GameState.BOARD_SIZE; ++i) {

            if (state.at((GameState.BOARD_SIZE-1)-i, i) == player) {
                diaTwo += (i+1)*2;
            } else if (state.at((GameState.BOARD_SIZE-1)-i, i) == Player.otherPlayer(player)) {
                diaTwo = 0;
                break;
            }
        }



        return row + col + diaOne + diaTwo;
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
