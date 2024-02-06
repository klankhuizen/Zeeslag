package BKE.Game.Player;

import BKE.Game.IBoard;
import BKE.Game.IGame;
import BKE.Helper.Vector2D;
import BKE.Network.Message.MoveMessage;

public interface IPlayer {
    void doMove();
    String getName();
    void setBoard(IBoard board);
    IBoard getBoard();
    boolean isRemote();
    void setNextMove(int x, int y);
    void setGame(IGame game);
    void setMoveResult(MoveMessage msg);
}
