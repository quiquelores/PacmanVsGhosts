package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.EnumMap;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.BFS;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class PacmanBFS extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;

	public MOVE getMove(Game game, long timeDue)
	{
		//Place your game logic here to play the game as Ms Pac-Man

		BFS graph = new BFS();
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


		int locNearestPill = game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), targetsArray, DM.PATH);
		int[] bestPath = graph.computePathBFS(game.getPacmanCurrentNodeIndex(), locNearestPill, game);

		myMove = game.getMoveToMakeToReachDirectNeighbour(game.getPacmanCurrentNodeIndex(), bestPath[1]);
		return myMove;
	}
}
