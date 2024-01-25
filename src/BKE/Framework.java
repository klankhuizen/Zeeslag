package BKE;

import BKE.Game.IGame;
import BKE.Game.Player.IPlayer;
import BKE.Game.Variants.TicTacToe;
import BKE.Game.Variants.Zeeslag;
import BKE.Network.Command.LoginCommand;
import BKE.Network.INetworkClient;
import BKE.Network.Message.GameResultMessage;
import BKE.Network.NetworkClient;
import BKE.Network.NetworkCommand;
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

    /**
     * The currently playing game.
     */
    private static IGame _currentGame;

    /**
     * The games that this framework supports.
     */

    private static HashMap<String, Type> _availableGames;

    /**
     * Supported user interfaces.
     */
    private static final ArrayList<IUserInterface> userInterfaces = new ArrayList<>();

    /**
     * Main thread.
     */
    private static Thread _gameThread;

    /**
     * Thread for incoming network messages.
     */
    private static Thread _networkThread;

    /**
     * Networking client handling the sending / receiving of network messages.
     */
    private static INetworkClient _networkedClient;

    /**
     * If the main thread is running.
     */
    private static boolean _mainThreadRunning = false;

    /**
     * Create a new instance.
     */
    private Framework(){
        _availableGames = new HashMap<>();
        _availableGames.put("Tic Tac Toe", TicTacToe.class);
        _availableGames.put("Zeeslag", Zeeslag.class);
    }

    /**
     * Gets the game currently being played or NULL if no game is currently active.
     * @return The current game or NULL
     */
    public static IGame GetCurrentGame(){
        return _currentGame;
    }

    /**
     * Unload the current game and reset.
     * @throws IOException if it could not close the game.
     */
    public static void UnloadCurrentGame() throws IOException {

        if (_currentGame == null) return;

        _currentGame.close();
        _currentGame = null;

        for (IUserInterface userInterface : userInterfaces) {
            userInterface.close();
        }
    }

    /**
     * Returns the games supported for this framework.
     * @return games.
     */

    public static HashMap<String, Type> GetAvailableGames(){
        return _availableGames;
    }

    /**
     * Start the main thread.
     * @return the thread
     */
    public static Thread Start(){
        _mainThreadRunning = true;
        _gameThread = new Thread(() -> {
            while (_mainThreadRunning){
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

        try {
            StartNetwork("127.0.0.1", 7789, "s" + System.currentTimeMillis());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

//        CreateConsoleUI();
        return _gameThread;
    }

    /**
     * Load a game
     * @param game The game TYPE
     * @param playerOne First player
     * @param playerTwo Second player
     * @param playerStarting Name of the player starting
     * @param networked true if one of the players is networked.
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
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
                break;
            }
        }

        if (_currentGame == null) throw new RuntimeException("Could not load game");

        _currentGame.initialize(playerOne, playerTwo, networked);
        _currentGame.start(playerStarting);
        for (IUserInterface uInf : userInterfaces){
            new Thread(uInf::Start).start();
        }
    }

    /**
     * Create console UI
     */
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

    /**
     * Create swing UI
     */
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

    /**
     * Send all UIs updated data in an ongoing match.
     * @param playerOne first player
     * @param playerTwo second player
     */
    public static void UpdateUI(IPlayer playerOne, IPlayer playerTwo){
        for (IUserInterface userInterface : userInterfaces) {
            userInterface.UpdateFields(playerOne, playerTwo);
        }
    }

    /**
     * Send all UIs updated data with match results.
     * @param gsm game result.
     */
    public static void UpdateUI(GameResultMessage gsm){
        for (IUserInterface userInterface : userInterfaces) {
            userInterface.setWinner(gsm);
        }
    }

    /**
     * Shut down the whole thing.
     */
    public static void Shutdown() {
        // Shutdown code here
        _mainThreadRunning = false;
        if (_currentGame != null){
            try {
                _currentGame.close();
            } catch (IOException e) {
                System.out.println("Error while shutting down.");
            }
        }
        System.out.println("Shutting down...");
    }

    /**
     * Send a message to the player in every user interface.
     * @param message the message.
     */
    public static void SendMessageToUser(String message){
        for (IUserInterface userInterface : userInterfaces) {
           userInterface.SendMessageToUser(message);
        }
    }

    /**
     * Stops the networking threads.
     * @throws IOException if stopping was not an option.
     */
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
    }

    /**
     * Start the networking thread and connect to a host.
     * @param host IPv4 Address
     * @param port port
     * @param userName The username that is used to connect to the service with. MUST BE UNIQUE.
     * @throws IOException network error
     * @throws InterruptedException the thread was shut down at some point.
     */
    public static void StartNetwork(String host, int port, String userName) throws IOException, InterruptedException {

        if (_networkThread != null && _networkThread.isAlive()){
            _networkThread.interrupt();
            _networkThread = null;
        }

        _networkThread = new Thread(() -> {

            try {
                if (_networkedClient != null && _networkedClient.status() == 1){
                    _networkedClient.disconnect();
                }

                _networkedClient = new NetworkClient();
                _networkedClient.connect(host, port);

                int timeoutCounter = 0;
                // Wait for a connection. Time out if nothing happens.
                while (_networkedClient.status() == 0 && timeoutCounter < 100){
                    Thread.sleep(100);
                    timeoutCounter ++ ;
                }

                if (timeoutCounter > 99){
                    throw new IOException("Could not connect to host.");
                }

                // Connected
                _networkedClient.send(new LoginCommand(userName));

                _networkedClient.setUserName(userName);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        _networkThread.start();
    }

    public static String[] sendNetworkMessage(NetworkCommand cmd) throws IOException, InterruptedException {
        if (_networkedClient != null && _networkedClient.status() == 1){
            return _networkedClient.send(cmd);
        }

        throw new IOException("Network not started");
    }

}
