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


public final class BFSGhosts extends Controller<EnumMap<GHOST,MOVE>>
{		
	EnumMap<GHOST,MOVE> myMoves=new EnumMap<GHOST,MOVE>(GHOST.class);
	
	public EnumMap<GHOST,MOVE> getMove(Game game,long timeDue)
	{
		myMoves.clear();
		for(GHOST ghost : GHOST.values())	//for each ghost
		{			
			if(game.doesGhostRequireAction(ghost))		//if ghost requires an action
			{
				MOVE myMove = MOVE.NEUTRAL;
				
				TreeSearch graph = new TreeSearch();
				graph.createGraph(game.getCurrentMaze().graph);
				
				int[] bestPath = graph.computeGhostPathBFS(game.getGhostCurrentNodeIndex(ghost), game, 200, ghost);
				if(bestPath.length >0){
					myMove = game.getMoveToMakeToReachDirectNeighbour(game.getGhostCurrentNodeIndex(ghost), bestPath[1]);
				}
				myMoves.put(ghost, myMove);
			}
		}
		return myMoves;
	}
}

