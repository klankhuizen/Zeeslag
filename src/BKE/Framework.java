package BKE;

import BKE.Game.IGame;
import BKE.Game.Variants.TicTacToe;
import BKE.Game.Variants.Zeeslag;
import BKE.Network.INetworkClient;
import BKE.UI.ConsoleUserInterface;
import BKE.UI.GraphicalUserInterface;
import BKE.UI.IUserInterface;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;

public final class Framework {


    private static final Framework _instance = new Framework();

    private static IGame _currentGame;

    private static ArrayList<IGame> _availableGames;

    private static IUserInterface _userInterface;

    private static Thread _gameThread;

    private static INetworkClient _networkedClient;

    private static boolean _isRunning = false;

    private Framework(){
    }

    public static IGame GetCurrentGame(){
        return _currentGame;
    }

    public static void UnloadCurrentGame() throws IOException {
        _currentGame.close();
        _currentGame = null;
    }

    public static Thread Start(){
        _isRunning = true;
        _gameThread = new Thread(() -> {
            while (_isRunning){
                try {

                    if (_currentGame != null){
                        if (_currentGame.GetState() == ApplicationState.ERROR || _currentGame.GetState() == ApplicationState.HALTED){

                            _currentGame.close();
                            _currentGame = null;
                        }
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        _gameThread.start();
        GraphicalUserInterface s = new GraphicalUserInterface();
        s.Start();
        _userInterface = new ConsoleUserInterface();
        _userInterface.Start();

        return _gameThread;
    }

    public static void LoadGame(Type game, boolean networked) throws IllegalArgumentException, IOException {

        // load the game

        if (_currentGame != null){
            _currentGame.close();
        }

        switch( game.getTypeName() ){

            case "BKE.Game.Variants.Zeeslag":
                _currentGame = new Zeeslag();
                break;

            case "BKE.Game.Variants.TicTacToe":
                _currentGame = new TicTacToe();
                break;

            default:
                throw new IllegalArgumentException("Undefined game: " + game.getTypeName() );
        }


        _currentGame.initialize();
        _currentGame.start();
    }

    public static void Shutdown() {
        // Shutdown code here
        _isRunning = false;
        if (_currentGame != null){
            try {
                _currentGame.close();

            } catch (IOException e) {
                System.out.println("Error while shutting down.");
            }
        }
        System.out.println("Shutting down...");
    }
}
