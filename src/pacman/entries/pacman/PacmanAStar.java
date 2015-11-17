package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.EnumMap;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.TreeSearch;
import pacman.game.Game;

public class PacmanAStar extends Controller<MOVE>
{
	final int MAX_DEPTH = 100;
	private MOVE myMove=MOVE.NEUTRAL;
	ArrayList<Integer> path = new ArrayList<Integer>();

	public MOVE getMove(Game game, long timeDue)
	{
		if(game.wasPacManEaten()){
			path = new ArrayList<Integer>();
		}
		if(path.isEmpty()){
			TreeSearch graph = new TreeSearch();
			graph.createGraph(game.getCurrentMaze().graph);

			int[] bestPath = graph.computePathAStar(game.getPacmanCurrentNodeIndex(), game, MAX_DEPTH);
			
			if (bestPath.length > 0) {
				for(int i = 1; i < bestPath.length; i++){
					path.add(bestPath[i]);
				}
			}
		}
		
		if (path.size() > 0) {
			myMove = game.getMoveToMakeToReachDirectNeighbour(game.getPacmanCurrentNodeIndex(), path.remove(0));
		}
		else {
			myMove = MOVE.NEUTRAL;
		}
		return myMove;
	}
}
