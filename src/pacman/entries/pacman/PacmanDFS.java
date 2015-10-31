package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.EnumMap;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.DFS;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class PacmanDFS extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	ArrayList<Integer> path = new ArrayList<Integer>();
	
	public MOVE getMove(Game game, long timeDue) 
	{
		//Place your game logic here to play the game as Ms Pac-Man
		
		if(path.isEmpty()){
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
			
			//return the next direction once the closest target has been identified
			//return game.getNextMoveTowardsTarget(current,game.getClosestNodeIndexFromNodeIndex(current,targetsArray,DM.PATH),DM.PATH);
			
			int locNearestPill = game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), targetsArray, DM.PATH);
			int[] bestPath = graph.computePathsAStar(game.getPacmanCurrentNodeIndex(), locNearestPill, game);
			System.out.println(game.getPacmanCurrentNodeIndex());
			for(int i = 1; i < bestPath.length; i++){
				path.add(bestPath[i]);
				System.out.println(i+ " " + bestPath[i]);
			}
		}
		
		
		myMove = game.getMoveToMakeToReachDirectNeighbour(game.getPacmanCurrentNodeIndex(), path.remove(0));

		return myMove;
	}
}