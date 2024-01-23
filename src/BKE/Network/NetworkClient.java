package BKE.Network;

import BKE.Framework;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class NetworkClient implements INetworkClient {
    private Thread _socketThread;

    private Socket _socket;

    private BufferedReader _in;

    private BufferedWriter _out;

    private boolean _waitingForResponse = false;

    private boolean _incomingResponse = false;

    private boolean _isExpectingResponse = false;

    private String[] _responseBuffer;

    public NetworkClient() {

    }

    public void connect(String host, int port){
        _socketThread = new Thread(this::run);
        try {
            _socket = new Socket(host, port);
            _out = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));
            _in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    }

    public String[] send(NetworkCommand cmd) throws IOException {
        if (_waitingForResponse){
            throw new RuntimeException( "Sending commands too quick, awaiting server response.");
        }

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
        _waitingForResponse = true;
        _out.write('\n');
        _out.flush();

        while (_waitingForResponse){
            // block
        }

        while (_incomingResponse){
            // block
        }

        while (_isExpectingResponse){
        }
        String[] response = new String[]{ "NO RESPONSE"};
        if (_responseBuffer != null){
            response = _responseBuffer.clone();
        }

        return response;
    }

    private void markReady() {
        _waitingForResponse = false;
        _isExpectingResponse = false;
        _incomingResponse = false;
    }


    private void run() {
        try {
           String input;

           while( _socket.isConnected() && (input = _in.readLine()) != null){
               String[] segments = input.split(" ");
               if (segments.length < 1){
                   throw new RuntimeException("Received invalid input from server");
               }

               switch (segments[0]){
                   case "OK":
                       System.out.println("OK");
                       Framework.SendMessageToUser("OK");
                       _waitingForResponse = false;
                       if (_isExpectingResponse){
                           _incomingResponse = true;
                       }
                       break;
                   case "ERR":
                       StringBuilder sb = new StringBuilder();
                       for (String segment: segments) {
                           sb.append(segment).append(" ");
                       }
                       Framework.SendMessageToUser(sb.toString().trim());
                       _responseBuffer = segments.clone();
                       markReady();
                       break;
                   case "SVR":
                       handleMessageFromServer(segments);
                       break;
                   default:
                       if (_incomingResponse){
                           _responseBuffer = segments.clone();
                           markReady();
                       } else {
                           StringBuilder sb2 = new StringBuilder();
                           for (String segment: segments) {
                               sb2.append(segment).append(" ");
                           }
                           Framework.SendMessageToUser(sb2.toString().trim());
                           System.out.println(sb2.toString().trim());
                       }
                       break;
               }
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
                System.out.println("We got a match");
                break;
            case "YOURTURN":
                System.out.println("OUR TURN");
                break;
            case "MOVE":

                System.out.println("A move was made");

                break;
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
}
