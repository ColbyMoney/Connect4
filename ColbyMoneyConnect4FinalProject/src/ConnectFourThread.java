import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ConnectFourThread extends Thread implements Runnable, ConnectFourConstants {
    int threadID; //keep track of the threads

    public ConnectFourThread(int threadNumber) {
        threadID = threadNumber;
    }

    // create and start a thread for each connect 4 game
    public static void startNewGame(Socket player1, Socket player2, int numOfGames) throws IOException {
        // Create data input and output streams
        DataInputStream fromPlayer1 = new DataInputStream(
                player1.getInputStream());
        DataOutputStream toPlayer1 = new DataOutputStream(
                player1.getOutputStream());
        DataInputStream fromPlayer2 = new DataInputStream(
                player2.getInputStream());
        DataOutputStream toPlayer2 = new DataOutputStream(
                player2.getOutputStream());

        // create and start a thread for each connect 4 game
        for (int i = 0; i < numOfGames; i++) {
            Thread gameThread = new ConnectFourThread(i); {
                ConnectFourClient1 client1 = new ConnectFourClient1();
                ConnectFourClient2 client2 = new ConnectFourClient2();
                Platform.runLater(() -> {
                    client1.createGame();
                    client2.createGame();
                });
            }

            gameThread.start();
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

