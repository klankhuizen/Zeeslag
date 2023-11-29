package BKE.Game.Variants;

import BKE.ApplicationState;
import BKE.Game.Board;
import BKE.Game.IBoard;
import BKE.Game.IGame;

import java.io.IOException;

public class Zeeslag implements IGame {

    private ApplicationState _state;
    private IBoard _board;

    @Override
    public void start() {
        System.out.println("Starting Zeeslag");
    }

    @Override
    public void initialize() {
        System.out.println("Initializing Zeeslag");

        // zet bord op
        _board = new Board(8, 8);
        _state = ApplicationState.RUNNING;
    }

    @Override
    public void HandleInput(String input) {
        // Hier komt the user-input binnen
        System.out.println("Zeeslag: " + input);
    }

    @Override
    public ApplicationState GetState() {
        return _state;
    }

    @Override
    public void SetState(ApplicationState state) {
        _state = state;
    }

    @Override
    public IBoard GetBoard() {
        return _board;
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closing Zeeslag");
        _state = ApplicationState.HALTED;
    }

    public Zeeslag() {}
}
