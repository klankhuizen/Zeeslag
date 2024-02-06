package BKE.Network;

import BKE.Framework;
import BKE.Game.Player.IPlayer;
import BKE.Game.Variants.Zeeslag;
import BKE.Helper.Vector2D;
import BKE.Network.Command.DoMoveCommand;

import java.io.Closeable;
import java.io.IOException;

public class NetworkedGame implements Closeable {
    IPlayer _localPlayer;
    IPlayer _remotePlayer;
    String _gameType;

    public NetworkedGame(IPlayer localPlayer, IPlayer remotePlayer, String gameType) {
        _localPlayer = localPlayer;
        _remotePlayer = remotePlayer;
        _gameType = gameType;
    }

    public IPlayer getLocalPlayer(){
        return _localPlayer;
    }

    public IPlayer getRemotePlayer(){
        return _remotePlayer;
    }

    @Override
    public void close() throws IOException {

    }
}
