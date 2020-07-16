package sample;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<ActionEvent>  {

    private GridPane sudokuBoard;
    public static SingleNumberTextField[][] _dummyBoard = new SingleNumberTextField[9][9];
    private VBox vb;
    private ButtonBar buttonBar;
    private Button solveButton;
    private Button nextMoveButton;
    private SudokuBoard _game = new SudokuBoard();

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Sudoku Solver");
        //primaryStage.setScene(new Scene(root, 300, 300));

        sudokuBoard = new GridPane();
        sudokuBoard.setPadding(new Insets(10,10,10,10));
        sudokuBoard.setVgap(1);
        sudokuBoard.setHgap(1);
        sudokuBoard.setGridLinesVisible(true);
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9 ; j++){
                SingleNumberTextField cell = new SingleNumberTextField();
                SingleNumberTextField bindedCell = new SingleNumberTextField();
                GridPane.setConstraints(cell,i,j);
                sudokuBoard.getChildren().add(cell);
                _dummyBoard[i][j] = bindedCell;
                _dummyBoard[i][j].textProperty().bindBidirectional(cell.textProperty());
            }
        }

        vb = new VBox();
        buttonBar = new ButtonBar();
        solveButton = new Button("Solve");
        solveButton.setOnAction(this);
        nextMoveButton = new Button("Next Move");
        nextMoveButton.setOnAction(this);
        ButtonBar.setButtonData(solveButton, ButtonBar.ButtonData.RIGHT);
        ButtonBar.setButtonData(nextMoveButton, ButtonBar.ButtonData.RIGHT);
        buttonBar.getButtons().addAll(nextMoveButton,solveButton);

        vb.getChildren().addAll(sudokuBoard,buttonBar);


        Scene scene = new Scene(vb,300,300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void handle(ActionEvent event) {
        ObservableList<Node> children = sudokuBoard.getChildren();
        for (int i = 0; i < 9; i++ ){
            for (int j = 0; j < 9; j++){
                if ((!_dummyBoard[i][j].getText().isEmpty())&&(!_game.getRow(j).getNode(i).hasBeenAssigned)){
                    int val = Integer.parseInt(_dummyBoard[i][j].getText());
                    _game.setValue(val,j,i);
                    System.out.println("row:" + j + ", col: " + i + ", value = " + val);

                }
            }
        }

        _game.updatePossibilities();
        _game.findOnlyNodePossibleForAnyFamily();
        _game.findPointingPairs();
        _game.findClaimingPairs();
        _game.searchForQuads();
        _game.searchForTriplets();
        _game.searchForPairs();

        NodeFamily testRow = _game.getRow(0);
        sample.Node testNode0 = testRow.getNode(0);
        sample.Node testNode3 = testRow.getNode(3);
        sample.Node testNode2 = testRow.getNode(2);
        testNode0.printPossibilities();
        System.out.println("");
        testNode3.printPossibilities();
        System.out.println("");
        printCurrentState();
        //testNode2.printPossibilities();
       /* for (Node node : children){
            TextField temp = (TextField)node;
            if (!temp.getText().isEmpty()){
                int row = GridPane.getRowIndex(node);
                int col = GridPane.getColumnIndex(node);
                int val = Integer.parseInt(temp.getText());
                _game.setValue(val,row,col);
                System.out.println("row:" + row + ", col: " + col + ", value = " + val);
            }
        }*/
    }

    private void printCurrentState() {
        System.out.println("possibilities are:");
        for (int i = 0; i < 9; i++) {
           for(int j = 0; j < 9; j++){
               // System.out.println(_game.getBox(0).getNode(i).getPossibilities());
                _game.getRow(i).getNode(j).printPossibilities();
           }
        }
    }
}
