import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeoutException;
import java.util.Collections;
import java.util.Comparator;
import java.util.*;


/**
 * Created by Anton Stagge and Cristian Osorio Bretti on 2018-09-20.
 */
public class AlphaBeta {
    private static Deadline dl;
    private static HashMap map;

    public static GameState alphaBetaMinMax(GameState state, Vector<GameState> nextStates, Deadline deadline, int maxDepth) throws TimeoutException {
        int init_alpha = Integer.MIN_VALUE;
        int init_beta = Integer.MAX_VALUE;
        dl = deadline;

        GameState best = null;

        if (isRed(state.getNextPlayer())) {
            Collections.sort(nextStates, new GameStateCompare());
            int max = Integer.MIN_VALUE;
            int max_idx = -1;
            for (int child = nextStates.size()-1; child >= 0; --child) {
                int current = alphabeta(nextStates.get(child), maxDepth, init_alpha, init_beta);
                if (current == Integer.MAX_VALUE) return nextStates.get(child);
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
        } else if (isWhite(state.getNextPlayer())){
            Collections.sort(nextStates, new GameStateCompare());
            int min = Integer.MAX_VALUE;
            int min_idx = -1;
            for (int child = 0; child < nextStates.size(); ++child) {
                int current = alphabeta(nextStates.get(child), maxDepth, init_alpha, init_beta);
                if (current == Integer.MIN_VALUE) return nextStates.get(child);
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

    private static int alphabeta(GameState state, int depth, int alpha, int beta) throws TimeoutException {

        if (dl.timeUntil()/1000000 < 2) throw new TimeoutException("about to run out");

        Vector<GameState> nextMoves = new Vector<>();
        state.findPossibleMoves(nextMoves);

        // order on heuristic
        //Collections.sort(nextMoves, new GameStateCompare());

        int player = state.getNextPlayer();

        int v = -1;
        if (nextMoves.size() == 0 || depth == 0) {
            if (state.isRedWin()) v = Integer.MAX_VALUE;
            else if (state.isWhiteWin()) v = Integer.MIN_VALUE;
            else if (state.isDraw()) v = 0;
            else {
                v = heuristic(state);
            }
        } else if (isRed(player)) {
            Collections.sort(nextMoves, new GameStateCompare());
            v = Integer.MIN_VALUE;
            for (int child = nextMoves.size()-1; child >= 0; --child) {

                v = Math.max(v, alphabeta(nextMoves.get(child), depth-1, alpha, beta));
                if (v == Integer.MAX_VALUE) return v;

                alpha = Math.max(v, alpha);

                if (beta <= alpha) {
                    break;
                }
            }
            return v;
        } else if (isWhite(player)){
            // white player
            Collections.sort(nextMoves, new GameStateCompare());
            v = Integer.MAX_VALUE;
            for (int child = 0; child < nextMoves.size(); ++child) {

                v = Math.min(v, alphabeta(nextMoves.get(child), depth-1, alpha, beta));
                if (v == Integer.MIN_VALUE) return v;
                beta = Math.min(v, beta);

                if (beta <= alpha) {
                    break;
                }
            }
            return v;
        }
        return v;
    }

    public static int heuristic(GameState state) {
        int value = 0;

        int red_points = 0;
        int white_points = 0;
        boolean onlyKingsLeft = true;
        // Count pieces Kings are worth 5 and regular pieces 3.
        for (int i = 0; i < state.NUMBER_OF_SQUARES; i++) {
            int piece = state.get(i);
            if (isRedPiece(piece)) {
                red_points += 2;
                onlyKingsLeft = false;
            } else if (isRedKing(piece)) {
                red_points += 10;
            } else if (isWhitePiece(piece)) {
                white_points += 2;
                onlyKingsLeft = false;
            } else if (isWhiteKing(piece)) {
                white_points += 10;
            }
        }

        value += (int) Math.pow(10,5)*red_points - (int) Math.pow(10,5)*white_points;


        //Only kings left
        if(onlyKingsLeft) {
            int numberOfRedKings = (int) red_points/10;
            int numberOfWhiteKings = (int) white_points/10;
            RowCol[] positionsOfRedKings = new RowCol[numberOfRedKings];
            int redIndex = 0;
            RowCol[] positionsOfWhiteKings = new RowCol[numberOfWhiteKings];
            int whiteIndex = 0;
            for (int row = 0; row < 8; row++) {
                for(int column = 0; column < 8; column++) {
                    int piece = state.get(row, column);
                    if((isRedKing(piece))) {
                        RowCol thisPosition = new RowCol(row, column);
                        positionsOfRedKings[redIndex] = thisPosition;
                        redIndex ++;
                    }
                    if((isWhiteKing(piece))) {
                        RowCol thisPosition = new RowCol(row, column);
                        positionsOfWhiteKings[whiteIndex] = thisPosition;
                        whiteIndex ++;
                    }
                }
                
            }

            int distanceBetweenKings = calculateDistanceBetweenAll(positionsOfRedKings, positionsOfWhiteKings);
            if(numberOfRedKings > numberOfWhiteKings){
                value -= Math.pow(10, 4)*distanceBetweenKings; 
            } else if (numberOfRedKings < numberOfWhiteKings) {
                value += Math.pow(10, 4)*distanceBetweenKings;
            }
        }

        red_points = 0;
        int red_count = 0;
        white_points = 0;
        int white_count = 0;
        for (int i = 0; i < state.NUMBER_OF_SQUARES; ++i) {
            int piece = state.get(i);
            int row = (int) i/4;
            //System.err.println("piece: " + piece + " at row: " + row  + " index: " + i);
            if (isRedPiece(piece)) {
                red_points += row;
                red_count++;
            } else if (isWhitePiece(piece)) {
                white_points += (8-1) - row;
                white_count++;
            }
        }
        if(red_count > 0) red_points = (int) red_points/red_count;

        if(white_count > 0) white_points = (int) white_points/white_count;

        value += (int) Math.pow(10,4)*red_points - (int) Math.pow(10,4)*white_points;

        //Corners
        red_points = 0;
        white_points = 0;
        for (int i = 0; i < state.NUMBER_OF_SQUARES; i++) {
            int piece = state.get(i);
            if((isRedPiece(piece) || isRedKing(piece)) && isCorner(piece)) red_points++;
            if((isWhitePiece(piece) || isWhiteKing(piece)) && isCorner(piece)) white_points++;
        }

        value += (int) Math.pow(10, 4)*red_points - (int) Math.pow(10, 4)*white_points;


        //Edges
        red_points = 0;
        white_points = 0;
        for (int i = 0; i < state.NUMBER_OF_SQUARES; i++) {
            int piece = state.get(i);
            if((isRedPiece(piece) || isRedKing(piece)) && isEdge(piece)) red_points++;
            if((isWhitePiece(piece) || isWhiteKing(piece)) && isEdge(piece)) white_points++;
        }

        value += (int) Math.pow(10, 4)*red_points - (int) Math.pow(10, 4)*white_points;

        
        
        return value;
    }

    public static boolean isRedPiece(int val) {
        return  val == Constants.CELL_RED;
    }

    public static boolean isRedKing(int val) {
        return  val == (Constants.CELL_RED | Constants.CELL_KING);
    }

    public static boolean isRed(int val) {
        return isRedPiece(val) || isRedKing(val);
    }

    public static boolean isWhitePiece(int val) {
        return  val == Constants.CELL_WHITE;
    }

    public static boolean isWhiteKing(int val) {
        return  val == (Constants.CELL_WHITE | Constants.CELL_KING);
    }

    public static boolean isWhite(int val) {
        return isWhitePiece(val) || isWhiteKing(val);
    }

    public static boolean isCorner(int val) {
        return val == 3 || val == 28 ;
    }

    public static boolean isEdge(int val) {
        return val == 0 || val == 1 || val == 2 || val == 4 || val == 12 || val == 20 || val == 11 || val == 19 || val == 27 || val == 29 || val == 30 || val == 31;
    }

    public static int calculateDistanceBetweenAll(RowCol[] redPositions, RowCol[] whitePositions){
        int sumOfDistances = 0;
        for(int i = 0; i < redPositions.length; i++){
            RowCol currentRedPosition = redPositions[i];
            for(int j = 0; j < whitePositions.length; j++){
                RowCol currentWhitePosition = whitePositions[j];
                sumOfDistances += calculateDistance(currentRedPosition, currentWhitePosition);
            }
        }
        return sumOfDistances;
    }

    public static int calculateDistance(RowCol position, RowCol otherPosition) {
        return Math.abs(position.row - otherPosition.row) + Math.abs(position.column - otherPosition.column);
    }

    public static class GameStateCompare implements Comparator<GameState> {

        @Override
        public int compare(GameState o1, GameState o2) {
            // write comparison logic here like below , it's just a sample
            return ((Integer)heuristic(o1)).compareTo((Integer) heuristic(o2));
        }
    }

    public static class RowCol {
        public int row;
        public int column;

        public RowCol(int r, int c){
            row = r;
            column = c;
        }
    }
}
