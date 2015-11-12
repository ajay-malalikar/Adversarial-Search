/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mancala;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author Ajay
 */
public class MancalaStateOld {

    int[] player1State;
    int[] player2State;
    int numOfStonesInPlayer1Mancala;
    int numOfStonesInPlayer2Mancala;
    int player;
    int numOfPits;
    boolean hasToPlayAgain = false;
    int depth = 0;
    MancalaStateOld parent = null;
    int eval = Integer.MAX_VALUE;
    int typeOfPlay;

    public MancalaStateOld(MancalaStateOld state) {
        this.player1State = state.player1State.clone();
        this.player2State = state.player2State.clone();
        this.numOfStonesInPlayer1Mancala = state.numOfStonesInPlayer1Mancala;
        this.numOfStonesInPlayer2Mancala = state.numOfStonesInPlayer2Mancala;
        this.player = state.player;
        this.numOfPits = state.player1State.length;
    }

    public MancalaStateOld(MancalaState state) {
        this.player1State = state.player1State.clone();
        this.player2State = state.player2State.clone();
        this.numOfStonesInPlayer1Mancala = state.numOfStonesInPlayer1Mancala;
        this.numOfStonesInPlayer2Mancala = state.numOfStonesInPlayer2Mancala;
        this.player = state.player;
        this.numOfPits = state.player1State.length;
        this.hasToPlayAgain = state.hasToPlayAgain;
    }

    public MancalaStateOld(int[] player1State, int[] player2State, int numOfStonesInPlayer1Mancala, int numOfStonesInPlayer2Mancala, int player) {
        this.player1State = player1State;
        this.player2State = player2State.clone();
        this.numOfStonesInPlayer1Mancala = numOfStonesInPlayer1Mancala;
        this.numOfStonesInPlayer2Mancala = numOfStonesInPlayer2Mancala;
        this.player = player;
        this.numOfPits = player1State.length;
        this.hasToPlayAgain = false;
    }

    public int evaluation() {
        if (this.player == 1) {
            return this.numOfStonesInPlayer1Mancala - this.numOfStonesInPlayer2Mancala;
        } else {
            return this.numOfStonesInPlayer2Mancala - this.numOfStonesInPlayer1Mancala;
        }
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

    public MancalaStateOld ExcuteAction(Action action) {
        MancalaStateOld state = new MancalaStateOld(this);
        state.parent = this;
        state.depth = depth;

        try {
            int last = Integer.MAX_VALUE;
            int numOfRevolutionRequired;

            if (state.player == 1) {
                // Updating all the pits and mancalas if stones complete rotations
                state.player1State[action.pit] = 0;

                numOfRevolutionRequired = action.stones / ((state.numOfPits * 2) + 1);
                state.updateStonesInMancala(numOfRevolutionRequired);

                action.stones -= numOfRevolutionRequired * ((state.numOfPits * 2) + 1);

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
                    for (int i = 0; i <= action.pit && action.stones != 0; i++) {
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
                        int oppositePit = last;
                        int temp = state.player2State[oppositePit] + 1;
                        state.numOfStonesInPlayer1Mancala += temp;
                        state.player2State[oppositePit] = 0;
                        state.player1State[last] = 0;
                    }
                }
            } 
            else {
                // Updating all the pits and mancalas if stones complete rotations
                state.player2State[action.pit] = 0;

                numOfRevolutionRequired = action.stones / ((state.numOfPits * 2) + 1);
                state.updateStonesInMancala(numOfRevolutionRequired);

                action.stones -= numOfRevolutionRequired * ((state.numOfPits * 2) + 1);

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
                        int oppositePit = last;
                        int temp = state.player1State[oppositePit] + 1;
                        state.numOfStonesInPlayer2Mancala += temp;
                        state.player1State[oppositePit] = 0;
                        state.player2State[last] = 0;
                    }
                }
            }

            // Special condition, where if my move makes all my pits zero then all the stones in opponents pits will 
            // go to his mancala, thereby reducing the evaluation.
            if (state.numOfStonesInPlayer1Pit() == 0) {
                state.numOfStonesInPlayer2Mancala += state.numOfStonesInPlayer2Pit();
                for (int i = 0; i < state.numOfPits; i++) {
                    state.player2State[i] = 0;
                }
            }
            else if (state.numOfStonesInPlayer2Pit() == 0) {
                state.numOfStonesInPlayer1Mancala += state.numOfStonesInPlayer1Pit();
                for (int i = 0; i < state.numOfPits; i++) {
                    state.player1State[i] = 0;
                }
            }
            
            return state;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void updateStonesInMancala(int number) {
        for (int i = 0; i < player1State.length; i++) {
            player1State[i] += number;
        }
        for (int i = 0; i < player2State.length; i++) {
            player2State[i] += number;
        }
        if (this.player == 1) {
            numOfStonesInPlayer1Mancala += number;
        } else {
            numOfStonesInPlayer2Mancala += number;
        }
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
