package sample;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class SudokuBoard {

	private ArrayList<Node> _board;
	private ArrayList<NodeFamily> _rows;
	private ArrayList<NodeFamily> _cols;
	private ArrayList<NodeFamily> _boxes;

	public SudokuBoard(){
		
		_board = new ArrayList<Node> ();
		_rows = new ArrayList<NodeFamily>();
		_cols = new ArrayList<NodeFamily>();
		_boxes = new ArrayList<NodeFamily>();
		
		//set up 9 rows cols and boxes
		for (int i = 0; i < 9; i++) {
			try {
			_rows.add(new NodeFamily(0,i));
			_cols.add(new NodeFamily(1,i));
			_boxes.add(new NodeFamily(2,i));
			} catch(IllegalArgumentException e ) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//create 81 nodes and allocate them to rows cols and boxes
		for (int i = 0; i <= 80; i++) {
			_board.add(new Node());
			Node tempNode = _board.get(i);
			//System.out.println(_board.get(i).getId());
			_rows.get(tempNode.getRow()).addNode(tempNode);
			_cols.get(tempNode.getCol()).addNode(tempNode);
			_boxes.get(tempNode.getBox()).addNode(tempNode);
		}
		
		int i = 26;
		
		//System.out.println(_board.get(i).getBox());
		//System.out.println(_board.get(i).getCol());
		//System.out.println(_board.get(i).getRow());
		
		
	}

	//returns true if the game of Sudoku is finished, i.e. if all nodes are assigned
	public boolean isComplete(){
		for (Node node : _board){
			if (!node.hasBeenAssigned){
				return false;
			}
		}
		return true;
	}

	public void updatePossibilities() {
		for (NodeFamily row : _rows) {
			row.updateNodePossibilities();
		}
		for (NodeFamily col : _cols) {
			col.updateNodePossibilities();
		}
		for (NodeFamily box : _boxes) {
			box.updateNodePossibilities();
		}
		for (Node node : _board) {
			if ((node.getPossibilities().size() == 1) && (!node.hasBeenAssigned)) {
				node.set_value(node.getPossibilities().get(0));
				//Main._dummyBoard[node.getCol()][node.getRow()].setText(node.getPossibilities().get(0).toString());
			}
		}


	}

	/**
	 * this method checks if there are any nodes such that they are the only node that could have a given value in a any row,col, or box
	 */
	public void findOnlyNodePossibleForAnyFamily(){

		for (NodeFamily row : _rows) {
			findOnlyNodePossibleForSpecificFamily(row);
		}
		for (NodeFamily col : _cols) {
			findOnlyNodePossibleForSpecificFamily(col);
		}
		for (NodeFamily box : _boxes) {
			findOnlyNodePossibleForSpecificFamily(box);
		}
		//findOnlyNodePossibleForSpecificFamily(_boxes.get(6));

	}
	/**
	 * this method checks if there are any nodes such that they are the only node that could have a given value in a specific row,col, or box. so even though the node could
	 * have multiple 'possible' values, it is the only only node that has x as a possibility and thus it must be that  value.
	 * @param: NodeFamily that is to be checked for a unique possibility
	 */
	// ******************should maybe change this method to only update possibilities instead of update values, and then only change the values in "findOnlyNodePossibleForAnyFamily()" which calls this method.
	private void findOnlyNodePossibleForSpecificFamily(NodeFamily nodeFam){
		int count = 0;
		Node nodeToUpdate = null;
		for(int value : nodeFam.getMissingValues()){
			for(Node node : nodeFam.getUnassignedNodes()) {
				//System.out.println("Unassigned nodes are" + nodeFam.getUnassignedNodes());
				//System.out.println(node.getId());
				if (node.getPossibilities().contains(value)) {
					count++;
					nodeToUpdate = node;
				}
			}
			/*if (nodeFam.getID() == 6) {
				System.out.println("-----" + count);
			}*/
			if (count == 1){
				//System.out.println(nodeToUpdate.getId());
				nodeToUpdate.set_value(value);
				//Main._dummyBoard[nodeToUpdate.getCol()][nodeToUpdate.getRow()].setText();
				break;
			}
			count = 0;
			nodeToUpdate = null;
		}
	}

	public void findClaimingPairs(){
		for (NodeFamily row : _rows){
			findClaimingPairsInSpecificRowOrCol(row);
		}
		for (NodeFamily col: _cols){
			findClaimingPairsInSpecificRowOrCol(col);
		}
	}

	/**
	 * This method checks if all possibilities of a value, x, in all boxes are in the same row/col. If so,
	 * then update possibilities of nodes in that row/col that arent in the box such that they cannot be that value.
	 *
	 */
	public void findPointingPairs(){
		for (NodeFamily box : _boxes){
			findPointingPairsInSpecificBox(box);
		}

	}

	/**
	 * This method checks if all possibilities of a value, x, in a specified box are in the same row/col. If so,
	 * then update possibilities of nodes in that row/col that arent in the box such that they cannot be that value.
	 * @param box
	 */
	private void findPointingPairsInSpecificBox(NodeFamily box){
		ArrayList<Node> nodesWithPossibleValue = new ArrayList<>();
		for (int value : box.getMissingValues()){
			for ( Node tempNode : box.getUnassignedNodes()){
				if (tempNode.existsInPossibilities(value)){
					nodesWithPossibleValue.add(tempNode);
				}
			}
			if ((nodesWithPossibleValue.size() > 3)){
				nodesWithPossibleValue.clear();
				continue;
			}
			else {
				int tempRowNumber = nodesWithPossibleValue.get(1).getRow();
				int tempColNumber = nodesWithPossibleValue.get(1).getCol();
				boolean sameRow = true;
				boolean sameCol = true;

				for (Node node : nodesWithPossibleValue){
					if (node.getRow() != tempRowNumber){
						sameRow = false;
					}
					if (node.getCol() != tempColNumber){
						sameCol = false;
					}
				}

				if(sameRow){
					removePossibilitiesInRowOrCol(box, nodesWithPossibleValue.get(1).get_row(), value );
				}
				if(sameCol){
					removePossibilitiesInRowOrCol(box, nodesWithPossibleValue.get(1).get_col(), value );
				}
			}

			nodesWithPossibleValue.clear();
		}
	}

	private void findClaimingPairsInSpecificRowOrCol(NodeFamily rowOrCol){
		FamilyType familyType = rowOrCol.getType();
		ArrayList<Node> nodesWithPossibleValue = new ArrayList<>();
		for (int value : rowOrCol.getMissingValues()){
			for (Node tempNode : rowOrCol.getUnassignedNodes()){
				if (tempNode.existsInPossibilities(value)){
					nodesWithPossibleValue.add(tempNode);
				}
			}
			if (nodesWithPossibleValue.size() > 3){
				nodesWithPossibleValue.clear();
				continue;
			}
			else {
				int tempBoxNumber = nodesWithPossibleValue.get(0).getBox();
				boolean sameBox = true;

				for (Node node : nodesWithPossibleValue){
					if (node.getBox() != tempBoxNumber){
						sameBox = false;
					}
				}
				if (sameBox){
					removePossibilitiesInBox(nodesWithPossibleValue.get(0).get_box(), rowOrCol, value);
				}
			}
			nodesWithPossibleValue.clear();
		}
	}

	/**
	 * This method removes a specific value from the possibilities of nodes that are in a specified
	 * box but that are not in a specified row/col.
	 * @param box
	 * @param row_col
	 * @param value
	 */
	private void removePossibilitiesInBox(NodeFamily box, NodeFamily row_col, int value){
		FamilyType type = row_col.getType();
		if ( type.equals(FamilyType.ROW)){
			int rowID = row_col.getID();
			for (Node node : box.getNodes()){
				if(node.getRow() != rowID){
					node.removePossibility(value);
				}
			}
		} else if ( type.equals(FamilyType.COLUMN)){
			int colID = row_col.getID();
			for (Node node : box.getNodes()){
				if(node.getCol() != colID){
					node.removePossibility(value);
				}
			}
		}
	}



	/**
	 * This method removes a specified value from the possibilities of nodes that are in a specified row/col
	 * but that are not in a specified box. i.e updates possibilities of a row/col but ignores a box in that row/col
	 * @param box
	 * @param row_col
	 * @param value
	 */
	private void removePossibilitiesInRowOrCol(NodeFamily box, NodeFamily row_col, int value){
		int boxID = box.getID();
		for(Node node : row_col.getNodes()){
			if(node.getBox() != boxID){
				node.removePossibility(value);
			}
		}

	}

	/**
	 * this method searches the board for pairs of nodes in the same node family that have the same two possibilities
	 */
	public void searchForPairs(){
		for (NodeFamily row : _rows) {
			findPairs(row);
		}
		for (NodeFamily col : _cols) {
			findPairs(col);
		}
		for (NodeFamily box : _boxes) {
			findPairs(box);
		}
	}
	public void searchForTriplets(){
		for (NodeFamily row : _rows) {
			findTriplets(row);
		}
		for (NodeFamily col : _cols) {
			findTriplets(col);
		}
		for (NodeFamily box : _boxes) {
			findTriplets(box);
		}
	}
	public void searchForQuads(){
		for (NodeFamily row : _rows) {
			findQuadruplets(row);
		}
		for (NodeFamily col : _cols) {
			findQuadruplets(col);
		}
		for (NodeFamily box : _boxes) {
			findQuadruplets(box);
		}
	}



	/**
	 * this method inspects a given node family for pairs of nodes that have the same two possibilities. it then removes
	 * those two possibilities from every other node in the node family. It also looks for two nodes such that they are the only two
	 * nodes in a node family with two specific possibilities, and if found, it removes all their other possibilities.
	 * @param nodeFam
	 */
	private void findPairs(NodeFamily nodeFam){
		for (Node i : nodeFam.getNodes()){
			for (Node j : nodeFam.getNodes()){
				if( !i.equals(j)){
					if ((i.getPossibilities().size() == 2 ) && (i.getPossibilities().equals(j.getPossibilities()))){
						removePossibilitiesByPairs(nodeFam,i,j);
					}
					else{
						ArrayList<Integer> values = new ArrayList<>();
						for (int val : nodeFam.getMissingValues()){
							ArrayList<Node> nodesWithPossibilityX = nodeFam.nodesWithPossibilityX(val);
							if (nodesWithPossibilityX.size() == 2){
								if ((nodesWithPossibilityX.get(0).equals(i) || nodesWithPossibilityX.get(0).equals(j)) && (nodesWithPossibilityX.get(1).equals(i) || nodesWithPossibilityX.get(1).equals(j))){
									values.add(val);
								}
							}
							if (values.size() == 2){
								removePossibilitiesByHiddenPairs(nodeFam, i, j, values.get(0), values.get(1));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * this method inspects a given node family for three nodes that have the same three possibilities, or a subset of
	 * the three possibilities. it then removes those three possibilities from every other node in the node family
	 * @param nodeFam
	 */

	private void findTriplets(NodeFamily nodeFam){
		for (Node i : nodeFam.getNodes()){
			if (i.getPossibilities().size() == 3 || i.getPossibilities().size() == 2) {
				for (Node j : nodeFam.getNodes()) {
					if (!i.equals(j)) {
						if ((j.getPossibilities().size() == 3 )|| (j.getPossibilities().size() == 2)) {
							for (Node k : nodeFam.getNodes()) {
								if (((k.getPossibilities().size() == 3 )|| (k.getPossibilities().size() == 2)) && (!k.equals(i)) && (!k.equals(j))) {
									ArrayList<Integer> possibilities = new ArrayList<>();
									possibilities.addAll(i.getPossibilities());
									possibilities.addAll(j.getPossibilities());
									possibilities.addAll(k.getPossibilities());
									possibilities = removeDuplicates(possibilities);
									if (possibilities.size() == 3) {
										removePossibilitiesByTriplets(nodeFam, i, j, k, possibilities);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private ArrayList<Integer> removeDuplicates(ArrayList<Integer> list){
		Set<Integer> set = new LinkedHashSet<>();
		set.addAll(list);
		list.clear();
		list.addAll(set);
		return list;
	}


	/**
	 * this method inspects a given node family for Quadruplets of nodes that have the same four possibilities. it then removes
	 * those four possibilities from every other node in the node family
	 * @param nodeFam
	 */
	private void findQuadruplets(NodeFamily nodeFam){
		for (Node i : nodeFam.getNodes()){
			for (Node j : nodeFam.getNodes()){
				if( !i.equals(j)){
					if ((i.getPossibilities().size() == 4 ) && (i.getPossibilities().equals(j.getPossibilities()))){
						for (Node k : nodeFam.getNodes()){
							if (((k.getPossibilities().equals(i.getPossibilities())))&&(!k.equals(i))&&(!k.equals(j))){
								for (Node l : nodeFam.getNodes()){
									if (((l.getPossibilities().equals(i.getPossibilities())))&&(!l.equals(i))&&(!l.equals(j))&&(!l.equals(k))){
										removePossibilitiesByQuads(nodeFam, i, j , k, l);
									}
								}
							}
						}
					}
				}
			}
		}
	}


	/**
	 * this method takes two nodes and two values and removes all of possibilities of the two nodes that aren't one of the values.
	 * @param nodeFam
	 * @param nodeA
	 * @param nodeB
	 * @param valA
	 * @param valB
	 */
	private void removePossibilitiesByHiddenPairs(NodeFamily nodeFam, Node nodeA, Node nodeB, int valA, int valB){
		for (int i = 1; i < 10; i++){
			if( i != valA && i != valB){
				nodeA.removePossibility(i);
				nodeB.removePossibility(i);
			}
		}
	}

	/**
	 * this method takes the possibilities of the two input nodes and removes those possibilities from the rest of the nodes in the input node family
	 * @param nodeFam
	 * @param nodeA
	 * @param nodeB
	 */
	private void removePossibilitiesByPairs(NodeFamily nodeFam, Node nodeA, Node nodeB){
		int value1 = nodeA.getPossibilities().get(0);
		int value2 = nodeA.getPossibilities().get(1);

		for (Node node : nodeFam.getNodes()){
			if ((!node.equals(nodeA)&&(!node.equals(nodeB)))){
				node.removePossibility(value1);
				node.removePossibility(value2);
			}
		}
	}

	/**
	 * this method takes the possibilities of the three input nodes and removes them from the rest of the nodes in the input node family
	 * @param nodeFam
	 * @param nodeA
	 * @param nodeB
	 * @param nodeC
	 */
	private void removePossibilitiesByTriplets(NodeFamily nodeFam, Node nodeA, Node nodeB, Node nodeC, ArrayList<Integer> possibilities){

		for (Node node : nodeFam.getNodes()){
			if ((!node.equals(nodeA))&&(!node.equals(nodeB))&&(!node.equals(nodeC))){
				node.removePossibility(possibilities.get(0));
				node.removePossibility(possibilities.get(1));
				node.removePossibility(possibilities.get(2));
			}
		}

	}

	/**
	 * this method takes the possibilities of the four input nodes and removes them from the rest of the nodes in the input node family
	 * @param nodeFam
	 * @param nodeA
	 * @param nodeB
	 * @param nodeC
	 * @param nodeD
	 */
	private void removePossibilitiesByQuads(NodeFamily nodeFam, Node nodeA, Node nodeB, Node nodeC, Node nodeD){
		int value1 = nodeA.getPossibilities().get(0);
		int value2 = nodeA.getPossibilities().get(1);
		int value3 = nodeA.getPossibilities().get(2);
		int value4 = nodeA.getPossibilities().get(3);
		for (Node node : nodeFam.getNodes()){
			if ((!node.equals(nodeA))&&(!node.equals(nodeB))&&(!node.equals(nodeC))&&(!node.equals(nodeD))){
				node.removePossibility(value1);
				node.removePossibility(value2);
				node.removePossibility(value3);
				node.removePossibility(value4);
			}
		}
	}

	public void setValue(int value, int row, int col){
		_rows.get(row).getNode(col).set_value(value);
	}
	
	public NodeFamily getRow(int rowNumber){
		return _rows.get(rowNumber);
	}

	public NodeFamily getBox(int rowNumber){
		return _boxes.get(rowNumber);
	}
	
	
}
