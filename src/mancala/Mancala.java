package mancala;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Ajay
 */
public class Mancala {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        File f = new File("next_state.txt");
        if (!f.exists()) {
            f.createNewFile();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            //Clears out existing file contents
            writer.write("");
            if (args.length == 0) {
                parseInputAndProcess("", writer);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static ProblemState prbState;

    static void parseInputAndProcess(String fileName, BufferedWriter writer) throws Exception {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("F:\\Netbeans\\Projects\\AI Homeworks\\HW2\\Mancala\\TestFiles\\input.txt"));
            int task = Integer.parseInt(reader.readLine());
            int player = Integer.parseInt(reader.readLine());
            int cutOffDepth = Integer.parseInt(reader.readLine());
            String player2StateRaw = reader.readLine().trim();
            String[] temp = player2StateRaw.split(" ");
            int[] player2State = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
                player2State[i] = Integer.parseInt(temp[i]);
            }
            String player1StateRaw = reader.readLine().trim();
            temp = player1StateRaw.split(" ");
            int[] player1State = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
                player1State[i] = Integer.parseInt(temp[i]);
            }
            int numOfStonesInPlayer2Mancala = Integer.parseInt(reader.readLine());
            int numOfStonesInPlayer1Mancala = Integer.parseInt(reader.readLine());

            prbState = new ProblemState(task, player, cutOffDepth, null);
            prbState.state = new MancalaState(player1State, player2State, numOfStonesInPlayer1Mancala, numOfStonesInPlayer2Mancala, player);

            playMancala(prbState, writer);
        } catch (IOException | NumberFormatException e) {
            throw e;
        }
    }

    static void writeStdOut(String data){
        System.out.println(data);
    }
    
    private static void playMancala(ProblemState prb, BufferedWriter writer) throws Exception {
        try {
            MancalaState mancalaState = null;
            switch (prb.taskType) {
                case 1:
                    mancalaState = Greedy.processGreedy(prbState.state);
                    break;
                case 2:
                    mancalaState = MiniMax.processMinimax(prbState.state);
                    break;
                case 3:
                default:
                    mancalaState = AlphaBeta.processAlphaBeta(prbState.state);
                    break;
                case 4:
                    break;
            }
            if (mancalaState != null) {
                writer.append(mancalaState.toString());
            }
        } catch (Exception e) {
            throw e;
        }
    }
}

class ProblemState {

    int taskType;
    int player;
    int cutOffDepth;
    MancalaState state;

    public ProblemState(int taskType, int player, int cutOffDepth, MancalaState state) {
        this.taskType = taskType;
        this.player = player;
        this.cutOffDepth = cutOffDepth;
        this.state = state;
    }
}
