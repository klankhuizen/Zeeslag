package BKE.UI;

import BKE.Game.Player.IPlayer;
import BKE.Network.Message.GameResultMessage;

import java.io.Closeable;

public interface IUserInterface extends Closeable {

    /**
     * Start the interface.
     */
    public void Start();

    /**
     * Update the fields for player one and player two.
     * @param playerOne First player.
     * @param playerTwo Second player.
     */

    public void UpdateFields(IPlayer playerOne, IPlayer playerTwo);

    /**
     * Sends a message to the player
     * @param message The message for the player.
     */
    public void SendMessageToUser(String message);

    /**
     * Set the winner of the match in the UI.
     * @param gsm The result
     */
    public void setWinner(GameResultMessage gsm);

}
