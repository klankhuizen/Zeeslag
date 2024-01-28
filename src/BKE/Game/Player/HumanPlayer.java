package BKE.Game.Player;

import BKE.Framework;
import BKE.Game.IBoard;
import BKE.Game.IGame;
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

public class HumanPlayer implements IPlayer {
    private IBoard _board;
    private boolean _holding;
    private String _name;
    private Random _random;
    private ArrayList<Ship> _ships = new ArrayList<>();
    private static final int[] SHIPSIZES = { 2, 3, 4, 6 };
    private HashSet<Integer> _shotLocations = new HashSet<>();

    private int nextX, nextY;

    public HumanPlayer(String name){
        _name = name;
        _random = new Random();
    }

    @Override
    public void doMove() {
        _holding = true;
        while (_holding){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // dont care
            }
        }

        IPlayer otherPlayer = Framework.GetCurrentGame().getPlayerOne() == this ? Framework.GetCurrentGame().getPlayerTwo() : Framework.GetCurrentGame().getPlayerOne();
        IBoard board = otherPlayer.getBoard();
        if (!board.isValidPosition(nextX, nextY)) {
            return ;
        }

        // Controleer of er een schip op de opgegeven positie is
        if (board.getValue(nextX, nextY) == Zeeslag.FieldValues.SHIP.getValue()) {
            board.setValue(nextX, nextY, Zeeslag.FieldValues.HIT.getValue()); // Markeer het getroffen schip
            Framework.SendMessageToUser("Gefeliciteerd! Je hebt een schip geraakt op positie " + board.locatie(nextX, nextY));
           // Controleert of het schip is gezonken
            return;
        } else if (board.getValue(nextX, nextY) == Zeeslag.FieldValues.EMPTY.getValue()) {
            board.setValue(nextX, nextY, Zeeslag.FieldValues.MISS.getValue()); // Markeer de gemiste schoten
        } else {
            Framework.SendMessageToUser("Je hebt hier al geschoten. Probeer een andere positie.");
        }
        _holding = true;
        nextX = 0;
        nextY = 0;
    }

    public String getName(){
        return _name;
    }

    @Override
    public void setBoard(IBoard board) {
        _board = board;
        generateShipPlacements();
    }

    @Override
    public IBoard getBoard() {
        return _board;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public void setNextMove(int x, int y) {
        nextX = x;
        nextY = y;
        _holding = false;
    }

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

        _board.clear();
        for (Ship ship : _ships) {

            Vector2D begin = ship.getBegin();
            Vector2D end = ship.getEnd();

            if (ship.isHorizontal()){
                for (int i = 0; i < ship.getLength(); i++){
                    _board.setValue(begin.X + i, begin.Y, Zeeslag.FieldValues.SHIP.getValue());
                }
            } else{
                for (int i = 0; i < ship.getLength(); i++){
                    _board.setValue(begin.X, begin.Y + i, Zeeslag.FieldValues.SHIP.getValue());
                }
            }
        }

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

    @Override
    public void setGame(IGame game) {

    }
}
