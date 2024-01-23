package BKE.Network.Command;

import BKE.Network.NetworkCommand;

public class LoginCommand extends NetworkCommand {
    public LoginCommand( String name ) {
        this.args = new String[]{"login", name};
    }

    @Override
    public boolean expectsResponse() {
        return false;
    }
}
