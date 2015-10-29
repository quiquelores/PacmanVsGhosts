package pacman.entries.ghosts;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.BFS;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MyGhosts extends Controller<EnumMap<GHOST,MOVE>>
{
	private EnumMap<GHOST, MOVE> myMoves=new EnumMap<GHOST, MOVE>(GHOST.class);
	
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue)
	{
		myMoves.clear();
		
		//Place your game logic here to play the game as the ghosts
		for(GHOST ghost : GHOST.values()){
			if(game.doesGhostRequireAction(ghost)){	//if it requires an action

				BFS graph = new BFS();
				graph.createGraph(game.getCurrentMaze().graph);
			
				int[] bestPath = graph.computePathBFS(game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex(), game);
				myMoves.put(ghost, game.getMoveToMakeToReachDirectNeighbour(game.getGhostCurrentNodeIndex(ghost), bestPath[0]));
			}
		}
		
		return myMoves;
	}
}