package BKE.Network.Command;

import BKE.Network.NetworkCommand;

public class ChallengeCommand extends NetworkCommand {

    public ChallengeCommand(String player, String game) {

        this.args = new String[]{ "challenge", player, game };

    }

    @Override
    public boolean expectsResponse() {
        return false;
    }
}
