/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mancala;

import java.util.LinkedList;

/**
 *
 * @author Ajay
 */
public class Greedy {

    public static MancalaState processGreedy(MancalaState currentState) {
        return findGreedySolution(currentState);
    }

    private static MancalaState findGreedySolution(MancalaState state) {
        LinkedList<MancalaState> list = new LinkedList<>();
        LinkedList<MancalaState> leafNodes = new LinkedList<>();
        MancalaState optimalState = null;
        MancalaState temp;
        list.add(state);
        do {
            MancalaState top = list.remove();
            if (top.hasToPlayAgain) {
                for (Action action : top.GetActions()) {
                    temp = top.ExcuteAction(action, 0);
                    list.add(temp);
                    if (!temp.hasToPlayAgain || temp.isGameOver) {
                        leafNodes.add(temp);
                    }
                }
            }
        } while (!list.isEmpty());
        
        for (MancalaState leafNode : leafNodes) {
            if(optimalState == null)
                optimalState = leafNode;
            if (leafNode.evaluation() > optimalState.evaluation()) {
                optimalState = leafNode;
            }
        }
        return optimalState == null ? state : optimalState;
    }
}
