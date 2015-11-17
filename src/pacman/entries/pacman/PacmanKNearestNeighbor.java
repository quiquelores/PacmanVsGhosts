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
	}
	
	public MOVE getMove(Game game, long timeDue)
	{
		TreeSearch graph = new TreeSearch();
		graph.createGraph(game.getCurrentMaze().graph);
		
		int[] features = PacmanGatherData.getFeatures(game);
		int[] targetFeatures = getTargetFeatures(features, 1); // getTargetFeatures(features, k)
		
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
			sum += Math.pow(from[i] - to[i], 2);
		}
		return Math.pow(sum, 0.5);
	}
	
	double getDistanceBetweenFeatures(int[] from, Integer[] to) {
		double sum = 0;
		for (int i = 0; i < from.length; ++i) {
			sum += Math.pow(from[i] - to[i], 2);
		}
		return Math.pow(sum, 0.5);
	}
	
	int[] getTargetFeatures(int[] currentFeatures, int k) {
		// Calculating the distances of each feature set from the currentFeatures
		double[] distances = new double[dataTable.size()];
		for (int i = 0; i < dataTable.size(); ++i) {
			distances[i] = getDistanceBetweenFeatures(currentFeatures, dataTable.get(i));
		}
		// Getting the k closest features to currentFeatures
		if (k > dataTable.size()) {
			k = dataTable.size();
		}
		ArrayList<Integer> mins = new ArrayList<Integer>();
		for (int iteration = 0; iteration < k; ++iteration) {
			double min = Double.MAX_VALUE;
			int index = 0;
			for (int i = 0; i < distances.length; ++i) {
				if (distances[i] < min) {
					min = distances[i];
					index = i;
				}
			}
			// If the min value found was not the last value in the training data, 
			// the target should the step after that (ie. the move the human made from that state) 
			if (index + 1 < distances.length) {
				mins.add(index + 1);
			}
			else {
				mins.add(index);
			}
			distances[index] = Double.MAX_VALUE;
		}
		ArrayList<Integer[]> kClosestFeatureSets = new ArrayList<Integer[]>();
		double[] weights = new double[k];
		double totalDistance = 0;
		for (int i = 0; i < mins.size(); ++i) {
			totalDistance += distances[mins.get(i)];
		}
		for (int i = 0; i < mins.size(); ++i) {
			kClosestFeatureSets.add(dataTable.get(mins.get(i)));
			weights[i] = distances[mins.get(i)] / totalDistance;
		}
		// Getting the weighted average of the k feature sets
		int[] targetFeatures = new int[currentFeatures.length];
		for (int i = 0; i < targetFeatures.length; ++i) {
			double sum = 0;
			for (int j = 0; j < k; ++j) {
				sum += kClosestFeatureSets.get(j)[i] * weights[j];
			}
			targetFeatures[i] = (int)Math.round(sum);
		}
		return targetFeatures;
	}
}
