package pacman.entries.ghosts;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.Constants.MOVE;
import pacman.game.internal.StateEvaluator;
import pacman.game.internal.TreeSearch;

import static pacman.game.Constants.*;


public final class HillClimberGhosts extends Controller<EnumMap<GHOST,MOVE>>
{		
	EnumMap<GHOST,MOVE> myMoves=new EnumMap<GHOST,MOVE>(GHOST.class);
	
	public EnumMap<GHOST,MOVE> getMove(Game game,long timeDue)
	{
		for(GHOST ghost : GHOST.values())	//for each ghost
		{			
			if(game.doesGhostRequireAction(ghost))		//if ghost requires an action
			{
				int currWorstEval = Integer.MAX_VALUE;
				MOVE myMove = MOVE.NEUTRAL;

				MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
				for(MOVE move : possibleMoves){

					Game gameCopy = game.copy();
					EnumMap<GHOST,MOVE> myMovesCopy=myMoves.clone();
					myMovesCopy.put(ghost, move);
					gameCopy.advanceGame(Executor.pacmanController.getMove(), myMovesCopy);

					if(StateEvaluator.evalGameState(gameCopy)<currWorstEval){
						myMove = move;
						currWorstEval = StateEvaluator.evalGameState(gameCopy);
					}
				}
				myMoves.put(ghost, myMove);
			}
		}
		return myMoves;
	}
}

