package BKE.Game.Player;

import BKE.Game.IBoard;
import BKE.Helper.Vector2D;

public class NetworkPlayer implements IPlayer{

    String _userName;

    IBoard _board;

    @Override
    public void doMove() {
    }

    @Override
    public String getName() {
        return _userName;
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
    public Vector2D getNextMove() {
        return null;
    }

    public NetworkPlayer(String username) {
        _userName = username;
    }
}
