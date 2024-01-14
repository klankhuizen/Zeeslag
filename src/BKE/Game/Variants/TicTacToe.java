package BKE.Game.Variants;

import BKE.ApplicationState;
import BKE.Game.IBoard;
import BKE.Game.IGame;

import java.io.IOException;

public class TicTacToe implements IGame {
    @Override
    public void start() {

    }

    @Override
    public void initialize() {
        System.out.println("<Insert TicTacToe here>");
    }

    @Override
    public void HandleInput(String input) {

    }

    @Override
    public ApplicationState GetState() {
        return null;
    }

    @Override
    public void SetState(ApplicationState state) {

    }

    @Override
    public IBoard GetPlayerBoard() {
        return null;
    }

    @Override
    public IBoard GetOpponentBoard() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
