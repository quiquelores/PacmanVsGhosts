package pacman.game.internal;

import java.util.Comparator;


public class StateComparator implements Comparator<TreeNode>{
	public int compare(TreeNode x, TreeNode y){
		if(StateEvaluator.evalGameState(x.gameState)> StateEvaluator.evalGameState(y.gameState)){
			return -1;
		} else if(StateEvaluator.evalGameState(x.gameState) < StateEvaluator.evalGameState(y.gameState)){
			return 1;
		}
		return 0;
	}
}