package pacman.entries.pacman;

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
			gameCopy.advanceGame(move, new StarterGhosts().getMove());
			
			if(evalGameState(gameCopy)>currBestEval){
				myMove = move;
				currBestEval = evalGameState(gameCopy);
			}
		}
		return myMove;
	}
	
	private int evalGameState(Game game){
		int score = 0;
		
		//being close to ghosts will lead to a lower score. eating a ghost will lead to a higher score
		for(GHOST ghost: GHOST.values()){
			if(game.wasGhostEaten(ghost)){
				score += 1000; //random value. we should play with it
			} else if(game.isGhostEdible(ghost)){
				score += 500;
				score -= game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost), DM.PATH);
			} else {
				score += game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost), DM.PATH);
			}
		}
		
		//being far close to pills will lead to a higher score
		
		//get all active pills
		int[] activePills=game.getActivePillsIndices();

		//get all active power pills
		int[] activePowerPills=game.getActivePowerPillsIndices();

		//create a target array that includes all ACTIVE pills and power pills
		int[] targetNodeIndices=new int[activePills.length+activePowerPills.length];

		for(int i=0;i<activePills.length;i++)
			targetNodeIndices[i]=activePills[i];

		for(int i=0;i<activePowerPills.length;i++)
			targetNodeIndices[activePills.length+i]=activePowerPills[i];
		
		score -= 100*game.getDistance(game.getPacmanCurrentNodeIndex(),game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(),targetNodeIndices,DM.PATH),DM.PATH);
		
		return score;
	}
}
