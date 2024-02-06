package BKE;

import BKE.Game.IGame;
import BKE.Game.Player.IPlayer;
import BKE.Game.Variants.TicTacToe;
import BKE.Game.Variants.Zeeslag;
import BKE.Helper.Benchmarker;
import BKE.Network.Command.LoginCommand;
import BKE.Network.INetworkClient;
import BKE.Network.Message.GameResultMessage;
import BKE.Network.NetworkClient;
import BKE.Network.NetworkCommand;
import BKE.UI.ConsoleUserInterface;
import BKE.UI.GUI.NetworkPanel;
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

    private static boolean _isRunningBenchMarks = false;

    private static IGame _currentGame;

    private static HashMap<String, Type> _availableGames;

    private static final ArrayList<IUserInterface> userInterfaces = new ArrayList<>();

    private static Thread _gameThread;

    private static Thread _networkThread;

    private static INetworkClient _networkedClient;

    private static boolean _isRunning = false;

    private static NetworkPanel _networkPanel;

    private static Benchmarker _benchmarker;

    private static boolean _isnetworked;

    private Framework(){
        _availableGames = new HashMap<>();
        _availableGames.put("Tic Tac Toe", TicTacToe.class);
        _availableGames.put("Zeeslag", Zeeslag.class);
    }

    public static void startRunningBenchmarks(){
        _isRunningBenchMarks = true;
        _benchmarker = new Benchmarker();
        _benchmarker.initialize();
        _benchmarker.start();
    }

    public static void stopRunningBenchmarks(){
        _benchmarker.stop(false);
        _isRunningBenchMarks = false;
    }

    public static boolean isRunningBenchmarks() {
        return _isRunningBenchMarks;
    }

    public static boolean isNetworked(){
        return _isnetworked;
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

//        try {
//            StartNetwork("127.0.0.1", 7789, "s" + System.currentTimeMillis());
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//        CreateConsoleUI();
        return _gameThread;
    }

    public static void LoadGame(Type game, IPlayer playerOne, IPlayer playerTwo, String playerStarting, boolean networked) throws IllegalArgumentException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

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

        if (_currentGame == null) throw new RuntimeException("Could not load game");

        _currentGame.initialize(playerOne, playerTwo, networked);

        playerOne.setGame(_currentGame);
        playerTwo.setGame(_currentGame);

        _currentGame.start(playerStarting);

        if (isRunningBenchmarks()) return;

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

    public static void UpdateUI(IPlayer playerOne, IPlayer playerTwo){
        for (IUserInterface userInterface : userInterfaces) {
            userInterface.UpdateFields(playerOne, playerTwo);
        }
    }

    public static void UpdateUI(GameResultMessage gsm){
        for (IUserInterface userInterface : userInterfaces) {
            userInterface.setWinner(gsm);
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

    public static void StopNetwork() throws IOException {

        if (null == _networkThread){
            return;
        }

        if (!_networkThread.isAlive()){
            _networkThread = null;
        }

        if (_networkedClient != null){
            _networkedClient.disconnect();
        }

        if (_networkPanel != null){
            _networkPanel.close();
            _networkPanel = null;
        }

        _isnetworked = false;
    }

    public static void StartNetwork(String host, int port, String userName) throws IOException, InterruptedException {
        _isnetworked = true;
        if (_networkThread != null && _networkThread.isAlive()){
            _networkThread.interrupt();
            _networkThread = null;
        }

        _networkThread = new Thread(() -> {

            try {
                if (_networkedClient != null && _networkedClient.status() == 1){
                    _networkedClient.disconnect();
                }

                if (_networkPanel != null){
                    _networkPanel.close();
                    _networkPanel = null;
                }
                _networkedClient = new NetworkClient();
                _networkedClient.connect(host, port);
                while (_networkedClient.status() == 0){
                }

                // Connected
                _networkedClient.send(new LoginCommand(userName));

                _networkedClient.setUserName(userName);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        _networkThread.start();
//        _networkPanel = new NetworkPanel();
    }

    public boolean isNetworkRunning(){
        return _networkThread != null && _networkThread.isAlive();
    }

    public static String[] sendNetworkMessage(NetworkCommand cmd) throws IOException, InterruptedException {
        if (_networkedClient != null && _networkedClient.status() == 1){
            return _networkedClient.send(cmd);
        }

        throw new IOException("Network not started");
    }

}
