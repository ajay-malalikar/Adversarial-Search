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
public class AlphaBeta {
    static BufferedWriter writer;

    private static void init() throws IOException {
        try {
            File f = new File("traverse_log.txt");
            if (!f.exists()) {
                f.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(f));
            writer.write("Node,Depth,Value,Alpha,Beta");
            writer.append(System.getProperty("line.separator"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static MancalaState processAlphaBeta(MancalaState state) throws IOException
    {
        if (writer == null) {
            init();
        }
        state.updateNodedata();
        writer.append(state.nodeData);
        Mancala.writeStdOut(state.nodeData);

        MancalaState temp = Min_Value(state);
        
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
    
    private static MancalaState Max_Value(MancalaState state) throws IOException
    {
        if (state.hasToPlayAgain) {
            for (Action a : state.GetActions()) {
                MancalaState temp = state.ExcuteAction(a, state.depth);
                
                Mancala.writeStdOut(temp.nodeData);
                writer.append(temp.nodeData);
                
                MancalaState t = Max_Value(temp);
                printEvaluatedValForGameOver(t, temp.nodeData);
                
                // Player at max level having another chance. [Min node]
                if(!state.playerSwitched && Mancala.prbState.player != state.player && !state.isLeafNode){
                    state.eval = min(state.eval, t.eval);
                    if(state.alpha >= min(state.beta, t.eval)){
                        state.updateNodedata();
                        Mancala.writeStdOut(state.nodeData);
                        writer.append(state.nodeData);
                        state.beta = min(state.beta, t.eval);
                    }
                    else if(state.alpha < state.beta){
                        state.beta = min(state.beta, t.eval);
                        state.updateNodedata();
                        Mancala.writeStdOut(state.nodeData);
                        writer.append(state.nodeData);
                    }
                }
                
                // Player at min level [Max node]
                else if(state.playerSwitched && !state.isLeafNode){
                    state.eval = min(state.eval, temp.eval);
                    if(state.alpha >= min(state.eval, t.eval)){
                        state.updateNodedata(state.depth-1);
                        Mancala.writeStdOut(state.nodeData);
                        writer.append(state.nodeData);
                        state.beta = min(state.beta, temp.eval);
                    }
                    else if(state.alpha < state.beta){
                        state.beta = min(state.beta, temp.eval);
                        state.updateNodedata(state.depth-1);
                        Mancala.writeStdOut(state.nodeData);
                        writer.append(state.nodeData);
                    }
                }
                
                if(state.alpha >= state.beta)
                    return state;
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

            MancalaState temp = Min_Value(t);
            
            state.eval = temp.eval;
            state.beta = temp.beta;
            state.alpha = temp.alpha;
            
            return state;
        }
        return state;
    }
    
    private static MancalaState Min_Value(MancalaState state) throws IOException
    {
        if (state.hasToPlayAgain) {
            for (Action a : state.GetActions()) {
                MancalaState temp = state.ExcuteAction(a, state.depth == 0 ? 1 : state.depth);

                Mancala.writeStdOut(temp.nodeData);
                writer.append(temp.nodeData);
                
                MancalaState t = Min_Value(temp);
                printEvaluatedValForGameOver(t, temp.nodeData);
                
                // Player at min level having another chance. [Max node]
                if(!state.playerSwitched && Mancala.prbState.player == state.player && !state.isLeafNode){
                    //state.eval = max(state.eval, t.eval);
                    if(t.eval > state.eval){
                        state.eval = t.eval;
                        state.child = t;
                        state.updateNodedata();
                    }
                    if(state.beta <= max(state.alpha, t.eval)){
                        state.updateNodedata();
                        Mancala.writeStdOut(state.nodeData);
                        writer.append(state.nodeData);
                        max(state, t);
                    }
                    else if(state.alpha < state.beta){
                        //state.alpha = max(state.alpha, t.eval);
                        max(state, t);
                        state.updateNodedata();
                        Mancala.writeStdOut(state.nodeData);
                        writer.append(state.nodeData);
                    }
                }
                
                // Player at max level [Min node]
                else if(state.playerSwitched && !state.isLeafNode){
                    state.eval = max(state.eval, temp.eval);
                    if(state.beta <= max(state.alpha, t.eval)){
                        state.updateNodedata(state.depth-1);
                        Mancala.writeStdOut(state.nodeData);
                        writer.append(state.nodeData);
                        state.alpha = max(state.alpha, temp.eval);
                    }
                    else if(state.alpha < state.beta){
                        state.alpha = max(state.alpha, temp.eval);
                        state.updateNodedata(state.depth-1);
                        Mancala.writeStdOut(state.nodeData);
                        writer.append(state.nodeData);
                    }
                }
                
                if(state.beta <= state.alpha)
                    return state;

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

            MancalaState t1 = Max_Value(t);
            
            state.eval = t1.eval;
            state.beta = t1.beta;
            state.alpha = t1.alpha;
            
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
        if(s2.eval > s1.alpha){
            s1.alpha = s2.eval;
            s1.child = s2;
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
            if(!prevData.equals(state.nodeData)){
                Mancala.writeStdOut(state.nodeData);
                writer.append(state.nodeData);
            }
        }
    }
}