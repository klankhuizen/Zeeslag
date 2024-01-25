package BKE.Game;

import BKE.ApplicationState;
import BKE.Game.Player.IPlayer;
import BKE.Network.Message.GameResultMessage;
import BKE.Network.Message.MoveMessage;

import java.io.Closeable;

public interface IGame extends Closeable {

    /**
     * This will start the game
     * @param playerStarting The name of the player to start.
     */
    public void start(String playerStarting);

    /**
     * This will set up the game
     * @param playerone player one
     * @param playertwo player two
     * @param isNetworked set to true if network game
     */
    public void initialize(IPlayer playerone, IPlayer playertwo, boolean isNetworked);

    /**
     * This will handle any input coming from the user interface
     * @param input The command to give to the game
     */
    public void HandleInput(String input);

    /**
     * Gets the current state of the game.
     * @return State
     */
    public ApplicationState GetState();

    /**
     * Set the current state of the game to something else
     * @param state State of game.
     */
    public void SetState(ApplicationState state);

    /**
     * Gets the game board
     * @return Board
     */
    public IBoard GetPlayerBoard();

    public IBoard GetOpponentBoard();


    public void RequestUpdate();

    /**
     * Returns whether or not the game is networked
     * @return
     *
     */
    boolean getIsNetworked();

    String GetGameName();

    IPlayer getPlayer(String name);

    void doTurn(String playerName);

    void move(MoveMessage msg);

    void setGameResult(GameResultMessage gsm);
}
