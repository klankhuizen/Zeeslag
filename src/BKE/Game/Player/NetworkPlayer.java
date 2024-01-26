package BKE.Game.Player;

import BKE.Game.IBoard;
import BKE.Game.IGame;
import BKE.Helper.Vector2D;

public class NetworkPlayer implements IPlayer{

    String _userName;

    IBoard _board;

    IGame _game;

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
    public boolean isRemote() {
        return true;
    }

    @Override
    public void setGame(IGame game) {
        _game = game;
    }

    public NetworkPlayer(String username) {
        _userName = username;
    }
}
