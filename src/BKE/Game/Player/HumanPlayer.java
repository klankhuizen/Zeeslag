package BKE.Game.Player;

import BKE.Game.IBoard;

public class HumanPlayer implements IPlayer {
    private String _name;
    private IBoard _board;

    public HumanPlayer(String name){
        _name = name;
    }

    @Override
    public void doMove() {

    }

    public String getName(){
        return _name;
    }

    @Override
    public void setBoard(IBoard board) {
        _board = board;
    }
}
