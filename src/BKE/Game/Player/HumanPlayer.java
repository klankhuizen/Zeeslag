package BKE.Game.Player;

import BKE.Game.IBoard;
import BKE.Game.IGame;
import BKE.Helper.Vector2D;

public class HumanPlayer implements IPlayer {
    private String _name;
    private IBoard _board;

    private boolean _holding;

    public HumanPlayer(String name){
        _name = name;
    }

    @Override
    public void doMove() {
        _holding = true;
        while (_holding){

        }
    }

    public String getName(){
        return _name;
    }

    @Override
    public void setBoard(IBoard board) {
        _board = board;
    }

    @Override
    public IBoard getBoard() {
        return _board;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public void setGame(IGame game) {

    }
}
