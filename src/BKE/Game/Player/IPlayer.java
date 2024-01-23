package BKE.Game.Player;

import BKE.Game.IBoard;

public interface IPlayer {

    void doMove();

    String getName();

    void setBoard(IBoard board);
}
