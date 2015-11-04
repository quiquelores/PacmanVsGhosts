package pacman.entries.pacman;

import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.internal.DFS;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class PacmanIterativeDeepening extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	ArrayList<Integer> path = new ArrayList<Integer>();

	public MOVE getMove(Game game, long timeDue)
	{
		if(game.wasPacManEaten()){
			path = new ArrayList<Integer>();
		}
		while(path.isEmpty()){
			DFS graph = new DFS();
			graph.createGraph(game.getCurrentMaze().graph);

			int[] pills=game.getPillIndices();
			int[] powerPills=game.getPowerPillIndices();

			ArrayList<Integer> targets=new ArrayList<Integer>();

			for(int i=0;i<pills.length;i++)					//check which pills are available
				if(game.isPillStillAvailable(i))
					targets.add(pills[i]);

			for(int i=0;i<powerPills.length;i++)			//check with power pills are available
				if(game.isPowerPillStillAvailable(i))
					targets.add(powerPills[i]);

			int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array

			for(int i=0;i<targetsArray.length;i++)
				targetsArray[i]=targets.get(i);

			int[] bestPath = new int[0];
			int depth = 1;
			while(bestPath.length <= 1){
				bestPath = graph.computePathDFS(game.getPacmanCurrentNodeIndex(), targetsArray, game, depth++);
			}

			for(int i = 1; i < bestPath.length; i++){
				path.add(bestPath[i]);
				System.out.println(bestPath[i]);
			}
		}
		myMove = game.getMoveToMakeToReachDirectNeighbour(game.getPacmanCurrentNodeIndex(), path.remove(0));
		
		if(myMove == null) {
			path = new ArrayList<Integer>();
			return getMove(game, timeDue);
		}
		return myMove;
	}
}
