package BKE.Game.Player;

import BKE.Game.IBoard;
import BKE.Helper.Vector2D;

public interface IPlayer {

    void doMove();

    String getName();

    void setBoard(IBoard board);
    IBoard getBoard();

    boolean isRemote();

}
