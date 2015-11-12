package mancala;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author Ajay
 */
public class MancalaState {

    String nodeName;
    String nodeData;
    int[] player1State;
    int[] player2State;
    int numOfStonesInPlayer1Mancala;
    int numOfStonesInPlayer2Mancala;
    int player;
    int rootPlayer;
    int numOfPits;
    boolean hasToPlayAgain = false;
    int depth;
    boolean playerSwitched = false;

    // Used to find next state in Minimax and Alpha Beta
    MancalaState child = null;

    int eval = Integer.MAX_VALUE;
    boolean isGameOver = false;
    boolean isLeafNode = false;
    
    // Alpha Beta related properties
    int alpha = Integer.MIN_VALUE;
    int beta = Integer.MAX_VALUE;
    
    public MancalaState(MancalaState state) {
        this.player1State = state.player1State.clone();
        this.player2State = state.player2State.clone();
        this.numOfStonesInPlayer1Mancala = state.numOfStonesInPlayer1Mancala;
        this.numOfStonesInPlayer2Mancala = state.numOfStonesInPlayer2Mancala;
        this.player = state.player;
        this.numOfPits = state.player1State.length;
        this.nodeName = state.nodeName;
        this.rootPlayer = state.rootPlayer;
        this.eval = state.eval;
        this.alpha = state.alpha;
        this.beta = state.beta;
    }

    public MancalaState(MancalaState state, int alpha, int beta) {
        this(state);
        this.alpha = alpha;
        this.beta = beta;
    }

    public MancalaState(int[] player1State, int[] player2State, int numOfStonesInPlayer1Mancala, int numOfStonesInPlayer2Mancala, int player) {
        this.player1State = player1State.clone();
        this.player2State = player2State.clone();
        this.numOfStonesInPlayer1Mancala = numOfStonesInPlayer1Mancala;
        this.numOfStonesInPlayer2Mancala = numOfStonesInPlayer2Mancala;
        this.player = player;
        this.rootPlayer = player;
        this.numOfPits = player1State.length;
        this.hasToPlayAgain = false;
        firstInit();
    }

    public int evaluation() {
        return this.rootPlayer == 1 ? this.numOfStonesInPlayer1Mancala - this.numOfStonesInPlayer2Mancala
                : this.numOfStonesInPlayer2Mancala - this.numOfStonesInPlayer1Mancala;
    }

    public void calculateEval() {
        this.eval = this.evaluation();
    }

    private void firstInit(){
        this.depth = 0;
        this.hasToPlayAgain = true;
        this.nodeName = "root";
        this.eval = Integer.MIN_VALUE;
    }
    
    private int numOfStonesInPlayer1Pit() {
        int retVal = 0;
        for (int i : this.player1State) {
            retVal += i;
        }
        return retVal;
    }

    private int numOfStonesInPlayer2Pit() {
        int retVal = 0;
        for (int i : this.player2State) {
            retVal += i;
        }
        return retVal;
    }

    public LinkedList<Action> GetActions() {
        LinkedList<Action> actions = new LinkedList<>();
        if (this.player == 1) {
            for (int i = 0; i < player1State.length; i++) {
                if (player1State[i] != 0) {
                    actions.add(new Action(player1State[i], i));
                }
            }
        } else {
            for (int i = 0; i < player2State.length; i++) {
                if (player2State[i] != 0) {
                    actions.add(new Action(player2State[i], i));
                }
            }
        }
        return actions;
    }

