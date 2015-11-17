package pacman.entries.pacman;

import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.internal.TreeSearch;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class PacmanIterativeDeepening extends Controller<MOVE>
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

			int[] bestPath = new int[0];
			int depth = 1;
			while(bestPath.length <= 1){
				bestPath = graph.computePathDFS(game.getPacmanCurrentNodeIndex(), game, depth++);
				if (depth >= MAX_DEPTH) {
					break;
				}
			}

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
