package pacman.entries.pacman;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.TreeSearch;
import pacman.game.internal.ID3TreeNode;
import pacman.game.Game;

public class PacmanID3 extends Controller<MOVE>
{
	
	ID3TreeNode root;
	
	public PacmanID3() {
		// Initializing the training data
		ArrayList<float[]> data = new ArrayList<float[]>();
		// Reading the training data
		try {
			FileReader fileReader = new FileReader("perceptronTrainingData.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
					
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				if (line != "") {
					ArrayList<Float> rowInfo = new ArrayList<Float>();
					String[] fields = line.split(",");
					for (int i = 0; i < fields.length; ++i) {
						if (fields[i] != "") {
							rowInfo.add(Float.parseFloat(fields[i]));
						}
					}
					
					float[] rowArray = new float[rowInfo.size()];
					for (int i = 0; i < rowArray.length; ++i) {
						rowArray[i] = rowInfo.get(i);
					}
					data.add(rowArray);
				}
			}   
			bufferedReader.close(); 
		}
		catch(FileNotFoundException ex) {
			System.out.println("Unable to open file 'perceptronTrainingData.txt'");                
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		
		// Creating the decision tree based on the training data
		ArrayList<Integer> splitFeatures = new ArrayList<Integer>();
		splitFeatures.add(1); // Nearest pill attribute
		splitFeatures.add(2); // Nearest power pill attribute
		splitFeatures.add(3); // Nearest ghost attribute
		splitFeatures.add(4); // Nearest ghost's edible time attribute
		root = ID3TreeNode.buildTree(data, splitFeatures);
		
	}

	public MOVE getMove(Game game, long timeDue)
	{
		int[] currentFeaturesAsInts = PacmanPerceptronGatherData.getFeatures(game);
		float[] currentFeatures = new float[currentFeaturesAsInts.length];
        for(int i = 0; i < currentFeatures.length; i++) {
        	currentFeatures[i] = currentFeaturesAsInts[i];
    	}
        
        MOVE move = MOVE.NEUTRAL;
        int output = root.classify(currentFeatures);
        if (output > 0) {
			// Move towards nearest pill
			int nearestPillDistance = Integer.MAX_VALUE;
			int nearestPillNodeIndex = 0;
	    	for (int nodeIndex : game.getActivePillsIndices()) {
	    		int d = (int)Math.round(game.getDistance(game.getPacmanCurrentNodeIndex(), nodeIndex, DM.PATH));
	    		if (d < nearestPillDistance) {
	    			nearestPillDistance = d;
	    			nearestPillNodeIndex = nodeIndex;
	    		}
	    	}
			move = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), nearestPillNodeIndex, DM.PATH);
		}
		else {
			// Move away from nearest ghost
			int nearestGhostDistance = Integer.MAX_VALUE;
			GHOST nearestGhost = GHOST.BLINKY;
	    	for (GHOST g : GHOST.values()) {
	    		int d = (int)Math.round(game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(g), DM.PATH));
	    		if (d >= 0 && d < nearestGhostDistance) {
	    			nearestGhostDistance = d;
	    			nearestGhost = g;
	    		}
	    	}
	    	
	    	move = game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(nearestGhost), DM.PATH);
	    	//move = game.getApproximateNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(nearestGhost), game.getPacmanLastMoveMade(), DM.PATH);
	    	
		}
		return move;
	}
}
