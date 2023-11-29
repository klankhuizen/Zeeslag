package BKE;

import BKE.Game.IGame;
import BKE.Network.INetworkClient;
import BKE.UI.IUserInterface;

import java.util.ArrayList;

public final class Framework {

    private static final Framework _instance = new Framework();

    private static IGame _currentGame;

    private static ArrayList<IGame> _availableGames;

    private static IUserInterface _userInterface;

    private static Runnable _gameThread;

    private static INetworkClient _networkedClient;

    private Framework(){
    }

    public static void Start(){

    }
}
