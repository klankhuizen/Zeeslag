package BKE.Network.Command;

import BKE.Network.NetworkCommand;

public class GetGamesCommand extends NetworkCommand {

    public GetGamesCommand() {

        this.args = new String[]{"get", "gamelist"};

    }

    @Override
    public boolean expectsResponse() {
        return true;
    }
}
