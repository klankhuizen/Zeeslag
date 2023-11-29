
package BKE.Game;

public class Game {
    private Board board;
    private Player[] players = new Player[2];
    private Player currentPlayer;
    private Player currentMove;

    private boolean isOver;
    public Game(){

    }
    public void start()
    {
        this.board = new Board(3,3);
        this.isOver = false;
        // Just heeft hier coinFlip, maar snap die methode niet
    }

    public void update(){
            for(Player p : this.players) {
                board.checkWin(p);
                if(p.getId() != this.currentMove.getId()){
                    this.currentMove = p;
                }
            }

        }

    public Player getPlayerByName(String name) {
        Player player = null;
        for(Player p : this.players)
        {
            if(p.getName().equalsIgnoreCase(name)) {
                player = p;
            } else {
                System.out.println("Unknown player: " + name );
            }
        }
        return player;
    }

    public Player getPlayerById(int id) {
        Player player = null;
        for(Player p : this.players) {
            if(p.getId() == id) {
                player = p;
            } else {
                System.out.println("No player found with ID: " + id);
            }
        }
        return player;
    }
    public Board getBoard(){
        return this.board;
    }

}
