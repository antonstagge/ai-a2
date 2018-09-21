import java.util.*;

public class Player {
    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        // Let one player play random for testing.
        /*
        if (gameState.getNextPlayer() == Constants.CELL_O) {
            Random random = new Random();
            return nextStates.elementAt(random.nextInt(nextStates.size()));
        }
        */


        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */
        GameState best =  AlphaBeta.alphaBetaMinMax(gameState, nextStates, deadline);

        //System.err.println(best.toString(gameState.getNextPlayer()));
        return best;
    }

    public static int otherPlayer(int player) {
        return player == Constants.CELL_X ? Constants.CELL_O : Constants.CELL_X;
    }
}
