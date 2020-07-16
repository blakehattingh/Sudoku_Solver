package sample;
import java.util.ArrayList;
import java.util.Arrays;

public class Node {

	private static int _id = 0 ;
	private final int _nodeId;
	private int _value = 0;
	private final int _rowNumber;
	private final int _colNumber;
	private final int _boxNumber;
	private ArrayList<Integer> _possibilities = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
	private NodeFamily _row;
	private NodeFamily _col;
	private NodeFamily _box;
	public boolean hasBeenAssigned = false;

	
	
	public Node() {
		
		_nodeId = _id++;
		_rowNumber = (_nodeId / 9);
		_colNumber = (_nodeId % 9);
		_boxNumber = ((_rowNumber/ 3) * 3) + (_colNumber / 3);
		
		
	}
	
	
	public ArrayList<Integer> getPossibilities(){
		return _possibilities;
	}
	
	public int getRow() {
		return _rowNumber;
	}
	
	public int getCol() {
		return _colNumber;
	}
	public int getBox() {
		return _boxNumber;
	}
	public int getId() {
		return _nodeId;
	}
	public NodeFamily get_row(){return _row; }
	public NodeFamily get_col(){return _col; }
	public NodeFamily get_box(){return _box; }

	public void setBox(NodeFamily box) {
		this._box = box;
	}

	public void setRow(NodeFamily row) {
		this._row = row;
	}

	public void setCol(NodeFamily col){
		this._col = col;
	}

	public void removePossibility(int value){
		if(_possibilities.contains(value)) {
			_possibilities.remove(_possibilities.indexOf(value));

		}
	}

	public void set_value(int value){

		_value = value;
		_row.addAValue(value);
		_col.addAValue(value);
		_box.addAValue(value);
		_possibilities = new ArrayList<Integer>(Arrays.asList(value));
		hasBeenAssigned = true;
		Main._dummyBoard[_colNumber][_rowNumber].setText(Integer.toString(value));

	}

	public boolean existsInPossibilities(int value){
		if (_possibilities.contains(value)){
			return true;
		} else return false;
	}

	public void printPossibilities(){
		System.out.println("");
		for (int i : _possibilities){
			System.out.print(i);
		}
	}
}
