import java.util.Scanner;

public class ConnectFour {
    private int[][] board = new int[6][7]; //create board 6 slots tall and 7 slots wide
    private int player = 1; //determine which player's turn it is, player 1 goes first
    private int row = 0; //row that gets assigned to the lowest open spot
    private int column = 0; //column that players select on their turn
    private boolean didWin = false; //variable didWin to avoid unnecessary method calls
    private boolean isFull = false; //variable isFull to avoid unnecessary method calls
    private Scanner input = new Scanner(System.in);

    //create GUI board object to use
    //ConnectFourGUI con4GUI = new ConnectFourGUI();

    public int[][] getBoard() {
        return board;
    }
    public void setBoard(int i, int j, int player) {
        board[i][j] = player;
    }
    public int getPlayer() {
        return player;
    }
    public void setPlayer(int player) {
        this.player = player;
    }
    public boolean getDidWin() {
        return didWin;
    }
    public void setDidWin(boolean didWin) {
        this.didWin = didWin;
    }
    public boolean getIsFull() {
        return isFull;
    }
    public void setIsFull(boolean isFull) {
        this.isFull = isFull;
    }

    //red will be 1 and blue will be 2 on the board

    //default constructor
    public ConnectFour() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = 0;
            }
        }
    }

    public static void main(String[] args)
    {
        ConnectFour con4 = new ConnectFour();
        con4.toString(con4.getBoard());
        while (con4.getDidWin() == false) {
            con4.takeTurn(con4.getPlayer());
        }
        System.out.println("Player " + con4.getPlayer() + " wins!");
    }

    //play a game of connect 4
    public void playGame() {
        toString(getBoard());
        while (getDidWin() == false) {
            takeTurn(getPlayer());
            //set value in the boardGUI here
            //ConnectFourGUI.setBoardGUI(row, column, player);
        }
        System.out.println("Player " + player + " wins!");
    }

    //print out board
    public void toString(int[][] board)
    {
        System.out.println("1--2--3--4--5--6--7");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(board[i][j] + "  ");
            }
            System.out.println();
        }
    }

    //let a player take a turn, switch players after each turn
    public void takeTurn(int player) {
            System.out.println("Player " + player + ": Enter the column, between 1 and 7 inclusive: ");
            column = input.nextInt() - 1;

            //invalid column or column is full
            while ((column < 0 || column > 6 || board[0][column] != 0)) {
                System.out.println("Oops! Column is either full or invalid, try a different column.");
                System.out.println("Enter the column, between 0 and 6 inclusive: ");
                column = input.nextInt();
            }

            //determine the lowest open spot
            for (int i = 5; i >= 0; i--) {
                if (board[i][column] == 0) {
                    row = i;
                    break;
                }
            }

            //set selected spot equal to the player that chose it
            board[row][column] = player;
            //same thing but on the GUI
            //con4GUI.setBoardGUI(row, column, player);

            //print board
            toString(board);

            //win check
            setDidWin(didWin(board, player));

            //full board check
            setIsFull(isFull(board));

            //switch to other player if nobody has won yet and board has open spots
            if (didWin == false && isFull == false) {
                if (player == 1)
                    setPlayer(2);
                else
                    setPlayer(1);
            }
    }

    //check for a connect 4
    public boolean didWin(int[][] board, int player) {
        int count = 1; //count number of connected matching pieces
        int tempRow = row; //temp row for checking
        int tempColumn = column; //temp column for checking

        //vertical check
        for (tempRow = row; tempRow < 6 && board[tempRow][tempColumn] == player; tempRow++) {
            if (tempRow != row || tempColumn != column) //not the player-chosen spot
                count++;
        }
        if (count >= 4) //4+ in a row
            return true;
        //reset the checking/tracking variables
        count = 1;
        tempRow = row;

        //horizontal check
        //check left side of spot chosen by player
        for (tempColumn = column; tempColumn >= 0 && board[tempRow][tempColumn] == player; tempColumn--) {
            if (tempRow != row || tempColumn != column) //not the player-chosen spot
                count++;
        }
        //check right side of spot chosen by player
        for (tempColumn = column; tempColumn < 7 && board[tempRow][tempColumn] == player; tempColumn++) {
            if (tempRow != row || tempColumn != column) //not the player-chosen spot
                count++;
        }
        if (count >= 4) //4+ in a row
            return true;
        //reset the checking/tracking variables
        count = 1;

        //diagonal "/" check
        //check bottom left side of spot chosen by player
        for (tempRow = row, tempColumn = column; tempRow < 6 &&
                tempColumn >= 0 && board[tempRow][tempColumn] == player; tempRow++, tempColumn--) {
            if (tempRow != row || tempColumn != column) //not the player-chosen spot
                count++;
        }
        //check top right side of spot chosen by player
        for (tempRow = row, tempColumn = column; tempRow >= 0 &&
                tempColumn < 7 && board[tempRow][tempColumn] == player; tempRow--, tempColumn++) {
            if (tempRow != row || tempColumn != column) //not the player-chosen spot
                count++;
        }
        if (count >= 4) //4+ in a row
            return true;
        //reset the checking/tracking variables
        count = 1;

        //diagonal "\" check
        //check top left side of spot chosen by player
        for (tempRow = row, tempColumn = column; tempRow >= 0 &&
                tempColumn >= 0 && board[tempRow][tempColumn] == player; tempRow--, tempColumn--) {
            if (tempRow != row || tempColumn != column) //not the player-chosen spot
                count++;
        }
        //check bottom right side of spot chosen by player
        for (tempRow = row, tempColumn = column; tempRow < 6 &&
                tempColumn < 7 && board[tempRow][tempColumn] == player; tempRow++, tempColumn++) {
            if (tempRow != row || tempColumn != column) //not the player-chosen spot
                count++;
        }
        if (count >= 4) //4+ in a row
            return true;

        return false;
    }

    //check if board is full
    public boolean isFull(int[][] board) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (board[i][j] == 0)
                    return false;
            }
        }
        return true;
    }
}