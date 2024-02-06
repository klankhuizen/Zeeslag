package BKE.Network.Command;

import BKE.Game.IBoard;
import BKE.Helper.Vector2D;
import BKE.Network.NetworkCommand;

public class PlaceCommand extends NetworkCommand {
    public PlaceCommand(IBoard board, int first, int second) {
        this.args = new String[]{ "PLACE", "" + first, "" + second};
    }
    @Override
    public boolean expectsResponse() {
        return false;
    }
}
