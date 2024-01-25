package BKE.Game.Player;

import BKE.Game.IBoard;
import BKE.Helper.Vector2D;

public interface IPlayer {
    /**
     * Do a move
     */

    void doMove();

    /**
     * Get the name of this player
     * @return Player Name
     */

    String getName();

    /**
     * Set the board for this player.
     * @param board
     */

    void setBoard(IBoard board);

    /**
     * Get the board for this player
     * @return board
     */
    IBoard getBoard();

    /**
     * Get whether this player is remote.
     * If remote, it is a networkplayer that is not on this local machine.
     * @return true if remote.
     */
    boolean isRemote();

}
