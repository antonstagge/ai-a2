import org.omg.CORBA.INTERNAL;

import java.util.Vector;

/**
 * Created by anton on 2018-09-20.
 */
public class AlphaBeta {
    public static GameState alphaBetaMinMax(GameState state, Vector<GameState> nextStates, Deadline deadline) {
        int maxDepth = 5;
        int init_alpha = -Integer.MAX_VALUE;
        int init_beta = Integer.MAX_VALUE;
        int player = state.getNextPlayer();
        int nextMoveIndex = alphabeta(state, maxDepth, init_alpha, init_beta, player, -1).cameFrom;
        return nextStates.get(nextMoveIndex);
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
        int row = 0;
        for (int i = 0; i < GameState.BOARD_SIZE; ++i) {
            for (int j = 0; j < GameState.BOARD_SIZE; ++j) {
                if (state.at(i,j) == player) {
                    ++row;
                }
            }
        }
        int col = 0;
        for (int i = 0; i < GameState.BOARD_SIZE; ++i) {
            for (int j = 0; j < GameState.BOARD_SIZE; ++j) {
                if (state.at(j,i) == player) {
                    ++col;
                }
            }
        }
        int dia = 0;
        for (int i = 0; i < GameState.BOARD_SIZE; ++i) {
            if (state.at(i,i) == player) {
                ++dia;
            }
            if (state.at(i, GameState.BOARD_SIZE-i) == player) {
                ++dia;
            }

        }
        return row + col + dia;
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
