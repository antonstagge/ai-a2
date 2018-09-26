import java.util.HashMap;
import java.util.Vector;


/**
 * Created by Anton Stagge and Cristian Osorio Bretti on 2018-09-20.
 */
public class AlphaBeta {
    private static int originalPlayer;
    private static Deadline dl;
    private static final int sizeOfBoard = 8;
    private static HashMap map;


    public static GameState alphaBetaMinMax(GameState state, Vector<GameState> nextStates, Deadline deadline) {
        int maxDepth = 9;
        int init_alpha = Integer.MIN_VALUE;
        int init_beta = Integer.MAX_VALUE;
        originalPlayer = state.getNextPlayer();
        map = new HashMap<String, Integer>();
        dl = deadline;

        GameState best = null;

        if (state.getNextPlayer() == Constants.CELL_RED) {
            int max = -Integer.MAX_VALUE;
            int max_idx = -1;
            for (int child = 0; child < nextStates.size(); ++child) {
                int current = alphabeta(nextStates.get(child), maxDepth, init_alpha, init_beta);
                if (current > max) {
                    max = current;
                    max_idx = child;
                }

                init_alpha = Math.max(max, init_alpha);

                if (init_beta <= init_alpha) {
                    break;
                }
            }

            best = nextStates.get(max_idx);
        } else {
            int min = Integer.MAX_VALUE;
            int min_idx = -1;
            for (int child = 0; child < nextStates.size(); ++child) {
                int current = alphabeta(nextStates.get(child), maxDepth, init_alpha, init_beta);
                if (current < min) {
                    min = current;
                    min_idx = child;
                }

                init_beta = Math.min(min, init_beta);

                if (init_beta <= init_alpha) {
                    break;
                }
            }

            best = nextStates.get(min_idx);
        }



        return best;
    }

    private static int alphabeta(GameState state, int depth, int alpha, int beta) {
        Vector<GameState> nextMoves = new Vector<>();
        state.findPossibleMoves(nextMoves);
        int player = state.getNextPlayer();

        if (nextMoves.size() == 0 || depth == 0) {
            //termial state or end of search depth
            if (state.isRedWin()) {
                return Integer.MAX_VALUE;
            } else if (state.isWhiteWin()) {
                return Integer.MIN_VALUE;
            } else {
                int heuristicValue = heuristic(state);
                String key = state.toMessage();
                if(!map.containsKey(key)){
                    map.put(key, heuristicValue);
                }
                return heuristicValue;
            }
        }

        if (player == Constants.CELL_RED) {
            int v = -Integer.MAX_VALUE;
            for (int child = 0; child < nextMoves.size(); ++child) {
                String key = nextMoves.get(child).toMessage();
                if(map.containsKey(key)){
                    v = Math.max(v, (int)map.get(key));
                } else {
                    v = Math.max(v, alphabeta(nextMoves.get(child), depth-1, alpha, beta));
                }

                alpha = Math.max(v, alpha);

                if (beta <= alpha) {
                    break;
                }
            }

            return v;
        }

        if (player == Constants.CELL_WHITE) {
            int v = Integer.MAX_VALUE;
            for (int child = 0; child < nextMoves.size(); ++child) {
                String key = nextMoves.get(child).toMessage();
                if(map.containsKey(key)){
                    v = Math.min(v, (int)map.get(key));
                } else {
                    v = Math.min(v, alphabeta(nextMoves.get(child), depth-1, alpha, beta));
                }
                beta = Math.min(v, beta);

                if (beta <= alpha) {
                    break;
                }
            }

            return v;
        }

        // never going to happen!
        throw new IndexOutOfBoundsException("GOT TO WHERE NO MAN GOT BEFORE!");
    }

    public static int heuristic(GameState state) {
        int returnValue = 0;

        for(int row = 0; row < sizeOfBoard; row++){
            for(int column = 0; column < sizeOfBoard; column++){
                int valueInSquare = state.get(row, column);
                if(valueInSquare == Constants.CELL_RED) returnValue++;
                else if(valueInSquare == Constants.CELL_WHITE) returnValue--;
                else{
                    //Nothing
                }
            }
        }
        return returnValue;
    }

}