package BKE.Game.Player;

import BKE.Game.IBoard;

public class NetworkPlayer implements IPlayer{

    String _userName;

    @Override
    public void doMove() {
    }

    @Override
    public String getName() {
        return _userName;
    }

    @Override
    public void setBoard(IBoard board) {

    }

    public NetworkPlayer(String username) {
        _userName = username;
    }
}
