package pacman.game.internal;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Dictionary;
import java.util.Collections;
import java.util.EnumMap;
import java.util.PriorityQueue;

import pacman.game.Game;
import pacman.Executor;
import pacman.game.Constants.DM;
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
	public synchronized int[] computePathBFS(int s, Game game, int depth)
    {
		ArrayList<TreeNode> open = new ArrayList<TreeNode>();
		ArrayList<TreeNode> closed = new ArrayList<TreeNode>();
		TreeNode start = graph[s];
		start.gameState = game.copy();
		start.reachedBy = MOVE.NEUTRAL;
		open.add(start);
		TreeNode current = null;
		while (!open.isEmpty()) {
			
			current = open.remove(0);
			closed.add(current);
			
			if (current.gameState.getScore() > game.getScore()) {
				break;
			}
			
			for (MOVE move : current.neighbors.keySet()) {
				if(move != current.reachedBy.opposite()) {
					TreeNode child = current.neighbors.get(move);
					if (closed.contains(child)) {
						continue;
					}
					child.g = current.g + 1;
					if(child.g < depth){
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
		}

        return TreeNode.extractPath(current);
    }
	
	
	// DFS implementation
	public synchronized int[] computePathDFS(int s, Game game, int depth)
    {
		ArrayList<TreeNode> open = new ArrayList<TreeNode>();
		ArrayList<TreeNode> closed = new ArrayList<TreeNode>();
		TreeNode start = graph[s];
		start.gameState = game.copy();
		start.reachedBy = MOVE.NEUTRAL;
		open.add(start);
		TreeNode current = null;
		Boolean foundSolution = false;
		while (!open.isEmpty()) {
			
			current = open.remove(open.size() - 1);
			closed.add(current);
			
			if (current.gameState.getScore() > game.getScore()) {
				foundSolution = true;
				break;
			}
			
			for (MOVE move : current.neighbors.keySet()) {
				if(move != current.reachedBy.opposite()) {
					TreeNode child = current.neighbors.get(move);
					if (closed.contains(child)) {
						continue;
					}
					child.g = current.g + 1;
					if(child.g < depth){
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
		}

        if(foundSolution) {
        	return TreeNode.extractPath(current);
        }
        else {
        	return new int[0];
        }
    }
	
	// A* implementation
	public synchronized int[] computePathAStar(int s, Game game, int maxIterations)
    {
		PriorityQueue<TreeNode> open = new PriorityQueue<TreeNode>();
		HashMap<Integer, TreeNode> closed = new HashMap<Integer, TreeNode>();
		TreeNode start = graph[s];
		start.gameState = game.copy();
		start.reachedBy = MOVE.NEUTRAL;
		open.add(start);
		TreeNode current = null;
		int count = 0;
		while (!open.isEmpty()) {

			current = open.poll();
			closed.put(current.index, current);
			
			count += 1;
			if (count > maxIterations) {
				break;
			}
			
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
						int numActivePills = gameState.getNumberOfActivePills();
						if (numActivePills > game.getNumberOfActivePills()) {
							numActivePills = 0;
						}
						child.h = numActivePills;
						if (closed.containsKey(child.index) && closed.get(child.index).f() > child.f()) {
							closed.remove(child.index);
							open.add(child);
						}
						else if (!closed.containsKey(child.index) && !open.contains(child)) {
							open.add(child);
						}
					}
				}
			}
		}
        return TreeNode.extractPath(current);
    }
	
	
	public synchronized HashMap<MOVE, Game> getImmediateNextGameStates(int s, Game game)
	{
		TreeNode start = graph[s];
		HashMap<MOVE, Game> nextStates = new HashMap<MOVE, Game>();
		
		for (MOVE move : start.neighbors.keySet()) {
			Game gameState = game.copy();
			gameState.advanceGame(move, Executor.ghostAI.getMove(gameState, -1));
			nextStates.put(move, gameState);
		}
		
        return nextStates;
	}
	
	
	
}