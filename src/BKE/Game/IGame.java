package BKE.Game;

import BKE.ApplicationState;

import java.io.Closeable;

public interface IGame extends Closeable {

    /**
     * This will start the game
     */
    public void start();

    /**
     * This will set up the game
     */
    public void initialize();

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

}
