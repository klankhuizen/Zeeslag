package BKE;

import BKE.Game.IGame;
import BKE.Network.INetworkClient;
import BKE.UI.ConsoleUserInterface;
import BKE.UI.IUserInterface;

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

    public static Thread Start(){
        _isRunning = true;
        _gameThread = new Thread(() -> {
            while (_isRunning){
                try {


                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        _gameThread.start();

        _userInterface = new ConsoleUserInterface();

        _userInterface.Start();

        return _gameThread;
    }

    public static void Shutdown() {
        // Shutdown code here
        _isRunning = false;
        System.out.println("Shutting down...");
    }
}
