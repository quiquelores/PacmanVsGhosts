package pacman.entries.pacman;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class PacmanHillclimber extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;

	public MOVE getMove(Game game, long timeDue)
	{
		int currBestEval = Integer.MIN_VALUE;

		MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		for(MOVE move : possibleMoves){

			Game gameCopy = game.copy();
			gameCopy.advanceGame(move, Executor.ghostAI.getMove());

			if(evalGameState(gameCopy)>currBestEval){
				myMove = move;
				currBestEval = evalGameState(gameCopy);
			}
		}
		return myMove;
	}
	
	private int evalGameState(Game game){
		
		if(game.wasPacManEaten()) return Integer.MIN_VALUE;

		int score = game.getScore();

		//having edible ghosts will lead to a higher score. being close to them will lead to a higher score if edible, and to a lower score if they're not edible.
		for(GHOST ghost: GHOST.values()){
			if(game.isGhostEdible(ghost)){
				score += 50;
				score -= game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost), DM.PATH);
			} else {
				score += game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost), DM.PATH);
			}
		}
		return score;
	}
}
