package BKE.Network;

import java.util.ArrayList;

public abstract class NetworkCommand {

    /**
     * Arguments of this command.
     */
    public String[] args;

    public abstract boolean expectsResponse();
}
