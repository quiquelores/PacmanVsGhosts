package pacman.entries.pacman;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.controllers.Controller;
import pacman.controllers.KeyBoardInput;

/*
 * Allows a human player to play the game using the arrow key of the keyboard. The data from the play session will be written to a file.
 */

public class PacmanGatherData extends Controller<MOVE>
{
	public KeyBoardInput input;
	String data = "";
    
    public PacmanGatherData(KeyBoardInput input)
    {
    	this.input=input;
    }
    
    public KeyBoardInput getKeyboardInput()
    {
    	return input;
    }
    
    public void writeDataToFile() {
    	try {
    		FileWriter fileWriter = new FileWriter("trainingData.txt", true);
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
    	
    	// Appending the frame data to the game's data log
    	data += frameData + "\n";    	
    	
    	switch(input.getKey())
    	{
	    	case KeyEvent.VK_UP: 	return MOVE.UP;
	    	case KeyEvent.VK_RIGHT: return MOVE.RIGHT;
	    	case KeyEvent.VK_DOWN: 	return MOVE.DOWN;
	    	case KeyEvent.VK_LEFT: 	return MOVE.LEFT;
	    	default: 				return MOVE.NEUTRAL;
    	}
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
    		if (d >= 0 && d < nearestGhostDistance) {
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