package BKE.Network.Command;

import BKE.Network.NetworkCommand;

public class AcceptChallengeCommand extends NetworkCommand {
    public AcceptChallengeCommand(int challengeId) {
        this.args = new String[]{"challenge", "accept", "" + challengeId};
    }

    @Override
    public boolean expectsResponse() {
        return false;
    }
}
