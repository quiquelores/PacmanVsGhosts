package pacman.game.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import pacman.game.Game;
import sun.misc.Queue;
import pacman.game.Constants.MOVE;

/*
 * This class is used to compute the shortest path to the given target using BFS
 */
public class BFS
{
	private N[] graph;

	public void createGraph(Node[] nodes)
	{
		graph=new N[nodes.length];

		//create graph
		for(int i=0;i<nodes.length;i++)
			graph[i]=new N(nodes[i].nodeIndex);

		//add neighbours
		for(int i=0;i<nodes.length;i++)
		{
			EnumMap<MOVE,Integer> neighbours=nodes[i].neighbourhood;
			MOVE[] moves=MOVE.values();

			for(int j=0;j<moves.length;j++)
				if(neighbours.containsKey(moves[j]))
					graph[i].adj.add(new E(graph[neighbours.get(moves[j])],moves[j],1));
		}
	}


	public synchronized int[] computePathBFS(int s, int[] targets, MOVE lastMoveMade, Game game)
    {
		N start=graph[s];
		N currentNode = null;
		HashSet<N> targetsN = new HashSet<N>();
		for(int i = 0; i < targets.length; i++){
			targetsN.add(graph[targets[i]]);
		}

        ArrayList<N> open = new ArrayList<N>();
        ArrayList<N> closed = new ArrayList<N>();

        start.g = 0;
        
        start.reached=lastMoveMade;

        open.add(start);

        while(!open.isEmpty())
        {
        	currentNode = open.remove(0);
            closed.add(currentNode);

            if (targetsN.contains(currentNode))
                break;

            for(E next : currentNode.adj)
            {
            	if(next.move!=currentNode.reached.opposite()&& !open.contains(next.node) && !closed.contains(next.node))
            	{
            		next.node.parent = currentNode;
            		next.node.reached=next.move;

            		open.add(next.node);

            	}
            }
        }

        return extractPath(currentNode);
    }

	public synchronized int[] computePathBFS(int s, int[] targets, Game game)
    {
		return computePathBFS(s, targets, MOVE.NEUTRAL, game);
    }

    private synchronized int[] extractPath(N target)
    {
    	ArrayList<Integer> route = new ArrayList<Integer>();
        N current = target;
        route.add(current.index);

        while (current.parent != null)
        {
            route.add(current.parent.index);
            current = current.parent;
        }

        Collections.reverse(route);

        int[] routeArray=new int[route.size()];

        for(int i=0;i<routeArray.length;i++)
        	routeArray[i]=route.get(i);

        return routeArray;
    }

    public void resetGraph()
    {
    	for(N node : graph)
    	{
    		node.g=0;
    		node.h=0;
    		node.parent=null;
    		node.reached=null;
    	}
    }
}
