# Adversarial-Search
Predict the next move for a player in Mancala game using Adversarial search algorithms like Greedy, Minimax and Alpha-Beta prunning. This is a java project.

The rules of the Mancala game can be found at https://en.wikipedia.org/wiki/Mancala [1] and you can also try playing it online at http://play-mancala.com/.

Evaluation function:
Func(p) = #Stones_player - #Stones_opponent

Input Type: <File Required: input.txt>
<Task#> Greedy=1, MiniMax=2, Alpha-Beta=3, Competition=4
<Your player: 1 or 2>
<Cutting off depth>
<Board state for player-2>
<Board state for player-1>
<#stones in player-2’s mancala>
<#stones in player-1’s mancala>

Output: <File Generated: next_state.txt>
 Line-1 represents the board state for player-2, i.e. the upper side of the board. Each number is separated by a single white space.
 Line-2 represents the board state for player-1, i.e. the upper side of the board. Each number is separated by a single white space.
 Line-3 gives you the number of stones in player-2’s mancala.
 Line-4 gives you the number of stones in player-1’s mancala.

traverse_log.txt contains the travesal for Minimax and Alpha Beta algorithms.


How to Run:
javac mancala.java
java mancala –i inputFile.txt
