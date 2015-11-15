package pacman.entries.pacman;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.StateEvaluator;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class PacmanSimulatedAnnealing extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;

	public MOVE getMove(Game game, long timeDue)
	{
		int currBestEval = Integer.MIN_VALUE;

		MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());

		for(MOVE move : possibleMoves){

			Game gameCopy = game.copy();
			gameCopy.advanceGame(move, Executor.ghostAI.getMove());

			int stateScore = StateEvaluator.evalGameState(gameCopy);
			double time = gameCopy.getCurrentLevelTime();

			if(stateScore>currBestEval){
				myMove = move;
				currBestEval = stateScore;
			} else if(stateScore != Integer.MIN_VALUE && stateScore != currBestEval && Math.random() <= Math.exp(((stateScore-currBestEval)/10*(game.getScore())))){
				myMove = move;
				currBestEval = stateScore;
			}
		}
		return myMove;
	}
}
