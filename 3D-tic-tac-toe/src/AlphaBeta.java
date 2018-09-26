import java.util.Vector;


/**
 * Created by Anton Stagge and Cristian Osorio Bretti on 2018-09-20.
 */
public class AlphaBeta {
    private static int originalPlayer;
    private static Deadline dl;

    public static GameState alphaBetaMinMax(GameState state, Vector<GameState> nextStates, Deadline deadline) {
        int maxDepth = 1;
        int init_alpha = Integer.MIN_VALUE;
        int init_beta = Integer.MAX_VALUE;
        originalPlayer = state.getNextPlayer();
        dl = deadline;

        GameState best = null;

        if (state.getNextPlayer() == Constants.CELL_X) {
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
                   // break;
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
                    //break;
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
            // if (state.isXWin()) {
            //     return Integer.MAX_VALUE;
            // } else if (state.isOWin()) {
            //     return Integer.MIN_VALUE;
            // } else {
                return heuristic(state, Constants.CELL_X) - heuristic(state, Constants.CELL_O);
            //}
        }

        if (player == Constants.CELL_X) {
            int v = -Integer.MAX_VALUE;
            for (int child = 0; child < nextMoves.size(); ++child) {
                v = Math.max(v, alphabeta(nextMoves.get(child), depth-1, alpha, beta));

                alpha = Math.max(v, alpha);

                if (beta <= alpha) {
                    break;
                }
            }

            return v;
        }

        if (player == Constants.CELL_O) {
            int v = Integer.MAX_VALUE;
            for (int child = 0; child < nextMoves.size(); ++child) {
                v = Math.min(v, alphabeta(nextMoves.get(child), depth-1, alpha, beta));

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

    public static int heuristic(GameState state, int player) {
        //System.err.println("Heu for : " + player);
        // For every Row
        int returnValue = 0;

        //If player has center squares, it is good
        int valueForCenterPiece = 0;
        if(state.at(1, 1, 1) == player) returnValue+= valueForCenterPiece;
        if(state.at(1, 1, 2) == player) returnValue+= valueForCenterPiece;
        if(state.at(1, 2, 2) == player) returnValue+= valueForCenterPiece;
        if(state.at(2, 2, 2) == player) returnValue+= valueForCenterPiece;
        if(state.at(2, 1, 1) == player) returnValue+= valueForCenterPiece;
        if(state.at(2, 2, 1) == player) returnValue+= valueForCenterPiece;
        if(state.at(2, 1, 2) == player) returnValue+= valueForCenterPiece;
        if(state.at(1, 2, 1) == player) returnValue+= valueForCenterPiece;


        //For every layer
        for(int layer = 0; layer < GameState.BOARD_SIZE; layer++){
            
            //For every row
            int rowTotal = 0;
            for (int row = 0; row < GameState.BOARD_SIZE; ++row) {
                int rowCount = 0;
                for (int col = 0; col < GameState.BOARD_SIZE; ++col) {
                    int marked = state.at(row,col, layer);
                    if (marked == player) {
                        ++rowCount;
                    } else if (marked == Player.otherPlayer(player)) {
                        rowCount = 0;
                        break;
                    }
                }

                rowTotal += valueForMarks(rowCount);
            }

            // For every Column
            int colTotal = 0;
            for (int col = 0; col < GameState.BOARD_SIZE; ++col) {
                int colCount = 0;
                for (int row = 0; row < GameState.BOARD_SIZE; ++row) {
                    int marked = state.at(row,col, layer);
                    if (marked == player) {
                        ++colCount;
                    } else if (marked == Player.otherPlayer(player)) {
                        colCount = 0;
                        break;
                    }
                }
                colTotal += valueForMarks(colCount);;

            }
            //System.err.println("total col: " + colTotal);

            // For one diagonal where row == column
            int diaCount = 0;
            for (int rowColumn = 0; rowColumn < GameState.BOARD_SIZE; ++rowColumn) {
                if (state.at(rowColumn,rowColumn, layer) == player) {
                    ++diaCount;
                } else if (state.at(rowColumn, rowColumn,layer) == Player.otherPlayer(player)) {
                    diaCount = 0;
                    break;
                }
            }

            int diaOne = valueForMarks(diaCount);
            
            // For the other diagonal where 
            diaCount = 0;
            for (int rowColumn = 0; rowColumn < GameState.BOARD_SIZE; ++rowColumn) {
                if (state.at((GameState.BOARD_SIZE-1)-rowColumn, rowColumn, layer) == player) {
                    ++diaCount;
                } else if (state.at((GameState.BOARD_SIZE-1)-rowColumn, rowColumn, layer) == Player.otherPlayer(player)) {
                    diaCount = 0;
                    break;
                }
            }
            
            int diaTwo = valueForMarks(diaCount);

            returnValue += rowTotal + colTotal + diaOne + diaTwo;

        }

        //Pillars
        int pillarValues = 0;
        for(int row = 0; row < GameState.BOARD_SIZE; row++){
            for(int column = 0; column < GameState.BOARD_SIZE; column++){
                int thisPillarCount = 0;
                for(int layer = 0; layer < GameState.BOARD_SIZE; layer++){
                    int playerAtThisCell = state.at(row, column, layer);
                    if(playerAtThisCell == player) {
                        ++thisPillarCount;
                    } else if(playerAtThisCell == Player.otherPlayer(player)) {
                        thisPillarCount = 0;
                        break;
                    }
                }
                pillarValues += valueForMarks(thisPillarCount);
            }
        }
        returnValue += pillarValues;

        //Diagonals in the other two directions

        //RowLayers diagonal
        for(int column = 0; column < GameState.BOARD_SIZE; column++){

            int diaCount = 0;
            for (int rowLayer = 0; rowLayer < GameState.BOARD_SIZE; ++rowLayer) {
                if (state.at(rowLayer,column, rowLayer) == player) {
                    ++diaCount;
                } else if (state.at(rowLayer, column,rowLayer) == Player.otherPlayer(player)) {
                    diaCount = 0;
                    break;
                }
            }

            returnValue += valueForMarks(diaCount);

            // For the other diagonal where 
            diaCount = 0;
            for (int rowLayer = 0; rowLayer < GameState.BOARD_SIZE; ++rowLayer) {
                if (state.at((GameState.BOARD_SIZE-1)-rowLayer, column, rowLayer) == player) {
                    ++diaCount;
                } else if (state.at((GameState.BOARD_SIZE-1)-rowLayer, column, rowLayer) == Player.otherPlayer(player)) {
                    diaCount = 0;
                    break;
                }
            }

            returnValue += valueForMarks(diaCount);
        }

        //LayCol diagonal
        for(int row = 0; row < GameState.BOARD_SIZE; row++){

            int diaCount = 0;
            for (int layCol = 0; layCol < GameState.BOARD_SIZE; ++layCol) {
                if (state.at(row,layCol, layCol) == player) {
                    ++diaCount;
                } else if (state.at(row, layCol,layCol) == Player.otherPlayer(player)) {
                    diaCount = 0;
                    break;
                }
            }

            returnValue += valueForMarks(diaCount);

            // For the other diagonal where 
            diaCount = 0;
            for (int layCol = 0; layCol < GameState.BOARD_SIZE; ++layCol) {
                if (state.at(row, (GameState.BOARD_SIZE-1)-layCol, layCol) == player) {
                    ++diaCount;
                } else if (state.at(row, (GameState.BOARD_SIZE-1)-layCol, layCol) == Player.otherPlayer(player)) {
                    diaCount = 0;
                    break;
                }
            }

            returnValue += valueForMarks(diaCount);
        }
        
        //First cross diagonal
        int firstCrossValue = 0;
        int firstCrossCount = 0;
        for (int index = 0; index < GameState.BOARD_SIZE; ++index) {
            if (state.at(index, index, index) == player) {
                ++firstCrossCount;
                firstCrossValue = valueForMarks(firstCrossCount);
            } else if (state.at(index, index, index) == Player.otherPlayer(player)) {
                firstCrossValue = 0;
                break;
            }
        }

        returnValue += firstCrossValue;

        //Second cross diagonal
        int secondCrossValue = 0;
        int secondCrossCount = 0;
        for (int index = 0; index < GameState.BOARD_SIZE; ++index) {
            int invers = GameState.BOARD_SIZE-1-index;
            if (state.at(invers, index, index) == player) {
                ++secondCrossCount;
                secondCrossValue = valueForMarks(secondCrossCount);
            } else if (state.at(invers, index, index) == Player.otherPlayer(player)) {
                secondCrossValue = 0;
                break;
            }
        }

        returnValue += secondCrossValue;

        //third cross diagonal
        int thirdCrossValue = 0;
        int thirdCrossCount = 0;
        for (int index = 0; index < GameState.BOARD_SIZE; ++index) {
            int invers = GameState.BOARD_SIZE-1-index;
            if (state.at(index, invers, index) == player) {
                ++thirdCrossCount;
                thirdCrossValue = valueForMarks(thirdCrossCount);
            } else if (state.at(index, invers, index) == Player.otherPlayer(player)) {
                thirdCrossValue = 0;
                break;
            }
        }

        returnValue += thirdCrossValue;

        //fourth cross diagonal
        int fourthCrossValue = 0;
        int fourthCrossCount = 0;
        for (int index = 0; index < GameState.BOARD_SIZE; ++index) {
            int invers = GameState.BOARD_SIZE-1-index;
            if (state.at(index, index, invers) == player) {
                ++fourthCrossCount;
                fourthCrossValue = valueForMarks(fourthCrossCount);
            } else if (state.at(index, index, invers) == Player.otherPlayer(player)) {
                fourthCrossValue = 0;
                break;
            }
        }

        returnValue += fourthCrossValue;

        return returnValue;
        
    }

    private static int valueForMarks(int countsSoFar) {
        if(countsSoFar == 0) return 0;
        return (int)Math.pow(100, countsSoFar);
    }
}