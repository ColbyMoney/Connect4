import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;

public class ConnectFourGUI extends Application {
    // Create and initialize a cell
    private Cell[][] cell =  new Cell[6][7];

    // Create and initialize a status label
    private Label lblStatus = new Label("Player 1's turn (Red)");

    int turn = 1; //track whose turn it is
    int row = 0; //track row
    int column = 0; //track column
    boolean isOver = false; //turn true when game ends
    private ArrayList<Cell> winningPieces = new ArrayList<>();
    private ArrayList<Point> winningPiecesCoords = new ArrayList<>(); //give coordinates of winning pieces for the client classes to use

    public ArrayList<Point> getCoords() {return winningPiecesCoords;}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Pane to hold cell
        GridPane boardGUI = new GridPane();
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++)
                boardGUI.add(cell[i][j] = new Cell(i, j), j, i);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(boardGUI);
        borderPane.setBottom(lblStatus);

        Scene scene = new Scene(borderPane, 700, 600);
        primaryStage.setTitle("Connect 4: Game on!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //print out board
    public void printBoard(Cell[][] board)
    {
        System.out.println("1--2--3--4--5--6--7");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(board[i][j].getPlayer() + "  ");
            }
            System.out.println();
        }
    }

    /** Determine if the cell are all occupied */
    public boolean isFull(Cell[][] cell) {
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++)
                if (cell[i][j].getPlayer() == 0)
                    return false;

        return true;
    }

    /** Determine if the player with the specified token wins */
    public boolean didWin(int p, int row, int column, Cell[][] cell) {
        int count = 1; //count number of connected matching pieces
        int tempRow = row; //temp row for checking
        int tempColumn = column; //temp column for checking

        ArrayList<Cell> tempWinChecks = new ArrayList<>();
        boolean didWin = false;

        tempWinChecks.add(cell[row][column]);
        //vertical check
        for (tempRow = row; tempRow < 6 && cell[tempRow][tempColumn].getPlayer() == p; tempRow++) {
            if (tempRow != row || tempColumn != column) { //not the player-chosen spot
                count++;
                tempWinChecks.add(cell[tempRow][tempColumn]);
            }
        }
        if (count >= 4) { //4+ in a row
            for (int i = 0; i < tempWinChecks.size(); i++) {
                if (winningPieces.indexOf(tempWinChecks.get(i)) == -1) {
                    winningPieces.add(tempWinChecks.get(i));
                    winningPiecesCoords.add(new Point(tempWinChecks.get(i).getRow(), tempWinChecks.get(i).getColumn()));
                }
            }
            didWin = true;
        }
        //reset the checking/tracking variables
        count = 1;
        tempRow = row;
        tempWinChecks.clear();
        tempWinChecks.add(cell[row][column]);

        //horizontal check
        //check left side of spot chosen by player
        for (tempColumn = column; tempColumn >= 0 && cell[tempRow][tempColumn].getPlayer() == p; tempColumn--) {
            if (tempRow != row || tempColumn != column) { //not the player-chosen spot
                count++;
                tempWinChecks.add(cell[tempRow][tempColumn]);
            }
        }
        //check right side of spot chosen by player
        for (tempColumn = column; tempColumn < 7 && cell[tempRow][tempColumn].getPlayer() == p; tempColumn++) {
            if (tempRow != row || tempColumn != column) { //not the player-chosen spot
                count++;
                tempWinChecks.add(cell[tempRow][tempColumn]);
            }
        }
        if (count >= 4) { //4+ in a row
            for (int i = 0; i < tempWinChecks.size(); i++) {
                if (winningPieces.indexOf(tempWinChecks.get(i)) == -1) {
                    winningPieces.add(tempWinChecks.get(i));
                    winningPiecesCoords.add(new Point(tempWinChecks.get(i).getRow(), tempWinChecks.get(i).getColumn()));
                }
            }
            didWin = true;
        }
        //reset the checking/tracking variables
        count = 1;
        tempWinChecks.clear();
        tempWinChecks.add(cell[row][column]);

        //diagonal "/" check
        //check bottom left side of spot chosen by player
        for (tempRow = row, tempColumn = column; tempRow < 6 &&
                tempColumn >= 0 && cell[tempRow][tempColumn].getPlayer() == p; tempRow++, tempColumn--) {
            if (tempRow != row || tempColumn != column) { //not the player-chosen spot
                count++;
                tempWinChecks.add(cell[tempRow][tempColumn]);
            }
        }
        //check top right side of spot chosen by player
        for (tempRow = row, tempColumn = column; tempRow >= 0 &&
                tempColumn < 7 && cell[tempRow][tempColumn].getPlayer() == p; tempRow--, tempColumn++) {
            if (tempRow != row || tempColumn != column) { //not the player-chosen spot
                count++;
                tempWinChecks.add(cell[tempRow][tempColumn]);
            }
        }
        if (count >= 4) { //4+ in a row
            for (int i = 0; i < tempWinChecks.size(); i++) {
                if (winningPieces.indexOf(tempWinChecks.get(i)) == -1) {
                    winningPieces.add(tempWinChecks.get(i));
                    winningPiecesCoords.add(new Point(tempWinChecks.get(i).getRow(), tempWinChecks.get(i).getColumn()));
                }
            }
            didWin = true;
        }
        //reset the checking/tracking variables
        count = 1;
        tempWinChecks.clear();
        tempWinChecks.add(cell[row][column]);

        //diagonal "\" check
        //check top left side of spot chosen by player
        for (tempRow = row, tempColumn = column; tempRow >= 0 &&
                tempColumn >= 0 && cell[tempRow][tempColumn].getPlayer() == p; tempRow--, tempColumn--) {
            if (tempRow != row || tempColumn != column) { //not the player-chosen spot
                count++;
                tempWinChecks.add(cell[tempRow][tempColumn]);
            }
        }
        //check bottom right side of spot chosen by player
        for (tempRow = row, tempColumn = column; tempRow < 6 &&
                tempColumn < 7 && cell[tempRow][tempColumn].getPlayer() == p; tempRow++, tempColumn++) {
            if (tempRow != row || tempColumn != column) { //not the player-chosen spot
                count++;
                tempWinChecks.add(cell[tempRow][tempColumn]);
            }
        }
        if (count >= 4) { //4+ in a row
            for (int i = 0; i < tempWinChecks.size(); i++) {
                if (winningPieces.indexOf(tempWinChecks.get(i)) == -1) {
                    winningPieces.add(tempWinChecks.get(i));
                    winningPiecesCoords.add(new Point(tempWinChecks.get(i).getRow(), tempWinChecks.get(i).getColumn()));
                }
            }
            didWin = true;
        }

        //make all the pieces involved in a connect 4 flicker
        if (didWin == true) {
            new Thread(() -> {
                try {
                    flickerWin(winningPieces);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        }

        return didWin;
    }

    //make the pieces involved in a connect 4 flicker
    public void flickerWin(ArrayList<Cell> cell) throws InterruptedException {
        if (cell.get(0).getPlayer() == 1) {
            while (true) {
                for (int i = 0; i < cell.size(); i++) {
                    cell.get(i).ellipse.setFill(Color.rgb(255,255,0));
                }
                Thread.sleep(333);
                for (int i = 0; i < cell.size(); i++) {
                    cell.get(i).ellipse.setFill(Color.rgb(255,0,0));
                }
                Thread.sleep(333);
            }
        }
        else if (cell.get(0).getPlayer() == 2) {
            while (true) {
                for (int i = 0; i < cell.size(); i++) {
                    cell.get(i).getEllipse().setFill(Color.rgb(0,255,255));
                }
                Thread.sleep(333);
                for (int i = 0; i < cell.size(); i++) {
                    cell.get(i).getEllipse().setFill(Color.rgb(0,0,255));
                }
                Thread.sleep(333);
            }
        }
    }

    // An inner class for a cell
    public class Cell extends Pane {
        // Token used for this cell
        private int player = 0;
        private int r; //row for this cell
        private int c; //column for this cell
        private Ellipse ellipse;

        public Cell() {
            setStyle("-fx-border-color: black");
            this.setPrefSize(800, 800);
            this.setOnMouseClicked(e -> handleMouseClick());
        }

        public Cell(int row, int column) {
            r = row;
            c = column;
            setStyle("-fx-border-color: black");
            this.setPrefSize(800, 800);
            this.setOnMouseClicked(e -> handleMouseClick());
        }

        /** Return token */
        public int getPlayer() {
            return player;
        }

        /** getters */
        public int getRow() { return r; }
        public int getColumn() { return c; }
        public Ellipse getEllipse() {return ellipse;}

        /** Set a new token */
        public void setPlayer(int p) {
            player = p;

            //add a super cool ellipse
            ellipse = new Ellipse(this.getWidth() / 2,
                    this.getHeight() / 2, this.getWidth() / 2 - 10,
                    this.getHeight() / 2 - 10);
            ellipse.centerXProperty().bind(
                    this.widthProperty().divide(2));
            ellipse.centerYProperty().bind(
                    this.heightProperty().divide(2));
            ellipse.radiusXProperty().bind(
                    this.widthProperty().divide(2).subtract(10));
            ellipse.radiusYProperty().bind(
                    this.heightProperty().divide(2).subtract(10));
            ellipse.setStroke(Color.BLACK);
            if (turn == 1)
                ellipse.setFill(Color.RED);
            else if (turn == 2)
                ellipse.setFill(Color.BLUE);
            getChildren().add(ellipse);
        }

        /** Draw the line across the winning pieces wherever a win happens */
        public void drawLine(int x1, int y1, int x2, int y2) {
            System.out.println("drawLine " + x1 + "  " + y1 + "  " + x2 + "  " + y2);
            Line line = new Line(cell[x1][y1].getWidth() / 2, cell[x1][y1].getHeight() / 2,
                   cell[x2][y2].getWidth() / 2, cell[x2][y2].getHeight() / 2);

            line.startXProperty().bind(cell[x1][y1].widthProperty().divide(2));
            line.startYProperty().bind(cell[x1][y1].heightProperty().divide(2));

            line.endXProperty().bind(cell[x2][y2].widthProperty().divide(2));
            line.endYProperty().bind(cell[x2][y2].heightProperty().divide(2));

            // Add the lines to the pane
            cell[x1][y1].getChildren().add(line);
        }

        /* Handle a mouse click event */
        private void handleMouseClick() {
            // If selected column has an open spot and game is not over
            if (cell[0][this.c].getPlayer() == 0 && isOver == false) { //check if highest spot in the selected column is available
                row = this.r;
                column = this.c;
                for (int i = 5; i >= 0; i--) { //find the lowest open spot
                    if (cell[i][column].getPlayer() == 0) {
                        row = i;
                        break;
                    }
                }
                cell[row][column].setPlayer(turn);

                System.out.println("row: " + row + "   column: " + column);
                printBoard(cell);

                // Check game status
                if (didWin(turn, row, column, cell)) {
                    isOver = true;
                    lblStatus.setText("Player " + turn + " won! The game is over");
                }
                else if (isFull(cell)) {
                    isOver = true;
                    lblStatus.setText("Draw! The game is over");
                }
                else {
                    // Change the turn
                        if (turn == 1) {
                            turn = 2;
                            lblStatus.setText("Player " + turn + "'s turn (Blue)");
                        }
                        else {
                            turn = 1;
                            lblStatus.setText("Player " + turn + "'s turn (Red)");
                        }
                }
            }
        }
    }
}
