package BKE.Game.Variants;

import BKE.ApplicationState;
import BKE.Game.IBoard;
import BKE.Game.IGame;
import BKE.Game.Player.IPlayer;
import BKE.Helper.MatchStats;
import BKE.Network.Message.GameResultMessage;
import BKE.Network.Message.MoveMessage;

import java.io.IOException;

public class TicTacToe implements IGame {
    @Override
    public void start(String pl) {

    }

    @Override
    public void initialize(IPlayer playerone, IPlayer playertwo, boolean networked) {

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
    public void doTurn(String playerName) {

    }

    @Override
    public void move(MoveMessage msg) {

    }

    @Override
    public void setGameResult(GameResultMessage gsm) {

    }

    @Override
    public IPlayer getPlayerOne() {
        return null;
    }

    @Override
    public IPlayer getPlayerTwo() {
        return null;
    }

    @Override
    public MatchStats getMatchStats() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
