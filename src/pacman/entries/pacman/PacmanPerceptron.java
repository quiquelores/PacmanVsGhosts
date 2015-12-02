package pacman.entries.pacman;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.TreeSearch;
import pacman.game.Game;

public class PacmanPerceptron extends Controller<MOVE>
{
	
	float[] featureWeights;
	
	public PacmanPerceptron() {
		// Initializing the weights
		featureWeights = new float[5];
		featureWeights[0] = 0.01f; // Score
		featureWeights[1] = 0.01f; // Distance to nearest active pill
		featureWeights[2] = 0.01f; // Distance to nearest active power pill
		featureWeights[3] = 0.01f; // Distance to nearest ghost
		featureWeights[4] = 0.01f; // Edible time of nearest ghost
		
		// Initializing the training data
		ArrayList<float[]> inputVectors = new ArrayList<float[]>();
		ArrayList<Float> desiredOutputs = new ArrayList<Float>();
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
					
					/*
					float[] rowArray = new float[rowInfo.size() - 1];
					for (int i = 0; i < rowArray.length; ++i) {
						rowArray[i] = rowInfo.get(i);
					}
					*/
					
					float[] rowArray = new float[rowInfo.size()];
					rowArray[0] = 1f;
					for (int i = 0; i < rowArray.length - 1; ++i) {
						rowArray[i + 1] = rowInfo.get(i);
					}
					
					inputVectors.add(rowArray);
					desiredOutputs.add(rowInfo.get(rowInfo.size() - 1));
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
		
		
		// Setting the learning rate
		float alpha = 0.01f;
		
		// Learning from the training data
		for (int i = 0; i < 10; ++i) { //inputVectors.size()
			float[] x = inputVectors.get(i);
			Float d = desiredOutputs.get(i);
			// Calculating the actual output
			float y = 0;
			for (int j = 0; j < featureWeights.length; ++j) {
				y += featureWeights[j] * x[j];
			}
			System.out.println("y: " + y + "    d: " + d);
			System.out.println(featureWeights[0] + ", " + featureWeights[1] + ", " + featureWeights[2] + ", " + featureWeights[3] + ", " + featureWeights[4]);
			// Updating the weights
			for (int j = 0; j < featureWeights.length; ++j) {
				featureWeights[j] += alpha * (d - y) * x[j];
			}
			System.out.println(featureWeights[0] + ", " + featureWeights[1] + ", " + featureWeights[2] + ", " + featureWeights[3] + ", " + featureWeights[4]);
			System.out.println("");
		}
	}

	public MOVE getMove(Game game, long timeDue)
	{
		MOVE move = MOVE.NEUTRAL;
		
		int[] currentFeaturesAsInts = PacmanPerceptronGatherData.getFeatures(game);
		
		/*
		float[] currentFeatures = new float[currentFeaturesAsInts.length];
        for(int i = 0; i < currentFeatures.length; i++) {
        	currentFeatures[i] = currentFeaturesAsInts[i];
    	}
        */
		
		float[] currentFeatures = new float[currentFeaturesAsInts.length + 1];
        for(int i = 0; i < currentFeaturesAsInts.length; i++) {
        	currentFeatures[i + 1] = currentFeaturesAsInts[i];
    	}
        
		float output = 0;
		for (int j = 0; j < featureWeights.length; ++j) {
			output += featureWeights[j] * currentFeatures[j];
		}
		
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
			move = game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), nearestPillNodeIndex, DM.PATH);
		}
		else {
			// Move away from nearest ghost
			int nearestGhostDistance = Integer.MAX_VALUE;
			GHOST nearestGhost = GHOST.BLINKY;
	    	for (GHOST g : GHOST.values()) {
	    		int d = (int)Math.round(game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(g), DM.PATH));
	    		if (d < nearestGhostDistance) {
	    			nearestGhostDistance = d;
	    			nearestGhost = g;
	    		}
	    	}
			move = game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(nearestGhost), DM.PATH);
		}
		return move;
	}
}
