import javafx.application.Application;
import javafx.application.Platform;
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectFourClient1 extends ConnectFourGUI
        implements ConnectFourConstants {
    //win and game tracker
    private static int player1Wins = 0;
    private static int player2Wins = 0;
    private static int totalGames = 0;

    // Indicate whether the player has the turn
    private boolean myTurn = false;

    // Indicate the token for the player
    private int myToken = 0;

    // Indicate the token for the other player
    private int otherToken = 0;

    // Create and initialize cells
    private Cell[][] cell =  new Cell[6][7];

    // Create and initialize a title label
    private static Label lblTitle = new Label();

    // Create and initialize a status label
    private Label lblStatus = new Label();

    // Indicate selected row and column by the current move
    private int rowSelected;
    private int columnSelected;

    // Input and output streams from/to server
    private DataInputStream fromServer;
    private DataOutputStream toServer;

    // Continue to play?
    private boolean continueToPlay = true;

    // Wait for the player to mark a cell
    private boolean waiting = true;

    // Host name or ip
    private String host = "localhost";

    /*public ConnectFourClient1() {
        // Indicate whether the player has the turn
        myTurn = false;

        // Indicate the token for the player
        myToken = 0;

        // Indicate the token for the other player
        otherToken = 0;

        // Create and initialize cells
        cell =  new Cell[6][7];

        // Create and initialize a title label
        lblTitle = new Label();

        // Create and initialize a status label
        lblStatus = new Label();

        // Indicate selected row and column by the current move
        rowSelected = 0;
        columnSelected = 0;

        // Input and output streams from/to server
        DataInputStream fromServer;
        DataOutputStream toServer;

        // Continue to play?
        continueToPlay = true;

        // Wait for the player to mark a cell
        waiting = true;

        // Host name or ip
        host = "localhost";
    }*/

    /*public boolean getMyTurn() { return myTurn; }
    public void setMyTurn(boolean myTurn) { this.myTurn = myTurn; }
    public int getMyToken() {return myToken;}
    public void setMyToken(int myToken) {this.myToken = myToken;}
    public int getOtherToken() {return otherToken;}
    public void setOtherToken(int otherToken) {this.otherToken = otherToken;}
    public Cell[][] getCell() {return cell;}*/

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        // Pane to hold cell
        GridPane boardGUI = new GridPane();
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++)
                boardGUI.add(cell[i][j] = new Cell(i, j), j, i);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(lblTitle);
        borderPane.setCenter(boardGUI);
        borderPane.setBottom(lblStatus);

        // Create a scene and place it in the stage
        Scene scene = new Scene(borderPane, 700, 600);
        primaryStage.setTitle("Connect Four: Game on! (ConnectFourClient1)"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        //connect to the server
        connectToServer();
    }

    //create the board for each instance of a new game
    public void createGame() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            // Pane to hold cell
            GridPane boardGUI = new GridPane();
            for (int i = 0; i < 6; i++)
                for (int j = 0; j < 7; j++)
                    boardGUI.add(cell[i][j] = new Cell(i, j), j, i);

            BorderPane borderPane = new BorderPane();
            borderPane.setTop(lblTitle);
            borderPane.setCenter(boardGUI);
            borderPane.setBottom(lblStatus);

            // Create a scene and place it in the stage
            Scene scene = new Scene(borderPane, 700, 600);
            stage.setTitle("Connect Four: Game on! (ConnectFourClient1)"); // Set the stage title
            stage.setScene(scene); // Place the scene in the stage
            stage.show(); // Display the stage
        });
    }

    protected void connectToServer() {
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket(host, 8000);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        // Control the game on a separate thread
        new Thread(() -> {
            try {
                // Get notification from the server
                int player = fromServer.readInt();

                // Am I player 1 or 2?
                if (player == PLAYER1) {
                    myToken = PLAYER1;
                    otherToken = PLAYER2;
                    Platform.runLater(() -> {
                        if (totalGames > 0)
                            lblTitle.setText("Player 1 as Red     Total Games Played: " + totalGames + "     Total Wins: " + player1Wins +
                                    "     Win Rate: " + ((player1Wins * 100) / totalGames) + "%");
                        else
                            lblTitle.setText("Player 1 as Red     Total Games Played: " + totalGames + "     Total Wins: " + player1Wins +
                                    "     Win Rate: 0%");
                        lblStatus.setText("Waiting for player 2 to join");
                    });

                    // Receive startup notification from the server
                    fromServer.readInt(); // Whatever read is ignored

                    // The other player has joined
                    Platform.runLater(() ->
                            lblStatus.setText("Player 2 has joined. I start first"));

                    // It is my turn
                    myTurn = true;
                }
                else if (player == PLAYER2) {
                    myToken = PLAYER2;
                    otherToken = PLAYER1;
                    Platform.runLater(() -> {
                        if (totalGames > 0)
                            lblTitle.setText("Player 2 as Blue     Total Games Played: " + totalGames + "     Total Wins: " + player2Wins +
                                    "     Win Rate: " + ((player2Wins * 100) / totalGames) + "%");
                        else
                            lblTitle.setText("Player 2 as Blue     Total Games Played: " + totalGames + "     Total Wins: " + player2Wins +
                                    "     Win Rate: 0%");
                        lblStatus.setText("Waiting for player 1 to move");
                    });
                }

                // Continue to play
                while (continueToPlay) {
                    if (player == PLAYER1) {
                        waitForPlayerAction(); // Wait for player 1 to move
                        sendMove(); // Send the move to the server
                        receiveInfoFromServer(); // Receive info from the server
                    }
                    else if (player == PLAYER2) {
                        receiveInfoFromServer(); // Receive info from the server
                        waitForPlayerAction(); // Wait for player 2 to move
                        sendMove(); // Send player 2's move to the server
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /** Wait for the player to mark a cell */
    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }

        waiting = true;
    }

    /** Send this player's move to the server */
    private void sendMove() throws IOException {
        toServer.writeInt(rowSelected); // Send the selected row
        toServer.writeInt(columnSelected); // Send the selected column
        //System.out.println("row: " + rowSelected + "  column: " + columnSelected);
    }

    /** Receive info from the server */
    private void receiveInfoFromServer() throws IOException, InterruptedException {
        // Receive game status
        int status = fromServer.readInt();
        ArrayList<Point> coords = new ArrayList<>();
        coords = getCoords();

        if (status == PLAYER1_WON) {
            player1Wins++; //increment player 1 wins
            totalGames++; //increment total games played

            ArrayList<Point> finalCoords1 = coords;
            new Thread(() -> {
                try {
                    flickerWinHere(finalCoords1, PLAYER2, cell);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();

            // Player 1 won, stop playing
            continueToPlay = false;
            if (myToken == PLAYER1) {
                Platform.runLater(() -> {
                    lblStatus.setText("I won! (Red)");
                    if (totalGames > 0)
                        lblTitle.setText("Player 1 as Red     Total Games Played: " + totalGames + "     Total Wins: " + player1Wins +
                                "     Win Rate: " + ((player1Wins * 100) / totalGames) + "%");
                    else
                        lblTitle.setText("Player 1 as Red     Total Games Played: " + totalGames + "     Total Wins: " + player1Wins +
                                "     Win Rate: 0%");
                });
            }
            else if (myToken == PLAYER2) {
                Platform.runLater(() -> {
                    lblStatus.setText("Player 1 (Red) has won!");
                    if (totalGames > 0)
                        lblTitle.setText("Player 2 as Blue     Total Games Played: " + totalGames + "     Total Wins: " + player2Wins +
                                "     Win Rate: " + ((player2Wins * 100) / totalGames) + "%");
                    else
                        lblTitle.setText("Player 2 as Blue     Total Games Played: " + totalGames + "     Total Wins: " + player2Wins +
                                "     Win Rate: 0%");
                });
                receiveMove();
            }
        }
        else if (status == PLAYER2_WON) {
            player2Wins++; //increment player 2 wins
            totalGames++; //increment total games played

            ArrayList<Point> finalCoords = coords;
            new Thread(() -> {
                try {
                    flickerWinHere(finalCoords, PLAYER2, cell);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();

            // Player 2 won, stop playing
            continueToPlay = false;
            if (myToken == PLAYER2) {
                Platform.runLater(() -> {
                    lblStatus.setText("I won! (Blue)");
                    if (totalGames > 0)
                        lblTitle.setText("Player 2 as Blue     Total Games Played: " + totalGames + "     Total Wins: " + player2Wins +
                                "     Win Rate: " + ((player2Wins * 100) / totalGames) + "%");
                    else
                        lblTitle.setText("Player 2 as Blue     Total Games Played: " + totalGames + "     Total Wins: " + player2Wins +
                                "     Win Rate: 0%");
                });
            }
            else if (myToken == PLAYER1) {
                Platform.runLater(() -> {
                    lblStatus.setText("Player 2 (Blue) has won!");
                    if (totalGames > 0)
                        lblTitle.setText("Player 1 as Red     Total Games Played: " + totalGames + "     Total Wins: " + player1Wins +
                                "     Win Rate: " + ((player1Wins * 100) / totalGames) + "%");
                    else
                        lblTitle.setText("Player 1 as Red     Total Games Played: " + totalGames + "     Total Wins: " + player1Wins +
                                "     Win Rate: 0%");
                });
                receiveMove();
            }
        }
        else if (status == DRAW) {
            totalGames++; //increment total games played

            // No winner, game is over
            continueToPlay = false;
            Platform.runLater(() ->
                    lblStatus.setText("Game is over, no winner!"));

            if (myToken == PLAYER1) {
                receiveMove();
                Platform.runLater(() -> {
                    if (totalGames > 0)
                        lblTitle.setText("Player 1 as Red     Total Games Played: " + totalGames + "     Total Wins: " + player1Wins +
                                "     Win Rate: " + ((player1Wins * 100) / totalGames) + "%");
                    else
                        lblTitle.setText("Player 1 as Red     Total Games Played: " + totalGames + "     Total Wins: " + player1Wins +
                                "     Win Rate: 0%");
                });
            }
            else if (myToken == PLAYER2) {
                Platform.runLater(() -> {
                    if (totalGames > 0)
                        lblTitle.setText("Player 2 as Blue     Total Games Played: " + totalGames + "     Total Wins: " + player2Wins +
                                "     Win Rate: " + ((player2Wins * 100) / totalGames) + "%");
                    else
                        lblTitle.setText("Player 2 as Blue     Total Games Played: " + totalGames + "     Total Wins: " + player2Wins +
                                "     Win Rate: 0%");
                });
            }
        }
        else {
            receiveMove();
            Platform.runLater(() -> lblStatus.setText("My turn"));
            myTurn = true; // It is my turn
        }
    }

    private void receiveMove() throws IOException {
        // Get the other player's move
        int row = fromServer.readInt();
        int column = fromServer.readInt();
        Platform.runLater(() -> cell[row][column].setPlayer(otherToken));
    }

    //make the pieces involved in a connect 4 flicker
    public void flickerWinHere(ArrayList<Point> coords, int player, Cell[][] cell) throws InterruptedException {
        if (player == 1) {
            while (true) {
                for (int i = 0; i < coords.size(); i++) {
                    cell[(int)coords.get(i).getX()][(int)coords.get(i).getY()].ellipse.setFill(Color.rgb(255,255,0));
                }
                Thread.sleep(333);
                for (int i = 0; i < coords.size(); i++) {
                    cell[(int)coords.get(i).getX()][(int)coords.get(i).getY()].ellipse.setFill(Color.rgb(255,0,0));
                }
                Thread.sleep(333);
            }
        }
        else if (player == 2) {
            while (true) {
                for (int i = 0; i < coords.size(); i++) {
                    cell[(int)coords.get(i).getX()][(int)coords.get(i).getY()].ellipse.setFill(Color.rgb(0,255,255));
                }
                Thread.sleep(333);
                for (int i = 0; i < coords.size(); i++) {
                    cell[(int)coords.get(i).getX()][(int)coords.get(i).getY()].ellipse.setFill(Color.rgb(0,0,255));
                }
                Thread.sleep(333);
            }
        }
    }

    // An inner class for a cell with added client/server functionality
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
            if (p == 1)
                ellipse.setFill(Color.RED);
            else if (p == 2)
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
            // If cell is not occupied and the player has the turn
            if (cell[0][this.c].getPlayer() == 0 && myTurn) { //check if highest spot in the selected column is available
                row = this.r;
                column = this.c;
                for (int i = 5; i >= 0; i--) { //find the lowest open spot
                    if (cell[i][column].getPlayer() == 0) {
                        row = i;
                        break;
                    }
                }
                cell[row][column].setPlayer(myToken);  // Set the player's token in the cell
                myTurn = false;
                rowSelected = row;
                columnSelected = column;
                lblStatus.setText("Waiting for the other player to move");
                waiting = false; // Just completed a successful move
            }
        }
    }

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
