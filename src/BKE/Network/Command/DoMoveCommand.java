package BKE.Network.Command;

import BKE.Game.IBoard;
import BKE.Network.NetworkCommand;

public class DoMoveCommand extends NetworkCommand {

    public DoMoveCommand(IBoard board, int x, int y) {
        this.args = new String[]{"move", "" + (board.getHeight() * y + x)};
    }

    @Override
    public boolean expectsResponse() {
        return false;
    }
}
