package pacman.game.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.PriorityQueue;

import pacman.game.Game;
import pacman.Executor;
import pacman.game.Constants.MOVE;


public class TreeSearch {
	
	private TreeNode[] graph;
	
	// Function adapted from AStar.java
	public void createGraph(Node[] nodes)
	{
		graph = new TreeNode[nodes.length];
		//create graph
		for(int i=0;i<nodes.length;i++) {
			graph[i]=new TreeNode(nodes[i].nodeIndex);
		}
		//add neighbors
		for(int i=0;i<nodes.length;i++) {
			EnumMap<MOVE,Integer> edges=nodes[i].neighbourhood;
			MOVE[] moves=MOVE.values();
			for(int j=0;j<moves.length;j++) {
				if(edges.containsKey(moves[j])) {
					TreeNode x = graph[edges.get(moves[j])];
					graph[i].neighbors.put(moves[j], x);
				}
			}
		}
	}
	
	
	// BFS implementation
	private synchronized int[] computePathBFS(int s, int[] targets, MOVE lastMoveMade, Game game)
    {
		ArrayList<TreeNode> open = new ArrayList<TreeNode>();
		TreeNode start = graph[s];
		start.gameState = game.copy();
		start.reachedBy = MOVE.NEUTRAL;
		open.add(start);
		TreeNode current = null;
		while (!open.isEmpty()) {
			current = open.remove(0);
			
			if (current.gameState.getScore() > game.getScore()) {
				break;
			}
			
			for (MOVE move : current.neighbors.keySet()) {
				if(move != current.reachedBy.opposite()) {
					TreeNode child = current.neighbors.get(move);
					child.g = current.g + 1;
					child.parent = current;
					child.reachedBy = move;
					Game gameState = current.gameState.copy();
					gameState.advanceGame(move, Executor.ghostAI.getMove(gameState, -1));
					if (!gameState.wasPacManEaten()) {
						child.gameState = gameState;
						open.add(child);
					}
				}
			}
		}

        return extractPath(current);
    }
	
	
	// DFS implementation
	private synchronized int[] computePathDFS(int s, int[] targets, MOVE lastMoveMade, Game game, int depth)
    {
		ArrayList<TreeNode> open = new ArrayList<TreeNode>();
		TreeNode start = graph[s];
		start.gameState = game.copy();
		start.reachedBy = MOVE.NEUTRAL;
		open.add(start);
		TreeNode current = null;
		Boolean foundSolution = false;
		while (!open.isEmpty()) {
			current = open.remove(open.size() - 1);
			
			if (current.gameState.getScore() > game.getScore()) {
				foundSolution = true;
				break;
			}
			
			for (MOVE move : current.neighbors.keySet()) {
				if(move != current.reachedBy.opposite()) {
					TreeNode child = current.neighbors.get(move);
					child.g = current.g + 1;
					if(child.g < depth){
						child.parent = current;
						child.reachedBy = move;
						Game gameState = current.gameState.copy();
						gameState.advanceGame(move, Executor.ghostAI.getMove(gameState, -1));
						child.gameState = gameState;
						open.add(child);
					}
				}
			}
		}

        if(foundSolution) {
        	return extractPath(current);
        }
        else {
        	return new int[0];
        }
    }

	
	// BFS function call
	public synchronized int[] computePathBFS(int s, int[] targets, Game game)
    {
		return computePathBFS(s, targets, MOVE.NEUTRAL, game);
    }
	
	
	// DFS function call
	public synchronized int[] computePathDFS(int s, int[] targets, Game game)
    {
		return computePathDFS(s, targets, MOVE.NEUTRAL, game, Integer.MAX_VALUE);
    }
	
	
	// Depth-limited DFS function call
	public synchronized int[] computePathDFS(int s, int[] targets, Game game, int depth)
    {
		return computePathDFS(s, targets, MOVE.NEUTRAL, game, depth);
    }
	
	
	// Function adapted from AStar.java
	private synchronized int[] extractPath(TreeNode target)
    {
		ArrayList<Integer> route = new ArrayList<Integer>();
		TreeNode current = target;
		route.add(current.index);
		while (current.parent != null) {
			current = current.parent;
			route.add(current.index);
		}
		Collections.reverse(route);
		int[] routeArray=new int[route.size()];
        for(int i=0;i<routeArray.length;i++) {
        	routeArray[i]=route.get(i);
    	}
        return routeArray;
    }
	
}


class TreeNode
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
}
