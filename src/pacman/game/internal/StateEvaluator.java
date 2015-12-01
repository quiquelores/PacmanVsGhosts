package pacman.game.internal;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;

public class StateEvaluator {
	
public static int evalGameState(Game game){
		
		if(game.wasPacManEaten()) return Integer.MIN_VALUE;
		
		int score = 100*game.getScore();

		//having edible ghosts will lead to a higher score. being close to them will lead to a higher score if edible, and to a lower score if they're not edible.
		for(GHOST ghost: GHOST.values()){
			if(game.isGhostEdible(ghost)){
				score += 50;
				score -= game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost), DM.PATH);
			} else {
				score += game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost), DM.PATH);
			}
		}
		return score;
	}
}
