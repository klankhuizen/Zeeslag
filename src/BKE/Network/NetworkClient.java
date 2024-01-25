package BKE.Network;

import BKE.Framework;
import BKE.Game.Player.NetworkPlayer;
import BKE.Game.Player.ZeeslagAIPlayer;
import BKE.Game.Variants.Zeeslag;
import BKE.Helper.ServerDataDecoder;
import BKE.Network.Message.MoveMessage;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkClient implements INetworkClient {

    /**
     * Timeout for commands
     */
    private static final int TIMEOUT = 1000;

    /**
     * Time the last command was requested
     */
    private long _requestTime = 0;

    /**
     * Socket thread
     */
    private Thread _socketThread;

    private Thread _handleThread;

    /**
     * Socket
     */
    private Socket _socket;

    /**
     * The reader for data coming back from the socket
     */
    private BufferedReader _in;

    /**
     * The writer for the data going to the socket
     */
    private BufferedWriter _out;

    /**
     * If waiting for a response
     */
    private boolean _waitingForResponse = false;

    /**
     * If waiting for an incoming response
     */
    private boolean _incomingResponse = false;
    /**
     * If it is expecting response
     */
    private boolean _isExpectingResponse = false;

    private String _userName = "";

    LinkedBlockingQueue<String[]> _queue = new LinkedBlockingQueue<>();

    private HashMap<String, Type> _supportedGames = new HashMap<>();

    /**
     * The buffer that will contain the response when it's ready.
     */
    private String[] _responseBuffer;

    private NetworkedGame _game;

    public NetworkClient() {
        _supportedGames.put("battleship", Zeeslag.class);
    }

    /**
     * Connect to a server
     * @param host the host ipv4
     * @param port the host port
     */
    public void connect(String host, int port){
        _socketThread = new Thread(this::run);
        _handleThread = new Thread(this::runQueue);
        try {
            _socket = new Socket(host, port);
            _out = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));
            _in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        _handleThread.start();
        _socketThread.start();
    }

    @Override
    public int status() {
        if (_socket == null) return 0;
        if (_socket.isConnected()) return 1;
        return 0;
    }

    @Override
    public void disconnect() throws IOException {
        _socket.shutdownInput();
        _socket.shutdownOutput();
        _socket.close();
        while (!_socket.isClosed()){
            // Wait
        }
        _out.close();
        _in.close();
        _out = null;
        _in = null;
        _socket = null;

        _socketThread.interrupt();
        _handleThread.interrupt();
    }

    @Override
    public void setUserName(String userName) {
        _userName = userName;
    }

    /**
     * Send a command
     * @param cmd The command
     * @return The response, if any
     * @throws IOException
     * @throws InterruptedException
     */
    public String[] send(NetworkCommand cmd) throws IOException, InterruptedException {
        _responseBuffer = null;
        int length = cmd.args.length;
        for (int i = 0; i < cmd.args.length; i++) {
            String arg = cmd.args[i];
            _out.write(arg);
            if (i < length - 1){
                _out.write(' ');
            }
        }

        _isExpectingResponse = cmd.expectsResponse();
        _out.write('\n');
        _waitingForResponse = true;
        _out.flush();
        System.out.println("C -> S :: " + String.join(" ", cmd.args));

        _requestTime = System.currentTimeMillis();
        while (_waitingForResponse){
            // block
            Thread.sleep(10);
            if (System.currentTimeMillis() - _requestTime > TIMEOUT){
                System.out.println("TIMEOUT WaitingForResponse");
                break;
            }
        }

        while (_incomingResponse){
            // block
            Thread.sleep(10);
            if (System.currentTimeMillis() - _requestTime > TIMEOUT){
                System.out.println("TIMEOUT IncomingResponse");
                break;
            }
        }
//
        while (_isExpectingResponse){
            Thread.sleep(10);
            if (System.currentTimeMillis() - _requestTime > TIMEOUT){
                System.out.println("TIMEOUT IsExpectingResponse");
                break;
            }
        }


        String[] response = new String[]{ "NO RESPONSE"};
        if (_responseBuffer != null){
            response = _responseBuffer.clone();
        }
        _isExpectingResponse = false;
        return response;
    }

    private void markReady() {
        _waitingForResponse = false;
        _isExpectingResponse = false;
        _incomingResponse = false;
    }

    private void runQueue(){
        try{
            while (true){
                String[] msg = _queue.poll();
                if (msg != null){
                    handleMessageFromServer(msg);
                }
                Thread.sleep(100);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    private void run() {
        try {
            String input;

            while (_socket.isConnected() && (input = _in.readLine()) != null) {
                System.out.println("C <- S :: " + input);
                String[] segments = input.split(" ");
                if (segments.length < 1) {
                    throw new RuntimeException("Received invalid input from server");
                }

                switch (segments[0]) {

                    case "OK":
                        Framework.SendMessageToUser("OK");
                        _waitingForResponse = false;
                        if (_isExpectingResponse) {
                            _incomingResponse = true;
                        }
                        break;
                    case "ERR":
                        StringBuilder sb = new StringBuilder();
                        for (String segment : segments) {
                            sb.append(segment).append(" ");
                        }
                        Framework.SendMessageToUser(sb.toString().trim());
                        _responseBuffer = segments.clone();
                        markReady();
                        break;
                    case "SVR":
                        _queue.offer(segments);
                        break;
                    default:
                        if (_incomingResponse) {
                            _responseBuffer = segments.clone();
                            markReady();
                        } else {
                            StringBuilder sb2 = new StringBuilder();
                            for (String segment : segments) {
                                sb2.append(segment).append(" ");
                            }
                            Framework.SendMessageToUser(sb2.toString().trim());
                            System.out.println(sb2.toString().trim());
                        }
                        break;
                }
            }

        } catch (SocketException se){
            if (se.getMessage().toLowerCase().contains("socket closed")){
                // Do nothing, the socket simply closed dummy.
                System.out.println("Socket closed.");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleMessageFromServer(String[] args){
        switch(args[1]){
            case "HELP":
                StringBuilder sb = new StringBuilder();
                for (String segment: args) {
                    sb.append(segment);
                }
                Framework.SendMessageToUser(sb.toString());
                break;
            case "GAME":
                handleMessageFromGame(args);
                break;
            case "GAMELIST":
                handleReceiveGameList(args);
                break;
            case "PLAYERLIST":
                handleReceivePlayerList(args);
                break;
            default:
                System.out.println("INVALID SVR MSG");
                break;
        }
    }

    private void setResponse(String[] buffer){
        _responseBuffer = buffer;
        markReady();
    }

    private void handleReceiveGameList(String[] args) {

        ServerDataDecoder.DecodeArray(args);
        String[] buffer = new String[args.length - 2];
        for (int i = 2; i < args.length; i++){
            buffer[i - 2] = args[i].replace("\"", "").replace("[", "").replace("]", "").replace(",", "");
        }
        setResponse(buffer);
    }

    private void handleReceivePlayerList(String[] args){
        String[] buffer = new String[args.length - 2];
        for (int i = 2; i < args.length; i++){
            buffer[i - 2] = args[i].replace("\"", "").replace("[", "").replace("]", "").replace(",", "");
        }
        setResponse(buffer);
    }

    private void handleMessageFromGame(String[] args){

        switch (args[2]){
            case "MATCH":
                startNetworkedGame(args);
                System.out.println("We got a match");
                break;
            case "YOURTURN":
                System.out.println("OUR TURN");
                Framework.GetCurrentGame().doTurn(_userName);
                break;
            case "MOVE":{
                Map<String, String> mapped = ServerDataDecoder.DecodeMap(args);
                MoveMessage msg = new MoveMessage(Integer.parseInt(mapped.get("MOVE")), mapped.get("PLAYER"), mapped.get("RESULT"));
                Framework.GetCurrentGame().move(msg);
                break;
            }

            case "CHALLENGE":
                System.out.println("Received a challenge");
                break;
            case "WIN":
            case "LOSS":
            case "DRAW":
                System.out.println("GAME RESULT: "  + args[2]);
                break;

            default:
                System.out.println("invalid mssage from game");
                break;
        }
    }

    private void startNetworkedGame(String[] args) {

        if (_game != null){
            try {
                _game.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            _game = null;
        }


        Map<String, String> data = ServerDataDecoder.DecodeMap(args);
        String gameName = data.get("GAMETYPE");

        Type game = _supportedGames.get(gameName.toLowerCase());

        if (game != null) {
            // Start a new game
            try {
                String playerStarting = data.get("PLAYERTOMOVE");
                String opponent = data.get("OPPONENT");

                _game = new NetworkedGame(new ZeeslagAIPlayer(_userName), new NetworkPlayer(opponent), gameName);
                Framework.LoadGame(game, _game._localPlayer, _game._remotePlayer, playerStarting, true);
//                String nm = _game._localPlayer.getName();
//                if (nm.equals(playerStarting)){
//                    _game._localPlayer.doMove();
//                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
