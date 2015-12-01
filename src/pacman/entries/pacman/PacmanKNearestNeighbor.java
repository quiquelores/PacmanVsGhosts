package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.io.*;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.TreeSearch;
import pacman.game.Game;

public class PacmanKNearestNeighbor extends Controller<MOVE>
{
	
	public ArrayList<Integer[]> dataTable = new ArrayList<Integer[]>();
	float[] axisWeights;
	
	final int NUM_TARGETS_AT_A_TIME = 20;
	public ArrayList<Integer[]> nextTargets = new ArrayList<Integer[]>();
	
	public PacmanKNearestNeighbor() {
		// Reading the training data
		try {
			FileReader fileReader = new FileReader("trainingData.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				if (line != "") {
					ArrayList<Integer> rowInfo = new ArrayList<Integer>();
					String[] fields = line.split(",");
					for (int i = 0; i < fields.length; ++i) {
						if (fields[i] != "") {
							rowInfo.add(Integer.parseInt(fields[i]));
						}
					}
					Integer[] rowArray = new Integer[rowInfo.size()];
					for (int i = 0; i < rowArray.length; ++i) {
						rowArray[i] = rowInfo.get(i);
					}
					dataTable.add(rowArray);
				}
			}   

			bufferedReader.close(); 
		}
		catch(FileNotFoundException ex) {
            System.out.println("Unable to open file 'trainingData.txt'");                
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
		
		axisWeights = new float[5];
		axisWeights[0] = 0.20f; // Score
		axisWeights[1] = 0.5f; // Distance to nearest active pill
		axisWeights[2] = 0.0f; // Distance to nearest active power pill
		axisWeights[3] = 5.0f; // Distance to nearest ghost
		axisWeights[4] = 0.0f; // Edible time of nearest ghost
	}
	
	public MOVE getMove(Game game, long timeDue)
	{
		TreeSearch graph = new TreeSearch();
		graph.createGraph(game.getCurrentMaze().graph);
		
		if (game.wasPacManEaten()) {
			nextTargets = new ArrayList<Integer[]>();
		}
		
		if (nextTargets.size() == 0) {
			int[] features = PacmanGatherData.getFeatures(game);
			nextTargets = getTargetFeatures(features, 1, NUM_TARGETS_AT_A_TIME); // getTargetFeatures(features, k, numTargetsToFetch)
		}
		
		Integer[] targetFeatures = nextTargets.remove(0);
		
		HashMap<MOVE, Game> nextStates = graph.getImmediateNextGameStates(game.getPacmanCurrentNodeIndex(), game);
		MOVE bestMove = MOVE.NEUTRAL;
		double proximity = Double.MAX_VALUE;
		for (MOVE move : nextStates.keySet()) {
			Game gameState = nextStates.get(move);
			int[] nextFeatures = PacmanGatherData.getFeatures(gameState);
			double d = getDistanceBetweenFeatures(nextFeatures, targetFeatures);
			if (d < proximity) {
				proximity = d;
				bestMove = move;
			}
		}

		return bestMove;
	}
	
	// Returns the Euclidean distance between the features of two game states
	double getDistanceBetweenFeatures(int[] from, int[] to) {
		double sum = 0;
		for (int i = 0; i < from.length; ++i) {
			sum += Math.pow(from[i] - to[i], 2) * axisWeights[i];
		}
		return Math.pow(sum, 0.5);
	}
	
	double getDistanceBetweenFeatures(int[] from, Integer[] to) {
		double sum = 0;
		for (int i = 0; i < from.length; ++i) {
			sum += Math.pow(from[i] - to[i], 2) * axisWeights[i];
		}
		return Math.pow(sum, 0.5);
	}
	
	ArrayList<Integer[]> getTargetFeatures(int[] currentFeatures, int k, int numTargetsToFetch) {
		// Calculating the distances of each feature set from the currentFeatures
		double[] distances = new double[dataTable.size() - numTargetsToFetch];
		// used dataTable.size() - numTargetsToFetch because we need to find a point on the dataTable 
		// that matches the current state and then follow the next numTargetsToFetch moves after it
		for (int i = 0; i < distances.length; ++i) {
			distances[i] = getDistanceBetweenFeatures(currentFeatures, dataTable.get(i));
		}
		// Getting the k closest features to currentFeatures
		if (k > distances.length) {
			k = distances.length;
		}
		ArrayList<Integer> mins = new ArrayList<Integer>();
		for (int iteration = 0; iteration < k; ++iteration) { // Gets the indices of the k-closest features
			double min = Double.MAX_VALUE;
			int index = 0;
			for (int i = 0; i < distances.length; ++i) { 
				if (mins.contains(i + 1)) {
					continue; // Skips mins we've already counted before
				}
				if (distances[i] < min) {
					min = distances[i];
					index = i;
				}
			}
			mins.add(index + 1);
		}
		// Getting the weights of each feature set based on the distance to the current state
		double[] weights = new double[k];
		double totalDistance = 0;
		for (int i = 0; i < mins.size(); ++i) {
			totalDistance += distances[mins.get(i)];
		}
		for (int i = 0; i < mins.size(); ++i) {
			weights[i] = distances[mins.get(i)] / totalDistance;
		}
		// Getting the weighted average of the k feature sets
		ArrayList<Integer[]> targets = new ArrayList<Integer[]>();
		for (int targetIndex = 0; targetIndex < numTargetsToFetch; targetIndex++) {
			ArrayList<Integer[]> kClosestFeatureSets = new ArrayList<Integer[]>();
			for (int i = 0; i < mins.size(); ++i) {
				//System.out.println(mins.get(i) + " + " + targetIndex + ": " + dataTable.size());
				kClosestFeatureSets.add(dataTable.get(mins.get(i) + targetIndex));
			}
			Integer[] targetFeatures = new Integer[currentFeatures.length];
			for (int i = 0; i < targetFeatures.length; ++i) {
				double sum = 0;
				for (int j = 0; j < k; ++j) {
					sum += kClosestFeatureSets.get(j)[i] * weights[j];
				}
				targetFeatures[i] = (int)Math.round(sum);
			}
			targets.add(targetFeatures);
		}
		
		/*
		String debugLine = "Current: ";
		for (int i = 0; i < currentFeatures.length; ++i) {
			debugLine += currentFeatures[i] + ",";
		}
		debugLine += "     Target: ";
		for (int t = 0; t < numTargetsToFetch; ++t) {
			for (int i = 0; i < currentFeatures.length; ++i) {
				debugLine += targets.get(t)[i] + ",";
			}
			debugLine += "\n";
		}
		System.out.println(debugLine);
		*/
		
		return targets;
	}
}
