package BKE.Game.Player;
public class Player implements IPlayer {
    private int id;
    private String name;
    private char charPlayer;

    public Player(int id, String name, char charPlayer) {
        this.id = id;
        this.name = name;
        this.charPlayer = charPlayer;
    }


    public int getId(){
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getCharPlayer(){
        return this.charPlayer;
    }

    public void setCharPlayer(char charPlayer) {
        this.charPlayer = charPlayer;
    }
}
