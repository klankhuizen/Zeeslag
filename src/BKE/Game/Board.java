package BKE.Game;

import BKE.Game.Player.Player;

public class Board implements IBoard {

    private final int boardWidth;
    private final int boardHeight;
    private char[][] board;
    private Player currentPlayer;
    private Player opponent;

    public Board(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.board = new char[boardWidth][boardHeight];
    }

    public boolean checkMove(int moveRow, int moveCol) {
        return ((moveRow < boardWidth) && (moveCol < boardHeight) && (moveRow >= 0) && (moveCol >= 0));
    }

    public boolean checkWin(Player player) {
        // row check voor win.
        if ((this.board[0][0] == player.getCharPlayer()) && (this.board[0][1] == player.getCharPlayer()) && (this.board[0][2] == player.getCharPlayer())) {
            return true;
        } else if ((this.board[1][0] == player.getCharPlayer()) && (this.board[1][1] == player.getCharPlayer()) && (this.board[1][2] == player.getCharPlayer())) {
            return true;
        } else if ((this.board[2][0] == player.getCharPlayer()) && (this.board[2][1] == player.getCharPlayer()) && (this.board[2][2] == player.getCharPlayer())) {
            return true;
        }
        // col check voor win.
        if ((this.board[0][0] == player.getCharPlayer()) && (this.board[1][0] == player.getCharPlayer()) && (this.board[2][0] == player.getCharPlayer())) {
            return true;
        } else if ((this.board[0][1] == player.getCharPlayer()) && (this.board[1][1] == player.getCharPlayer()) && (this.board[2][1] == player.getCharPlayer())) {
            return true;
        } else if ((this.board[0][2] == player.getCharPlayer()) && (this.board[1][2] == player.getCharPlayer()) && (this.board[2][2] == player.getCharPlayer())) {
            return true;
        }
        // dia rechts en links check voor win.
        if ((this.board[0][0] == player.getCharPlayer()) && (this.board[1][1] == player.getCharPlayer()) && (this.board[2][2] == player.getCharPlayer())) {
            return true;
        } else return (this.board[0][2] == player.getCharPlayer()) && (this.board[1][1] == player.getCharPlayer()) && (this.board[2][0] == player.getCharPlayer());
    }

    public boolean checkGameOver(Player player){
        return checkWin(player);
    }
    
    public boolean isGameOver() {
        // Controleerd of er iemand gewonnen heeft
        if (checkWin(this.currentPlayer) || checkWin(this.opponent)) {
            return true;
        }

        // Controleer op gelijkspel
        if (checkBoardFull()) {
            return true;
        }
        return false;
    }
    
    public void checkResult(Player player){
        if (checkWin(player)){
            System.out.println("Speler " + player.getName() + " heeft gewonnen");
        } else if (checkDraw(player)){
            System.out.println("Het is een gelijk spel geworden");
        }
    }

    // checkt of een positie vol is of niet. True -> leeg, false -> vol.
    public boolean checkPositionFull(int moveRow, int moveCol){
        return this.board[moveRow][moveCol] == 0;
    }

    public boolean checkBoardFull(){
        for(int row  = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++) {
                if (this.board[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isCellEmpty(int x, int y) {

        if (board[x][y] == 'a') {
            return true;
        }
        return false;
    }

    public boolean checkDraw(Player player){
        return (checkBoardFull() && !checkWin(player));
    }

    public void makeMove(Player player, int moveRow, int moveCol){
        if (checkMove(moveRow, moveCol)) {
            if (checkPositionFull(moveRow, moveCol)) {
                setChar(moveRow, moveCol, player);
            }
        }
    }

    // Een zet ongedaan maken
    public void undoMove(int x, int y) {
        // Doormiddel van isCellEmpty leest het af of het vak leeg is
        if (isCellEmpty(x, y)) {
            board[x][y] = 'a';
        }
    }
    
    public void printBoard(){
        for(int row = 0; row < board.length; row++){
            for(int col = 0; col < board[row].length; col++){
                System.out.print(this.board[row][col] + "\t");
            }
            System.out.println();
        }
    }

    public void resetBoard(){
        for(int row  = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++) {
                this.board[row][col] = 0;
            }
        }
    }

    public void deleteMove(int moreRow, int moveCol){
    }



    public void setBoard(char[][] board) {
        this.board = board;
    }
    public char[][] getBoard() {
        return this.board;
    }

    public void setChar(int moveRow, int moveCol, Player player) {this.board[moveRow][moveCol] = player.getCharPlayer();}

    public char getChar(int moveRow, int moveCol){
        return this.board[moveRow][moveCol];
    }

    public int getBoardWidth(){
        return this.boardWidth;
    }

    public int getBoardHeight(){
        return this.boardHeight;
    }
}
