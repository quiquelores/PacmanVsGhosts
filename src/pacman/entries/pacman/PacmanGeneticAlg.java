package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.Collections;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.*;

public class PacmanGeneticAlg extends Controller<MOVE> {
	private MOVE myMove = MOVE.NEUTRAL;

	public MOVE getMove(Game game, long timeDue) {
		ArrayList<TreeNode> list = new ArrayList<TreeNode>();
		ArrayList<TreeNode> helperList = new ArrayList<TreeNode>();
		ArrayList<TreeNode> offspringList = new ArrayList<TreeNode>();

		list.add(new TreeNode(game));

		for (int a = 0; a < 4; a++) {
			// iterate game finding all children nodes within 2 levels of
			// depth.
			for (int j = 0; j < 2; j++) {
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

			for (int m = 0; m < offspringList.size(); m++) {
				TreeNode x = offspringList.get((int) (Math.random()*offspringList.size()));
				TreeNode y = offspringList.get((int)Math.random()*offspringList.size());

				TreeNode crossoverChild = crossover(x,y);
				if (Math.random() <= 0.05) {
					// mutate by moving to a random position near them
					MOVE[] moves = crossoverChild.gameState
							.getPossibleMoves(crossoverChild.gameState.getPacmanCurrentNodeIndex());
					MOVE randomMOVE = moves[(int) (Math.random() * moves.length)];
					crossoverChild = crossoverChild.evolve(randomMOVE);
				}
				helperList.add(crossoverChild);

			}
			list = helperList;
			helperList = new ArrayList<TreeNode>();
			offspringList = new ArrayList<TreeNode>();

		}
		Collections.sort(list, new StateComparator());

		int[] computedPath = TreeNode.extractPath(list.get(0));

		if (computedPath.length > 1) {
			myMove = game.getMoveToMakeToReachDirectNeighbour(game.getPacmanCurrentNodeIndex(), computedPath[1]);
		}
		return myMove;
	}

	//crossover by taking alternate 'moves' from t1's and t2's the last 40 moves
	//return the TreeNode containing that game state
	TreeNode crossover(TreeNode t1, TreeNode t2) {
		ArrayList<MOVE> moves = new ArrayList<MOVE>();
		int index = 0;
		while(t1.parent != null && t2.parent != null && index < 40){
			if (index % 2 == 0) {
				moves.add(0, t2.reachedBy);
			} else {
				moves.add(0, t1.reachedBy);
			}
			t1 = t1.parent;
			t2 = t2.parent;
			index++;
		}
		while (!moves.isEmpty()) {
			t2 = t2.evolve(moves.remove(0));
		}
		return t2;
	}
}
