package BKE.Game.Variants;

import BKE.ApplicationState;
import BKE.Game.IBoard;
import BKE.Game.IGame;
import BKE.Game.Player.IPlayer;

import java.io.IOException;

public class TicTacToe implements IGame {
    @Override
    public void start() {

    }

    @Override
    public void initialize(IPlayer playerone, IPlayer playertwo) {

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
    public void RequestUpdate() {

    }

    @Override
    public boolean getIsNetworked() {
        return false;
    }

    @Override
    public String GetGameName() {
        return "TIC TAC TOE";
    }

    @Override
    public IPlayer getPlayer(String name) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
