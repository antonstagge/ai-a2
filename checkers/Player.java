import java.util.*;
import java.util.concurrent.TimeoutException;

public class Player {
    /**
     * Performs a move
     *
     * @param state
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState state, final Deadline deadline) {

        Vector<GameState> nextStates = new Vector<GameState>();
        state.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(state, new Move());
        }

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */
         AlphaBeta.init();
         GameState best = null;
         int d = 1;
         try {
             while (true) {
                 best = AlphaBeta.alphaBetaMinMax(state, nextStates, deadline, d);
                 d++;
             }
         } catch(TimeoutException e) {
             // System.err.println("Got to depth: " + d);
         }

         return best;
    }
}
