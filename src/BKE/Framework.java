package BKE;

import BKE.Game.IGame;
import BKE.Game.Variants.TicTacToe;
import BKE.Game.Variants.Zeeslag;
import BKE.Network.INetworkClient;
import BKE.UI.ConsoleUserInterface;
import BKE.UI.GraphicalUserInterface;
import BKE.UI.IUserInterface;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public final class Framework {


    private static final Framework _instance = new Framework();

    private static IGame _currentGame;

    private static HashMap<String, Type> _availableGames;

    private static final ArrayList<IUserInterface> userInterfaces = new ArrayList<>();

    private static Thread _gameThread;

    private static INetworkClient _networkedClient;

    private static boolean _isRunning = false;

    private Framework(){
        _availableGames = new HashMap<>();
        _availableGames.put("Tic Tac Toe", TicTacToe.class);
        _availableGames.put("Zeeslag", Zeeslag.class);

    }

    public static IGame GetCurrentGame(){
        return _currentGame;
    }

    public static void UnloadCurrentGame() throws IOException {

        if (_currentGame == null) return;

        _currentGame.close();
        _currentGame = null;

        for (IUserInterface userInterface : userInterfaces) {
            userInterface.close();
        }
    }

    public static HashMap<String, Type> GetAvailableGames(){

        return _availableGames;

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

        CreateGraphicalUI();
        CreateConsoleUI();

        return _gameThread;
    }

    public static void LoadGame(Type game, boolean networked) throws IllegalArgumentException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        // load the game

        if (_currentGame != null){
            _currentGame.close();
        }

        // find supported games

        Set<String> keys = _availableGames.keySet();

        for (String k : keys){
            Type t = _availableGames.get(k);

            if (t == game){
                _currentGame = (IGame) Class.forName(t.getTypeName()).getDeclaredConstructor().newInstance();
            }
        }

        _currentGame.initialize();
        _currentGame.start();

        for (IUserInterface uInf : userInterfaces){
            new Thread(uInf::Start).start();
        }


    }

    private static void CreateConsoleUI(){
        for (IUserInterface userInterface : userInterfaces){
            if (userInterface instanceof ConsoleUserInterface){
                return;
            }
        }

        IUserInterface iFace = new ConsoleUserInterface();
        userInterfaces.add(iFace);
        iFace.Start();
    }

    private static void CreateGraphicalUI(){

        for (IUserInterface userInterface : userInterfaces){
            if (userInterface instanceof GraphicalUserInterface){
                return;
            }
        }

        IUserInterface iFace = new GraphicalUserInterface();
        userInterfaces.add(iFace);
        iFace.Start();
    }

    public static void UpdateUI(int[][] playerOne, int[][] playerTwo){
        for (IUserInterface userInterface : userInterfaces) {
            userInterface.UpdateFields(playerOne, playerTwo);
        }
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

    public static void SendMessageToUser(String message){
        for (IUserInterface userInterface : userInterfaces) {
           userInterface.SendMessageToUser(message);
        }
    }
}
