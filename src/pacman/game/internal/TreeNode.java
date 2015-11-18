package pacman.game.internal;

import java.util.EnumMap;

import pacman.Executor;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

	//Class adapted from AStar.java
public class TreeNode implements Comparable<TreeNode>
	{
		public Game gameState;
		public int index;
		public EnumMap<MOVE, TreeNode> neighbors = new EnumMap<MOVE, TreeNode>(MOVE.class);
		public double g = 0;
		public double h = 0;
		public TreeNode parent = null;
		public MOVE reachedBy = null;

	    public TreeNode(int index)
	    {
	        this.index=index;
	    }
	    public TreeNode(Game game){
	    	this.gameState = game;
	    	this.index = game.getPacmanCurrentNodeIndex();
	    	this.reachedBy = game.getPacmanLastMoveMade();    	
	    }
	    public TreeNode evolve(MOVE move){
	    	Game game = this.gameState.copy();
	    	game.advanceGame(move, Executor.ghostAI.getMove());
	    	TreeNode next = new TreeNode(game);
	    	return next;
	    }
	    
	    public double f() {
	    	return g + h;
	    }
	    
	    public boolean isEqual(TreeNode other)
	    {
	        return index == other.index;
	    }
	    
	    public int compareTo(TreeNode other)
	    {
			if (f() < other.f()) {
				return -1;
			}
			else if (f() > other.f()) {
				return 1;
			}
			return 0;
	    }
	}