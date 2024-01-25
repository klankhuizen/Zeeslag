package BKE.Network.Command;

import BKE.Game.IBoard;
import BKE.Network.NetworkCommand;

public class DoMoveCommand extends NetworkCommand {

    public DoMoveCommand(int pos) {
        this.args = new String[]{"move", "" + pos};
    }

    @Override
    public boolean expectsResponse() {
        return false;
    }
}
