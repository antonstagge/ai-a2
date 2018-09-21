import org.omg.CORBA.INTERNAL;

import java.util.Vector;

/**
 * Created by anton on 2018-09-20.
 */
public class AlphaBeta {
    public static void alphaBetaMinMax(GameState state, Vector<GameState> nextStates, Deadline deadline) {
        int maxDepth = 10;
        int init_alpha = -Integer.MAX_VALUE;
        int init_beta = Integer.MAX_VALUE;
        int player = state.getNextPlayer();
        int nextMoveIndex = alphabeta(state, maxDepth, init_alpha, init_alpha, player, -1).cameFrom;

    }

    private static ReturnTuple alphabeta(GameState state, int depth, int alpha, int beta, int player, int cameFrom) {
        Vector<GameState> nextMoves = new Vector<>();
        state.findPossibleMoves(nextMoves);

        if (depth == 0 || nextMoves.size() == 0) {
            // termial state or end of search depth
            int value = heuristic(state, player);
            return new ReturnTuple(value, cameFrom);
        }

        if (player == Constants.CELL_X) {
            ReturnTuple v = new ReturnTuple(-Integer.MAX_VALUE, -1);
            for (int child = 0; child < nextMoves.size(); ++child) {
                ReturnTuple current = alphabeta(nextMoves.get(child), depth-1, alpha, beta, Constants.CELL_O, child);

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
        return new ReturnTuple(1,1);
    }

    private static int heuristic(GameState state, int player) {
        return 1;
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
