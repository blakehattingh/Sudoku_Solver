package sample;

import java.util.ArrayList;
import java.util.Arrays;

public class NodeFamily {

	private FamilyType _familyType;
	private int _id;
	private ArrayList<Node> _nodes = new ArrayList<Node>(9);
	private ArrayList<Integer> _currentValues = new ArrayList<Integer>(9);
	private ArrayList<Integer> _missingValues = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
	private ArrayList<Node> _unassignedNodes = new ArrayList<Node>();
	
	public NodeFamily(int familyType, int id) throws Exception {
		if (familyType > 2 || familyType < 0) {
			throw new IllegalArgumentException("familyType must be between 0 and 2 inclusive");
		} 
		if (familyType == 0) { _familyType = FamilyType.ROW; }
		if (familyType == 1) { _familyType = FamilyType.COLUMN; }
		if (familyType == 2) { _familyType = FamilyType.BOX; }
		
		if (id > 8 || id < 0) {
			throw new IllegalArgumentException("ID must be between 0 and 8 inclusive");
		} else {
			_id = id;
		}
	
	}

	public void addAValue(int value){
		_currentValues.add(value);
		try {
			_missingValues.remove(_missingValues.indexOf(value));
		} catch (ArrayIndexOutOfBoundsException e){
			System.out.println("INVALID Sudoku: Two numbers in the same " + _familyType.toString() + " (there are two number " + value + "'s in " + _familyType.toString() + " " + _id + ")");

		}
	}
	
	public void addNode(Node node) {
		_nodes.add(node);
		if (_familyType.equals(FamilyType.ROW)){
		node.setRow(this);
		} else if (_familyType.equals(FamilyType.COLUMN)) {
			node.setCol(this);
		} else if (_familyType.equals(FamilyType.BOX)) {
			node.setBox(this);
		}
	}

	public void updateNodePossibilities(){
		for (Node node : _nodes){
			if (!node.hasBeenAssigned) {
				for (int val : _currentValues) {
					node.removePossibility(val);
				}
			}
		}
	}
	
	public FamilyType getType() {
		return _familyType;
	}
	
	public int getID() {
		return _id;
	}
	
	public int getWeight() {

		return _currentValues.size();
	}
	
	public ArrayList<Integer> getMissingValues() {
		return _missingValues;
	}
	
	public ArrayList<Node> getUnassignedNodes() {
		_unassignedNodes.clear();
		for (Node node : _nodes){
			if (!node.hasBeenAssigned){
				_unassignedNodes.add(node);
			}
		}
		return _unassignedNodes;
	}
	//this method returns an array of nodes that have a given value as one of their possibilities
	public ArrayList<Node> nodesWithPossibilityX(int val){
		ArrayList<Node> nodesWithPossibilityX = new ArrayList<Node>();
		for (Node node : getUnassignedNodes()){
			if (node.getPossibilities().contains(val)){
				nodesWithPossibilityX.add(node);
			}
		}
		return nodesWithPossibilityX;
	}

	public Node getNode(int position){
		return _nodes.get(position);
	}
	public ArrayList<Node> getNodes(){
		return _nodes;
	}

}
