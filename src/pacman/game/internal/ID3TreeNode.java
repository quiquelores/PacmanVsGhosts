package pacman.game.internal;

import java.util.ArrayList;

import pacman.game.Game;


public class ID3TreeNode {
    	
	private ID3TreeNode(int v) {
		value = v;
	}
	
	public static ID3TreeNode buildTree(ArrayList<float[]> data, ArrayList<Integer> splitFeatures) {
		ArrayList<Float> classes = new ArrayList<Float>();
		int numPositiveClasses = 0;
		int numNegativeClasses = 0;
		for (int i = 0; i < data.size(); ++i) {
			float val = data.get(i)[data.get(i).length - 1];
			if (val > 0) {
				numPositiveClasses += 1;
			}
			else {
				numNegativeClasses += 1;
			}
			classes.add(val);
		}
		if (numPositiveClasses == classes.size()) {
			return new ID3TreeNode(1);
		}
		else if (numNegativeClasses == classes.size()) {
			return new ID3TreeNode(-1);
		}
		else if (splitFeatures.size() == 0) {
			if (numPositiveClasses > numNegativeClasses) {
				return new ID3TreeNode(1);
			}
			else {
				return new ID3TreeNode(-1);
			}
		}
		int splitFeature = getBestFeatureToSplit(data, splitFeatures);
		for (int i = 0; i < splitFeatures.size(); ++i) {
			if (splitFeatures.get(i) == splitFeature) {
				splitFeatures.remove(i);
				break;
			}
		}
		ID3TreeNode node = new ID3TreeNode(splitFeature);
		ArrayList<ArrayList<float[]>> splitData = splitDataByFeature(data, splitFeature);
		for (int i = 0; i < splitData.size(); ++i) {
			node.addChild(buildTree(splitData.get(i), splitFeatures));
		}
		return node;
	}
	
	private static int getBestFeatureToSplit(ArrayList<float[]> data, ArrayList<Integer> splitFeatures) {
		float originalEntropy = entropy(data);
		float maxInfoGain = Float.MIN_VALUE;
		int bestFeature = splitFeatures.get(0);
		for (int i = 0; i < splitFeatures.size(); ++i) {
			ArrayList<ArrayList<float[]>> splitDatasets = splitDataByFeature(data, splitFeatures.get(i));
			float e = 0;
			for (int j = 0; j < splitDatasets.size(); ++j) {
				float p = ((float)splitDatasets.get(j).size()) / ((float)data.size());
				e += p * entropy(splitDatasets.get(j));
			}
			float infoGain = originalEntropy - e;
			if (infoGain > maxInfoGain) {
				maxInfoGain = infoGain;
				bestFeature = splitFeatures.get(i);
			}
		}
		return bestFeature;
	}
	
	private static float entropy(ArrayList<float[]> data) {
		int numPositiveClasses = 0;
		int numNegativeClasses = 0;
		for (int i = 0; i < data.size(); ++i) {
			float val = data.get(i)[data.get(i).length - 1];
			if (val > 0) {
				numPositiveClasses += 1;
			}
			else {
				numNegativeClasses += 1;
			}
		}
		float pPositive = ((float)numPositiveClasses) / ((float)data.size());
		float pNegative = ((float)numNegativeClasses) / ((float)data.size());
		float e = 0;
		e += pPositive * (Math.log(pPositive) / Math.log(2));
		e += pNegative * (Math.log(pNegative) / Math.log(2));
		return -e;
	}
	
	private static ArrayList<ArrayList<float[]>> splitDataByFeature(ArrayList<float[]> data, int splitFeature) {
		ArrayList<ArrayList<float[]>> results = new ArrayList<ArrayList<float[]>>();
		for (int i = 0; i < data.size(); ++i) {
			int branchIndex = getResultingBranchIndexAfterSplit(data.get(i), splitFeature);
			while (branchIndex >= results.size()) {
				results.add(new ArrayList<float[]>());
			}
			results.get(branchIndex).add(data.get(i));
		}
		return results;
	}
	
	private static int getResultingBranchIndexAfterSplit(float[] attributes, int splitFeature) {
		if (splitFeature == 0) { // Score
			return 0; // not sure how I want to split by score. I'll just skip splitting by score by partitioning all elements to the same side
		}
		else if (splitFeature == 1) { // Distance to nearest active pill
			if (attributes[splitFeature] > 25) {
				return 0;
			}
			else if (attributes[splitFeature] > 5) {
				return 1;
			}
			else {
				return 2;
			}
		}
		else if (splitFeature == 2) { // Distance to nearest active power pill
			if (attributes[splitFeature] > 20) {
				return 0;
			}
			else {
				return 1;
			}
		}
		else if (splitFeature == 3) { // Distance to nearest ghost
			if (attributes[splitFeature] > 10) {
				return 0;
			}
			else {
				return 1;
			}
		}
		else if (splitFeature == 4) { // Nearest ghost edible time
			if (attributes[splitFeature] <= 0) {
				return 0;
			}
			else {
				return 1;
			}
		}
		return 0; // Otherwise, just partition to branch 0 (should never have to happen though)
	}
	
	private void addChild(ID3TreeNode child) {
		children.add(child);
		//child.parent = this;
	}
	
	public int classify(float[] features) {
		if (children.size() == 0) {
			// If it is a leaf, return the value. The value represents the classification of that branch
			return value;
		}
		// Otherwise, determine which branch to partition the features into based on the value of the current node. 
		// The value represents the attribute index which this node uses to partition the data
		int branchIndex = getResultingBranchIndexAfterSplit(features, value);
		if (branchIndex >= children.size()) {
			return value;
		}
		// Recurse on that branch
		return children.get(branchIndex).classify(features);
	}
    	
    private int value;
    //private ID3TreeNode parent;
    private ArrayList<ID3TreeNode> children = new ArrayList<ID3TreeNode>();
    
}
