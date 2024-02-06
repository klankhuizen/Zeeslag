package BKE.Network.Command;

import BKE.Network.NetworkCommand;

public class ForfeitCommand extends NetworkCommand {
    public ForfeitCommand() {
        this.args = new String[]{"forfeit"};
    }

    @Override
    public boolean expectsResponse() {
        return false;
    }
}
