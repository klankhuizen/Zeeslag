package BKE.Game.Player;

import BKE.Game.IBoard;

public class ZeeslagAIPlayer implements IPlayer{
    private String _name = "Computer";
    @Override
    public void doMove() {

    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void setBoard(IBoard board) {

    }

    public ZeeslagAIPlayer(String userName) {
        _name = userName;
    }
}
