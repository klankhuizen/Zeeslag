package BKE.Game.Player;

import BKE.Framework;
import BKE.Game.IBoard;
import BKE.Game.Variants.Ship;
import BKE.Game.Variants.Zeeslag;
import BKE.Helper.Vector2D;
import BKE.Network.Command.DoMoveCommand;
import BKE.Network.Command.PlaceCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class ZeeslagAIPlayer implements IPlayer{
    private String _name;
    private Random _random;
    private ArrayList<Ship> _ships = new ArrayList<>();
    private boolean _isPlacingShips;
    private static final int[] SHIPSIZES = { 2, 3, 4, 6 };

    private HashSet<Integer> _shotLocations = new HashSet<>();

    private void generateShipPlacements(){

        // Hier worden de schip sizes gedefineerd
        // Dit kan eventueel ook later gelinked worden aan namen
        int totalsquares = Arrays.stream(SHIPSIZES).sum(); // get a sum of all the items in the array.
        int attempts = 0;
        boolean valid = false;
        _ships.clear();

        do {
            attempts ++;
            int placedSquares = 0;
            for (int length : SHIPSIZES) {
                int row, col;
                boolean horizontal = _random.nextBoolean(); // Random ligging van ship
                int cycles = 0;

                // Hier checked hij of het ship juist geplaatst wordt
                do {
                    row = _random.nextInt(_board.getHeight());
                    col = _random.nextInt(_board.getWidth());
                    cycles ++;
                } while (!Zeeslag.isValidPositionForShip(_board, row, col, length, horizontal) && cycles < 100000);

                // Will attempt 100 times... After that, probably impossible...
                if (cycles > 99){
                    // Invalid
                    break;
                }

                Zeeslag.plaatsSchipOpBord(_board, row, col, length, horizontal);
                _ships.add(new Ship(new Vector2D(col, row), horizontal, length, _board.getWidth()));

                placedSquares += length;
            }

            // The combination is invalid, start over.
            if (totalsquares != placedSquares){
                _ships.clear();
                _board.clear();
                System.out.println("Invalid config, attempt " + attempts);
                continue;
            }


            valid = true;
        } while (!valid);

    }

    private int getNewShotLocation(){
        int loc = 0;
        while (true){
            loc = _random.nextInt(0, 64);
            if (_shotLocations.contains(loc) || loc < 0 || loc > 63){
                continue;
            }
            break;
        }
        _shotLocations.add(loc);
        return loc;
    }

    IBoard _board;
    @Override
    public void doMove() {
        if (_isPlacingShips){
            while (!_ships.isEmpty()) {
                Ship ship = _ships.get(0);
                _ships.remove(0);
                try {
                    int begin = ship.getNetworkBegin();
                    int end = ship.getNetworkEnd();
                    String[] response = Framework.sendNetworkMessage(new PlaceCommand(_board, begin, end));
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(100); // spam prot
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            _isPlacingShips = false;
            return;
        }
        try {
            Thread.sleep(100); // spam prot
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            String[] response = Framework.sendNetworkMessage(new DoMoveCommand(getNewShotLocation()));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void setBoard(IBoard board) {
        _board = board;
        generateShipPlacements();
    }

    public void setShips(ArrayList<Ship> ships){
        this._ships = ships;
    }

    @Override
    public IBoard getBoard() {
        return _board;
    }

    @Override
    public boolean isRemote() {
        return false;
    }


    public ZeeslagAIPlayer(String userName) {
        _name = userName;
        _isPlacingShips = true;
        _random = new Random();

    }
}
