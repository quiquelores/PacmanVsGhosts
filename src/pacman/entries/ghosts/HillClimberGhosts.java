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
		ArrayList<EnumMap<GHOST, MOVE>> possibleMoves = new ArrayList<EnumMap<GHOST, MOVE>>();
		myMoves.clear();
		
		for(GHOST ghost : GHOST.values())	//for each ghost
		{	
			EnumMap<GHOST,MOVE> myMovesCopy=myMoves.clone();
			for(MOVE move : game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost))){
				myMovesCopy.put(ghost, move);
			}
			possibleMoves.add(myMovesCopy);
		}
		
		int currWorstEval = Integer.MAX_VALUE;
		int currBestMoves = -1;
		for(int i = 1; i < possibleMoves.size(); i++){
			Game gameCopy = game.copy();
			gameCopy.advanceGame(Executor.pacmanController.getMove(), possibleMoves.get(i));
			if(StateEvaluator.evalGameState(gameCopy) < currWorstEval){
				currBestMoves = i;
				currWorstEval = StateEvaluator.evalGameState(gameCopy);
			}	
		}	
		if(possibleMoves.size()>0 && currBestMoves != -1)
			return possibleMoves.get(currBestMoves);
		
		return myMoves;
	}
}

