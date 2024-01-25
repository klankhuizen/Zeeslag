package BKE.UI;

import BKE.Game.Player.IPlayer;

import java.io.Closeable;

public interface IUserInterface extends Closeable {

    public void Start();

    public void UpdateFields(IPlayer playerOne, IPlayer playerTwo);

    public void SendMessageToUser(String message);

}
