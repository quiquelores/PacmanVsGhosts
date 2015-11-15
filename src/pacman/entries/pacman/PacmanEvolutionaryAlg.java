package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.Collections;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.*;


public class PacmanEvolutionaryAlg extends Controller<MOVE> {
	private MOVE myMove = MOVE.NEUTRAL;
	private int k = 8; //How many "best" offsprings do we keep on each iteration

	public MOVE getMove(Game game, long timeDue) {

	//@TODO: repeat the following process until what? we reach a winning state and/or our score doubles?
		
		ArrayList<TreeNode> list = new ArrayList<TreeNode>();
		ArrayList<TreeNode> helperList = new ArrayList<TreeNode>();
		ArrayList<TreeNode> offspringList = new ArrayList<TreeNode>();

		list.add(new TreeNode(game));

		//iterate game finding all children nodes within 4 levels of depth.
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < list.size(); i++) {
				TreeNode t = list.get(i);
				MOVE[] possibleMoves = t.gameState.getPossibleMoves(t.gameState.getPacmanCurrentNodeIndex());
				for (MOVE move : possibleMoves) {
					TreeNode next = t.evolve(move);
					helperList.add(next);
					offspringList.add(next);
				}
			}
			list = helperList;
			helperList = new ArrayList<TreeNode>();
		}
		
		//sort by evaluation function? use treenodes instead? make them comparable
		Collections.sort(offspringList);
		
		//remove n-k worst offsprings
		for(int i = offspringList.size() -1; i>= k; i--){
			offspringList.remove(k);	
		}
		
		//@TODO: mutate them

		return myMove;
	}
}