    public MancalaState ExcuteAction(Action action, int depth) {
        MancalaState state = new MancalaState(this);
        state.depth = depth;

        try {
            int last = Integer.MAX_VALUE;
            
            state.eval = state.depth % 2 == 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            if (state.player == 1) {
                state.nodeName = "B" + (action.pit + 2);
                // Updating all the pits and mancalas if stones complete rotations
                state.player1State[action.pit] = 0;
                
                int numOfRevolutionRequired = action.stones / ((state.numOfPits * 2) + 1);
                state.updateStonesInMancala(numOfRevolutionRequired);
                action.stones -= numOfRevolutionRequired * ((state.numOfPits * 2) + 1);
            
                // After revolution if no stones are left then last stone was put in empt pit. Then empty that one and opposite pit
                if(numOfRevolutionRequired == 1 && action.stones == 0){
                    int temp = state.player2State[action.pit] + 1;
                    state.numOfStonesInPlayer1Mancala += temp;
                    state.player1State[action.pit] = 0;
                    state.player2State[action.pit] = 0;
                }
                
                // Fill pits to the right
                if (action.stones != 0) {
                    for (int i = action.pit + 1; i < state.numOfPits && action.stones != 0; i++) {
                        state.player1State[i] += 1;
                        --action.stones;
                        if (action.stones == 0) {
                            last = i;
                        }
                    }
                }

                // Fill Player's mancala
                if (action.stones != 0) {
                    state.numOfStonesInPlayer1Mancala += 1;
                    --action.stones;
                    if (action.stones == 0) {
                        state.hasToPlayAgain = true;
                    }
                }

                // Fill Opposition's pits
                if (action.stones != 0) {
                    for (int i = state.numOfPits - 1; i >= 0 && action.stones != 0; i--) {
                        state.player2State[i] += 1;
                        --action.stones;
                    }
                }

                // Fill pits to the left
                if (action.stones != 0) {
                    for (int i = 0; i < action.pit && action.stones != 0; i++) {
                        state.player1State[i] += 1;
                        --action.stones;
                        if (action.stones == 0) {
                            last = i;
                        }
                    }
                }

                // Check where the last stone was put
                if (last <= state.numOfPits - 1 && last >= 0) {
                    if (state.player1State[last] == 1) {
                        int temp = state.player2State[last] + 1;
                        state.numOfStonesInPlayer1Mancala += temp;
                        state.player2State[last] = 0;
                        state.player1State[last] = 0;
                    }
                }

                
            } else {
                state.nodeName = "A" + (action.pit + 2);
                // Updating all the pits and mancalas if stones complete rotations
                state.player2State[action.pit] = 0;

                int numOfRevolutionRequired = action.stones / ((state.numOfPits * 2) + 1);
                state.updateStonesInMancala(numOfRevolutionRequired);
                action.stones -= numOfRevolutionRequired * ((state.numOfPits * 2) + 1);
                
                // After revolution if no stones are left then last stone was put in empt pit. Then empty that one and opposite pit
                if(numOfRevolutionRequired == 1 && action.stones == 0){
                    int temp = state.player1State[action.pit] + 1;
                    state.numOfStonesInPlayer2Mancala += temp;
                    state.player1State[action.pit] = 0;
                    state.player2State[action.pit] = 0;
                }
                
                // Fill pits to the left
                if (action.stones != 0) {
                    for (int i = action.pit - 1; i >= 0 && action.stones != 0; i--) {
                        state.player2State[i] += 1;
                        --action.stones;
                        if (action.stones == 0) {
                            last = i;
                        }
                    }
                }

                // Fill Player's mancala
                if (action.stones != 0) {
                    state.numOfStonesInPlayer2Mancala += 1;
                    --action.stones;
                    if (action.stones == 0) {
                        state.hasToPlayAgain = true;
                    }
                }

                // Fill Opposition's pits
                if (action.stones != 0) {
                    for (int i = 0; i < state.numOfPits && action.stones != 0; i++) {
                        state.player1State[i] += 1;
                        --action.stones;
                    }
                }

                // Fill pits to the right
                if (action.stones != 0) {
                    for (int i = state.numOfPits - 1; i >= 0 && action.stones != 0; i--) {
                        state.player2State[i] += 1;
                        --action.stones;
                        if (action.stones == 0) {
                            last = i;
                        }
                    }
                }

                // Check where the last stone was put
                if (last <= state.numOfPits - 1 && last >= 0) {
                    if (state.player2State[last] == 1) {
                        int temp = state.player1State[last] + 1;
                        state.numOfStonesInPlayer2Mancala += temp;
                        state.player1State[last] = 0;
                        state.player2State[last] = 0;
                    }
                }
            }

            // Special condition, where if my move makes all my pits zero then all the stones in opponents pits will 
            // go to his mancala, thereby reducing the evaluation.
            if (state.numOfStonesInPlayer1Pit() == 0) {
                state.numOfStonesInPlayer2Mancala += state.numOfStonesInPlayer2Pit();
                for (int i = 0; i < numOfPits; i++) {
                    state.player2State[i] = 0;
                }
                state.isGameOver = true;
            }
            else if (state.numOfStonesInPlayer2Pit() == 0) {
                state.numOfStonesInPlayer1Mancala += state.numOfStonesInPlayer1Pit();
                for (int i = 0; i < state.numOfPits; i++) {
                    state.player1State[i] = 0;
                }
                state.isGameOver = true;
            }
            
            if (state.depth == Mancala.prbState.cutOffDepth && !state.hasToPlayAgain) {
                state.isLeafNode = true;
                if (state.eval == Integer.MAX_VALUE || state.eval == Integer.MIN_VALUE) {
                    state.eval = state.evaluation();
                }
            } else {
                if (state.hasToPlayAgain) {
                    state.eval = state.eval == Integer.MAX_VALUE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                }
            }

            state.updateNodedata();
            return state;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void updateNodedata(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.nodeName);
        sb.append(",").append(depth);
        sb.append(",").append(this.eval == Integer.MAX_VALUE ? "Infinity" : (this.eval == Integer.MIN_VALUE ? "-Infinity" : this.eval));
        if (Mancala.prbState.taskType == 3) {
            sb.append(",").append(this.alpha == Integer.MIN_VALUE ? "-Infinity" : this.alpha);
            sb.append(",").append(this.beta == Integer.MAX_VALUE ? "Infinity" : this.beta);
        }
        sb.append(System.getProperty("line.separator"));
        for (int i = 0; i < this.player2State.length; i++) {
            sb.append(this.player2State[i]).append(" ");
        }
        sb.append(System.getProperty("line.separator"));
        for (int i = 0; i < this.player1State.length; i++) {
            sb.append(this.player1State[i]).append(" ");
        }
        sb.append(System.getProperty("line.separator"));
        sb.append(this.numOfStonesInPlayer2Mancala);
        sb.append(System.getProperty("line.separator"));
        sb.append(this.numOfStonesInPlayer1Mancala);
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        this.nodeData = sb.toString();
    }

    public void updateNodedata() {
        this.updateNodedata(this.depth);
    }

    private void updateStonesInMancala(int number) {
        for (int i = 0; i < player1State.length; i++) {
            player1State[i] += number;
        }
        for (int i = 0; i < player2State.length; i++) {
            player2State[i] += number;
        }
        if(this.player == 1)
            numOfStonesInPlayer1Mancala += number;
        else numOfStonesInPlayer2Mancala += number;
    }

    @Override
    public String toString() {
        return buildStringData(player2State, player1State, numOfStonesInPlayer2Mancala, numOfStonesInPlayer1Mancala);
    }

    private String buildStringData(int[] player2State, int[] player1State, int player2Mancala, int player1Mancala) {
        StringBuilder sb = new StringBuilder();
        String p2 = Arrays.toString(player2State).replace(',', ' ').replace("[", "").replace("]", "");
        String p1 = Arrays.toString(player1State).replace(',', ' ').replace("[", "").replace("]", "");
        sb.append(p2).append(System.getProperty("line.separator"));
        sb.append(p1).append(System.getProperty("line.separator"));
        sb.append(player2Mancala).append(System.getProperty("line.separator"));
        sb.append(player1Mancala);
        return sb.toString();
    }
}