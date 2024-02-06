package BKE.Network.Command;

import BKE.Network.NetworkCommand;

public class DisconnectCommand extends NetworkCommand {

    public DisconnectCommand() {
        this.args = new String[]{"disconnect"};
    }

    @Override
    public boolean expectsResponse() {
        return false;
    }
}
