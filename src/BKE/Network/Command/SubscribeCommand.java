package BKE.Network.Command;

import BKE.Network.NetworkCommand;

public class SubscribeCommand extends NetworkCommand {
    public SubscribeCommand(String game) {
        this.args = new String[]{"subscribe" , game};
    }

    @Override
    public boolean expectsResponse() {
        return false;
    }
}
