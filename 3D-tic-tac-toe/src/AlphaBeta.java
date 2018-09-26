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
        int nextMoveIndex = alphabeta(state, maxDepth, init_alpha, init_beta, player, -1).cameFrom;

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
        if (true) {
            return 1;
        }
        int row = 0;
        for (int z = 0; z < GameState.BOARD_SIZE; ++z) {
            for (int y = 0; y < GameState.BOARD_SIZE; ++y) {
                for (int x = 0; x < GameState.BOARD_SIZE; ++x) {
                    if (state.at(y, x, z) == player) {
                        ++row;
                    }
                }
            }
        }
        int col = 0;
        for (int z = 0; z < GameState.BOARD_SIZE; ++z) {
            for (int x = 0; x < GameState.BOARD_SIZE; ++x) {
                for (int y = 0; y < GameState.BOARD_SIZE; ++y) {
                    if (state.at(y, x, z) == player) {
                        ++col;
                    }
                }
            }
        }
        int layer = 0;
        for (int x = 0; x < GameState.BOARD_SIZE; ++x) {
            for (int y = 0; y < GameState.BOARD_SIZE; ++y) {
                for (int z = 0; z < GameState.BOARD_SIZE; ++z) {
                    if (state.at(y, x, z) == player) {
                        ++layer;
                    }
                }
            }
        }
        int diaRegXY = 0;
        for (int z = 0; z < GameState.BOARD_SIZE; ++z) {
            for (int i = 0; i < GameState.BOARD_SIZE; ++i) {
                if (state.at(i, i, z) == player) {
                    ++diaRegXY;
                }
                if (state.at(i, GameState.BOARD_SIZE-i, z) == player) {
                    ++diaRegXY;
                }
            }
        }
        int diaRegYZ = 0;
        for (int x = 0; x < GameState.BOARD_SIZE; ++x) {
            for (int i = 0; i < GameState.BOARD_SIZE; ++i) {
                if (state.at(x, i, i) == player) {
                    ++diaRegYZ;
                }
                if (state.at(x, i, GameState.BOARD_SIZE-i) == player) {
                    ++diaRegYZ;
                }
            }
        }
        int diaRegXZ = 0;
        for (int y = 0; y < GameState.BOARD_SIZE; ++y) {
            for (int i = 0; i < GameState.BOARD_SIZE; ++i) {
                if (state.at(i, y, i) == player) {
                    ++diaRegYZ;
                }
                if (state.at(i, y, GameState.BOARD_SIZE-i) == player) {
                    ++diaRegYZ;
                }
            }
        }


        return row + col + layer + diaRegXY + diaRegYZ + diaRegXZ;
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
