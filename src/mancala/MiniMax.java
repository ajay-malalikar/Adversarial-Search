package mancala;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static mancala.Mancala.prbState;

/**
 *
 * @author Ajay
 */
public class MiniMax {

    static BufferedWriter writer;

    private static void init() throws IOException {
        try {
            File f = new File("traverse_log.txt");
            if (!f.exists()) {
                f.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(f));
            writer.write("Node,Depth,Value");
            writer.append(System.getProperty("line.separator"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static MancalaState processMinimax(MancalaState state) throws IOException {
        if (writer == null) {
            init();
        }
        state.updateNodedata();
        writer.append(state.nodeData);
        
        MancalaState temp = MIN_VALUE(state);
        MancalaState optimalState = null;
        boolean found = false;
        
        while(!found){
            optimalState = temp.child;
            if(optimalState != null && optimalState.child != null){
                temp = optimalState;
                optimalState = optimalState.child;
            }
            else found = true;
        }
        writer.close();
        return optimalState == null ? state : optimalState;
    }

    private static MancalaState MAX_VALUE(MancalaState state) throws IOException {
        if (state.hasToPlayAgain) {
            for (Action a : state.GetActions()) {
        
                MancalaState temp = state.ExcuteAction(a, state.depth);
                
                Mancala.writeStdOut(temp.nodeData);
                writer.append(temp.nodeData);
                
                MancalaState t = MAX_VALUE(temp);
                printEvaluatedValForGameOver(t, temp.nodeData);
                
                state.eval = min(state.eval, t.eval);
                if (state.playerSwitched) {
                    state.updateNodedata(state.depth - 1);
                } else {
                    state.updateNodedata();
                }
                
                Mancala.writeStdOut(state.nodeData);
                writer.append(state.nodeData);
            }
            return state;
        }
        if (state.depth == prbState.cutOffDepth) {
            return state;
        }
        if (!state.hasToPlayAgain) {
            MancalaState t = new MancalaState(state);
            t.depth = state.depth + 1;

            if (state.player == 1) {
                t.player = 2;
            } else {
                t.player = 1;
            }
            t.playerSwitched = true;
            t.hasToPlayAgain = true;

            MancalaState temp = MIN_VALUE(t);
            state.eval = max(state.eval, temp.eval);
            return state;
        }
        return state;
    }

    private static MancalaState MIN_VALUE(MancalaState state) throws IOException {
        if (state.hasToPlayAgain) {
            for (Action a : state.GetActions()) {
                
                MancalaState temp = state.ExcuteAction(a, state.depth == 0 ? 1 : state.depth);
                
                Mancala.writeStdOut(temp.nodeData);
                writer.append(temp.nodeData);
                MancalaState t = MIN_VALUE(temp);
                printEvaluatedValForGameOver(t, temp.nodeData);

                max(state, t);
                
                if (state.playerSwitched) {
                    state.updateNodedata(state.depth - 1);
                } else {
                    state.updateNodedata();
                }
                
                Mancala.writeStdOut(state.nodeData);
                writer.append(state.nodeData);
            }
            return state;
        }
        
        if (state.depth == prbState.cutOffDepth) {
            return state;
        }
        
        if (!state.hasToPlayAgain) {
            MancalaState t = new MancalaState(state);
            t.depth = state.depth + 1;

            if (state.player == 1) {
                t.player = 2;
            } else {
                t.player = 1;
            }
            t.playerSwitched = true;
            t.hasToPlayAgain = true;

            MancalaState temp = MAX_VALUE(t);
            state.eval = min(state.eval, temp.eval);
            return state;
        }
        return state;
    }

    private static int min(int s1, int s2) {
        return s1 < s2 ? s1 : s2;
    }

    private static int max(int s1, int s2) {
        if (s1 == Integer.MAX_VALUE) {
            return s2;
        } else if (s2 == Integer.MAX_VALUE) {
            return s1;
        } else {
            return s1 > s2 ? s1 : s2;
        }
    }
    
    private static void max(MancalaState s1, MancalaState s2) {
        if (s1.eval == Integer.MAX_VALUE) {
            s1.eval = s2.eval;
            s1.child = s2;
        }  else {
            if(s2.eval > s1.eval)
            {
                s1.eval = s2.eval;
                s1.child = s2;
            }
        }
    }
    
    private static void printEvaluatedValForGameOver(MancalaState state, String prevData) throws IOException
    {
        if(state.isGameOver){
            state.calculateEval();
            if (state.playerSwitched) {
                state.updateNodedata(state.depth - 1);
            } else {
                state.updateNodedata();
            }
            if(!prevData.endsWith(state.nodeData)){
                Mancala.writeStdOut(state.nodeData);
                writer.append(state.nodeData);
            }
        }
    }
}