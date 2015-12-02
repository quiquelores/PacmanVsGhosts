package pacman.entries.pacman;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.KeyBoardInput;

/*
 * Allows a human player to play the game using the arrow key of the keyboard. The data from the play session will be written to a file.
 */

public class PacmanPerceptronGatherData extends Controller<MOVE>
{
	public KeyBoardInput input;
	String data = "";
    
    public PacmanPerceptronGatherData(KeyBoardInput input)
    {
    	this.input=input;
    }
    
    public KeyBoardInput getKeyboardInput()
    {
    	return input;
    }
    
    public void writeDataToFile() {
    	try {
    		FileWriter fileWriter = new FileWriter("perceptronTrainingData.txt", true);
    		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    		bufferedWriter.write(data);
    		bufferedWriter.close();
    	}
    	catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public MOVE getMove(Game game,long dueTime)
    {
    	int[] features = getFeatures(game);
    	
    	// Creating the data for the current frame's state
    	String frameData = "";
    	for (int i = 0; i < features.length; ++i) {
    		frameData += features[i] + ",";
    	}    	
    	
    	MOVE move;
    	switch(input.getKey())
    	{
	    	case KeyEvent.VK_UP: 	move = MOVE.UP; break;
	    	case KeyEvent.VK_RIGHT: move = MOVE.RIGHT; break;
	    	case KeyEvent.VK_DOWN: 	move = MOVE.DOWN; break;
	    	case KeyEvent.VK_LEFT: 	move = MOVE.LEFT; break;
	    	default: 				move = MOVE.NEUTRAL;
    	}
    	
    	// Determining the decision the human made
    	int decision;
    	Game gameCopy = game.copy();
    	gameCopy.advanceGame(move, Executor.ghostAI.getMove());
    	// Getting the distance to the nearest active pill for both the current and next frames
    	int nearestPillDistance = Integer.MAX_VALUE;
    	int nearestPillDistance2 = Integer.MAX_VALUE;
    	for (int nodeIndex : game.getActivePillsIndices()) {
    		int d = (int)Math.round(game.getDistance(game.getPacmanCurrentNodeIndex(), nodeIndex, DM.PATH));
    		if (d < nearestPillDistance) {
    			nearestPillDistance = d;
    		}
    		int d2 = (int)Math.round(gameCopy.getDistance(gameCopy.getPacmanCurrentNodeIndex(), nodeIndex, DM.PATH));
    		if (d2 < nearestPillDistance2) {
    			nearestPillDistance2 = d2;
    		}
    	}
    	// Getting the distance to the nearest ghost and its edibleTime
    	int nearestGhostDistance = Integer.MAX_VALUE;
    	int nearestGhostDistance2 = Integer.MAX_VALUE;
    	for (GHOST g : GHOST.values()) {
    		int d = (int)Math.round(game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(g), DM.PATH));
    		if (d < nearestGhostDistance) {
    			nearestGhostDistance = d;
    		}
    		int d2 = (int)Math.round(gameCopy.getDistance(gameCopy.getPacmanCurrentNodeIndex(), gameCopy.getGhostCurrentNodeIndex(g), DM.PATH));
    		if (d2 < nearestGhostDistance2) {
    			nearestGhostDistance2 = d2;
    		}
    	}
    	// if going towards a pill
    	if (nearestPillDistance2 < nearestPillDistance) {
    		// if going away from a ghost
    		if (nearestGhostDistance2 >= nearestGhostDistance) {
    			// If ghosts are closer than the nearest pill is
    			if (nearestGhostDistance2 <= nearestPillDistance2) {
    				decision = -1; // going away from ghosts
    			}
    			else {
    				decision = 1; // going towards pills
    			}
    		}
    		else {
    			decision = 1;
    		}
    	}
    	else {
    		decision = -1; // going away from a ghost
    	}
    	
    	
    	String decisionStr = decision + ",";
    	
    	// Appending the frame data to the game's data log
    	data += frameData + decisionStr + "\n";
    	
    	return move;
    }
    
    public static int[] getFeatures(Game game) {
    	ArrayList<Integer> features = new ArrayList<Integer>();
    	
    	// Getting the Score
    	features.add(game.getScore());
    	
    	// Getting the distance to the nearest active pill
    	int nearestPillDistance = Integer.MAX_VALUE;
    	for (int nodeIndex : game.getActivePillsIndices()) {
    		int d = (int)Math.round(game.getDistance(game.getPacmanCurrentNodeIndex(), nodeIndex, DM.PATH));
    		if (d < nearestPillDistance) {
    			nearestPillDistance = d;
    		}
    	}
    	features.add(nearestPillDistance);
    	
    	// Getting the distance to the nearest active power pill
    	int nearestPowerPillDistance = Integer.MAX_VALUE;
    	for (int nodeIndex : game.getActivePowerPillsIndices()) {
    		int d = (int)Math.round(game.getDistance(game.getPacmanCurrentNodeIndex(), nodeIndex, DM.PATH));
    		if (d < nearestPowerPillDistance) {
    			nearestPowerPillDistance = d;
    		}
    	}
    	features.add(nearestPowerPillDistance);
    	
    	// Getting the distance to the nearest ghost and its edibleTime
    	int nearestGhostDistance = Integer.MAX_VALUE;
    	int edibleTime = 0;
    	for (GHOST g : GHOST.values()) {
    		int d = (int)Math.round(game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(g), DM.PATH));
    		if (d < nearestGhostDistance) {
    			nearestGhostDistance = d;
    			edibleTime = game.getGhostEdibleTime(g);
    		}
    	}
    	features.add(nearestGhostDistance);
    	features.add(edibleTime);
    	
    	int[] featureArray = new int[features.size()];
        for(int i = 0; i < featureArray.length; i++) {
        	featureArray[i] = features.get(i);
    	}
        return featureArray;
    }
    
}