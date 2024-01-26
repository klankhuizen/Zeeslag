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
import java.util.*;

public class ZeeslagAIPlayer implements IPlayer{
    private String _name;
    private Random _random;
    private ArrayList<Ship> _ships = new ArrayList<>();
    private boolean _isPlacingShips;
    private static final int[] SHIPSIZES = { 2, 3, 4, 6 };

    private HashSet<Integer> _shotLocations = new HashSet<>();
    private Zeeslag _game;

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
//                System.out.println("Invalid config, attempt " + attempts);
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

    IBoard _board;
    @Override
    public void doMove() {
        long time = System.currentTimeMillis();
        if (_isPlacingShips){
            while (!_ships.isEmpty()) {
                Ship ship = _ships.get(0);
                _ships.remove(0);
                int begin = ship.getNetworkBegin();

                if (Framework.isNetworked()){
                    try {
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
            }
            _isPlacingShips = false;
            return;
        }

        boolean isPlayerOne = _game.getPlayerOne() == this;

        if (Framework.isNetworked()){
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
        } else {
            int loc = getNewShotLocation();
            int x = loc % _board.getWidth();
            int y = Math.floorDiv(loc, _board.getWidth());
            boolean hit = _game.schiet(isPlayerOne ? _game.getPlayerTwo().getBoard() : _game.getPlayerOne().getBoard(), x, y);
            if (Framework.isRunningBenchmarks()){
                if (hit){
                    if (isPlayerOne){
                        _game.getMatchStats().playerOneHits ++;

                    } else {
                        _game.getMatchStats().playerTwoHits ++;
                    }
                } else {
                    if (isPlayerOne){
                        _game.getMatchStats().playerOneMisses ++;

                    } else {
                        _game.getMatchStats().playerTwoMisses ++;
                    }
                }

            }
        }

        if (Framework.isRunningBenchmarks()){
            if (isPlayerOne){
                _game.getMatchStats().playerOneTotalTurnTime += System.currentTimeMillis() - time;
            } else {
                _game.getMatchStats().playerTwoTotalTurnTime += System.currentTimeMillis() - time;
            }
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


    public ZeeslagAIPlayer(String userName, Zeeslag game) {
        _name = userName;
        _isPlacingShips = true;
        _random = new Random();
        setGame(game);
    }

    public ZeeslagAIPlayer(String userName) {
        _name = userName;
        _isPlacingShips = true;
        _random = new Random();
    }

    public void setGame(IGame game){
        _game = (Zeeslag) game;
    }
}
