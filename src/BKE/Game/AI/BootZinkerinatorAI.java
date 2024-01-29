package BKE.Game.AI;

import BKE.Framework;
import BKE.Game.IBoard;
import BKE.Game.IGame;
import BKE.Game.Player.IPlayer;
import BKE.Game.Variants.Ship;
import BKE.Game.Variants.Zeeslag;
import BKE.Helper.Vector2D;
import BKE.Network.Command.DoMoveCommand;
import BKE.Network.Command.PlaceCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class BootZinkerinatorAI implements IPlayer {
    private Ship _hitting;
    private boolean _isPlacingShips = true;
    private IBoard _board;
    private Zeeslag _game;
    private String _name;
    private ArrayList<Ship> _ships = new ArrayList<>();
    private ArrayList<Ship> _enemyShips = new ArrayList<>();
    private HashSet<Integer> _shotLocations = new HashSet<>();
    private int[] _offsets = new int[4];
    private int _lastShot;
    private final int[] _searchPattern = {27, 20, 37, 44, 34, 29, 51, 41, 17, 10, 13, 54, 24, 3, 23, 47, 61, 58, 49, 14, 39, 60, 32, 4, 9, 15, 57, 8, 2, 6, 63, 56, 53, 46, 30, 42, 43, 0, 11, 19, 25, 36, 40, 21, 1, 5, 7, 16, 18, 22, 26, 28, 31, 33, 35, 38, 48, 50, 52, 55, 59, 62, 12, 45 };
    private int _searchPatternIndex = 0;
    private Phase _phase = Phase.SEARCHING;
    private Direction _direction = Direction.RIGHT;
    private int _initialHit;
    private boolean _opposite;
    private boolean _lastWasHit;
    private final HashSet<Integer> _neighbourFields = new HashSet<>();

    ArrayList<Integer> _streak = new ArrayList<>();

    public BootZinkerinatorAI(String name, IGame game) {
        _name = name;
        _game = (Zeeslag) game;
    }

    public BootZinkerinatorAI(String userName) {
        _name = userName;
    }

    private enum Direction{
        LEFT, RIGHT, UP, DOWN
    }

    private enum Phase{
        SEARCHING,
        LOCKED,
        SINKING
    }

    @Override
    public void doMove() {
        long time = System.currentTimeMillis();
        if (_isPlacingShips){
            placeShips();
            return;
        }

        boolean isPlayerOne = _game.getPlayerOne() == this;

        if (!Framework.isRunningBenchmarks()){
            try {
                Thread.sleep(100); // spam prot
            } catch (InterruptedException e) {
                //
            }
        }

//        if (Framework.isRunningBenchmarks()){
//            try {
//                Thread.sleep(1); // spam prot
//            } catch (InterruptedException e) {
//                //
//            }
//        }


        if (Framework.isNetworked()){
            try {
                String[] response = Framework.sendNetworkMessage(new DoMoveCommand(getNewShotLocation()));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            int loc = getNewShotLocation();
            if (loc == -1){
                _phase = Phase.SEARCHING;
                loc = getNewShotLocation();
            }

            if (loc < 0){
                // Somehow we missed a square. Find it and shoot there.
                for (int i = 0; i < 64; i++){
                    if (_shotLocations.contains(i) || _neighbourFields.contains(i)) continue;
                    loc = i;
                }

            }

            int x = loc % _board.getWidth();
            int y = Math.floorDiv(loc, _board.getWidth());
            boolean hit = _game.schiet(isPlayerOne ? _game.getPlayerTwo().getBoard() : _game.getPlayerOne().getBoard(), x, y);
            _shotLocations.add(loc);
            _lastShot = loc;
            _lastWasHit = hit;

            switch (_phase){
                case SEARCHING -> {
                    if (!_streak.isEmpty()){
                        _streak.clear();
                    }

                    _opposite = false;
                    if (hit){
                        _phase = Phase.LOCKED;
                        _initialHit = loc;
                        _streak.add(loc);
                    }
                }

                case LOCKED -> {
                    _opposite = false;
                    if (hit){
                        _phase = Phase.SINKING;
                        _streak.add(loc);
                    } else{
                        _direction = getNextDirection(_direction);
                    }
                }

                case SINKING -> {
                    if (!hit) {
                        if (!_opposite){
                            _opposite = true;
                        } else {
                            _phase = Phase.SEARCHING;
                            _direction = Direction.RIGHT;
                            maskSurroundingTiles();
                        }
                    } else{
                        _streak.add(loc);
                    }
                }
            }


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

    private void maskSurroundingTiles(){
        Direction initialDirection = Direction.LEFT;
        int tiles = _board.getWidth() * _board.getHeight();
        for (int loc : _streak ) {
            for (int i = 0; i < 4; i ++){
                int neighbour = getNewPosFromDirection(loc, initialDirection);
                if (canHitOnWater(neighbour) && neighbour > 0 && neighbour < tiles){
                    _neighbourFields.add(neighbour);
                }
                initialDirection = getNextDirection(initialDirection);
            }
        }
        _streak.clear();
    }

    private boolean canHitOnWater(int loc){
        return !_shotLocations.contains(loc) && !_neighbourFields.contains(loc);
    }

    private int getNewShotLocation(){
        int loc = 0;
        switch (_phase){
            case SEARCHING -> {
                int length =_searchPattern.length;
                do {
                    // Keep searching through it
                    if (_searchPatternIndex > length - 1) {

                        // this should never happen because we should have won by now.
                        // throw new RuntimeException("Index Out Of Bounds: Search Pattern out of bounds.");
                        return -1;
                    }
                    loc = _searchPattern[_searchPatternIndex++];
                } while (!canHitOnWater(loc));
            }

            case LOCKED -> {
                loc = getNewPosFromDirection(_initialHit, _direction);
                int count = 1;
                while (!_board.isValidPosition(loc) || !canHitOnWater(loc)) {
                    Direction newDirection = getNextDirection(_direction);

                    loc = getNewPosFromDirection(_initialHit, newDirection);
                    _direction = newDirection;
                    if (count % 4 == 0){
                        // Gone full circle
                        _searchPatternIndex ++;
                        return -1;
                    }
                    count++;

//                    if (newDirection == _direction) {
//                        // We've gone full circle
//                        _phase = Phase.SEARCHING;
//                        _direction = Direction.LEFT;
//                        loc = getNewShotLocation();
//                    } else {
//
//                    }
                }
           }

            case SINKING -> {

                if (!_lastWasHit){
                    if (_opposite){
                        switch (_direction) {
                            case UP -> {
                                _direction = Direction.DOWN;
                            }
                            case DOWN -> {
                                _direction = Direction.UP;
                            }
                            case LEFT -> {
                                _direction = Direction.RIGHT;
                            }
                            case RIGHT -> {
                                _direction = Direction.LEFT;
                            }
                        }
                        _lastShot = _initialHit;
                    } else {
                        // Ship must have been sunk!
                        _phase = Phase.SEARCHING;
                    }
                }

                loc = getNewPosFromDirection(_lastShot, _direction);

                int count = 1;
//                while(_shotLocations.contains(loc) && count % 4 != 0){
//                    _direction = getNextDirection(_direction);
//                    loc = getNewPosFromDirection(_lastShot, _direction);
//                    count++;
//                }

                if (!canHitOnWater(loc)) {
                    return -1;
                }
            }
        }
        return loc;
    }

    private int getNewPosFromDirection(int initialLoc, Direction direction){
        int loc = initialLoc;
        switch (direction) {
            case RIGHT -> {
                loc += 1;
                // make sure to not go off the board
                if (loc % 8 == 0) {
                    // End of the line
                    loc = -1;
                }
            }
            case DOWN ->{
                loc += _board.getWidth();
            }
            case UP -> {
                loc -= _board.getWidth();
                if (loc < 0){
                    return -1;
                }
            }
            case LEFT -> {
                if (loc % 8 == 0){
                    loc = -1;
                } else {
                    loc -= 1;
                }
            }
        }
        return loc;
    }

    private Direction getNextDirection(Direction dir) {
        Direction direction = dir;
        switch (dir) {
            case UP -> {
                direction = Direction.RIGHT;
            }
            case RIGHT -> {
                direction = Direction.DOWN;
            }
            case DOWN -> {
                direction = Direction.LEFT;
            }
            case LEFT -> {
                direction = Direction.UP;
            }
        }

        return direction;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void setBoard(IBoard board) {
        _board = board;
        _offsets[0] = -1;
        _offsets[1] = -1 * _board.getWidth();
        _offsets[2] = 1;
        _offsets[3] = _board.getWidth();
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
        //
    }

    @Override
    public void setGame(IGame game) {
        _game = (Zeeslag) game;
    }

    public void placeShips() {
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

        // Copy the ships over. doesn't really matter which length.
        for (Ship s: _ships) {
            _enemyShips.add(new Ship(new Vector2D(0,0), s.isHorizontal(), s.getLength(), _board.getWidth()));
        }

        _isPlacingShips = false;
    }



    private int maxRemainingSize(){
        if (_enemyShips.isEmpty()) return 0;
        return _enemyShips.stream().max((a, b) -> Math.max(a.getLength(), b.getLength())).get().getLength();
    }
}
