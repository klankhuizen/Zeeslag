package BKE.Network.Command;

import BKE.Network.NetworkCommand;

public class GetPlayersCommand extends NetworkCommand {

    public GetPlayersCommand() {

        this.args = new String[] { "get" , "playerlist"};

    }

    @Override
    public boolean expectsResponse() {
        return true;
    }
}
