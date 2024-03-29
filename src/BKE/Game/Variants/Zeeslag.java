package BKE.Game.Variants;

import BKE.ApplicationState;
import BKE.Framework;
import BKE.Game.Board;
import BKE.Game.IBoard;
import BKE.Game.IGame;
import BKE.Game.Player.IPlayer;
import BKE.Helper.MatchStats;
import BKE.Helper.Vector2D;
import BKE.Network.Message.GameResultMessage;
import BKE.Network.Message.MoveMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Zeeslag implements IGame {
    private final String _name = "ZEESLAG";
    private ApplicationState _state;
    private IPlayer _playerOne;
    private IPlayer _playerTwo;
    private String _nextTurn;
    private IBoard _playerOneBoard;
    private IBoard _playerTwoBoard;
    private MatchStats _stats;
    private IPlayer _winner;

    private boolean _networked;
    private static Vector2D[] _boatOffsetChecks = {
            new Vector2D(-1, 0),
            new Vector2D(1, 0),
            new Vector2D(0, -1),
            new Vector2D(0, 1)
    };
    private boolean _playerOneTurn;
    private int _rowSelection;
    private int _columnSelection;

    private Thread _thread;

    public enum FieldValues {
        // This is some bullshit, Java!
        EMPTY(0), HIT(1), MISS(2), SHIP(3), GEZONKEN(4), MASKED(5);
        private final int value;
        private FieldValues(int val){
            this.value = val;
        }
        public int getValue() {
            return value;
        }
    }

    private void startLocalThread(){
        if (_thread != null){
            _thread.interrupt();
            _thread = null;
        }

        _thread = new Thread(() -> {
            // Schepen voor speler plaatsen
            plaatsSchepen(_playerOneBoard);

            // Schepen voor tegenstander plaatsen
            plaatsSchepen(_playerTwoBoard);

            _nextTurn = _playerOne.getName();
            IPlayer nextPlayer;
            // Spelronde
            while (!isGameOver()) {

                nextPlayer = _nextTurn.equals(_playerOne.getName()) ? _playerOne : _playerTwo;

                nextPlayer.doMove();

                _stats.turns++;

                _nextTurn = nextPlayer == _playerOne ? _playerTwo.getName() : _playerOne.getName();

                if (!Framework.isRunningBenchmarks()){
                    Framework.UpdateUI(_playerOne, _playerTwo);
                }

                _nextTurn = nextPlayer == _playerOne ? _playerTwo.getName() : _playerOne.getName();

                // Controleer of het spel voorbij is
                if (isGameOver()) {
                    break;
                }
            }

//            System.out.println("Winner is " + _winner.getName());
            _state = ApplicationState.HALTED;
            // Toon het resultaat
            resultaat();
        });
        _thread.start();
    }

    @Override
    public void start(String playerStarting) {
        if (!getIsNetworked()){
            startLocalThread();
        }
    }

    @Override
    public void initialize(IPlayer playerOne, IPlayer playerTwo, boolean isNetworked) {
//        System.out.println("Initializing Zeeslag");

        _networked = isNetworked;

        // zet bord op
        _playerOneBoard = new Board(8, 8);
        _playerTwoBoard = new Board(8, 8);
        _playerOne = playerOne;
        _playerTwo = playerTwo;
        _playerOne.setBoard(_playerOneBoard);
        _playerTwo.setBoard(_playerTwoBoard);
        _state = ApplicationState.RUNNING;

        _stats = new MatchStats();

        _stats.playerOne = playerOne.getName();
        _stats.playerTwo = playerTwo.getName();
    }

    @Override
    public void HandleInput(String input) {
        // Hier komt the user-input binnen

        // ongeldige input negeren
        if (_nextTurn != _playerOne.getName() || input == null || input.isEmpty()) return;

        if (input.length() != 2) return;

        _rowSelection = Integer.parseInt(String.valueOf(input.charAt(0)));
        _columnSelection = input.charAt(1) - 'A';

        _playerOne.setNextMove(_columnSelection, _rowSelection);
    }

    @Override
    public ApplicationState GetState() {
        return _state;
    }

    @Override
    public void SetState(ApplicationState state) {
        _state = state;
    }

    @Override
    public IBoard GetPlayerBoard() {
        return _playerOneBoard;
    }

    public IBoard GetOpponentBoard(){
        return _playerTwoBoard;
    }

    public void RequestUpdate() {
        Framework.UpdateUI(_playerOne, _playerTwo);
    }

    @Override
    public boolean getIsNetworked() {
        return _networked;
    }

    @Override
    public String GetGameName() {
        return  _name;
    }

    @Override
    public IPlayer getPlayer(String name) {
        return _playerOne.getName().equals(name) ? _playerOne : _playerTwo;
    }

    @Override
    public void doTurn(String playerName) {
        IPlayer player = getPlayer(playerName);
        if (player == null) throw new RuntimeException("Player " + playerName + " does not exist");

        player.doMove();
    }

    @Override
    public void move(MoveMessage msg) {
//        IPlayer playerMakingMove = _playerOne.getName().equals(msg.getPlayerName()) ? _playerOne : _playerTwo;
        IBoard affectedBoard = _playerOne.getName().equals(msg.getPlayerName()) ? _playerTwoBoard : _playerOneBoard;
        Vector2D position = affectedBoard.getFromNetworked(msg.getLocation());
        affectedBoard.setValue(position.X, position.Y, msg.getValue().getValue());
        Framework.UpdateUI(_playerOne, _playerTwo);
    }

    @Override
    public void setGameResult(GameResultMessage gsm) {
        Framework.UpdateUI(gsm);
    }

    @Override
    public IPlayer getPlayerOne() {
        return _playerOne;
    }

    @Override
    public IPlayer getPlayerTwo() {
        return _playerTwo;
    }

    @Override
    public MatchStats getMatchStats() {
        return _stats;
    }

    @Override
    public void close() throws IOException {
        if (_thread != null){
            _thread.interrupt();
            _thread = null;
        }
        _state = ApplicationState.HALTED;
    }

    private void zetSpeler() throws InterruptedException {
        // De speler kan een vak kiezen om op de schieten
        // Dit doet de speler door het nummer en de letter
        // van de bij behorende row en col aan te geven
        _playerOneTurn = true;
        _columnSelection = -1;
        _rowSelection = 0;

        Framework.SendMessageToUser("Jouw beurt:");
        Framework.SendMessageToUser("Voer coordinaten in:(1-8 + A-H) (1A, 4D, 8B)");

        while(_rowSelection == 0 && _columnSelection < 0){
            Thread.sleep(100);
        }

        // Voer het schot uit op het bord van de tegenstander
        boolean hit = schiet(_playerTwoBoard, _rowSelection - 1, _columnSelection);

        // Toon de resultaten van het schot aan de speler
        if (hit) {
            Framework.SendMessageToUser("Gefeliciteerd! Je hebt een schip geraakt op positie " + _playerTwoBoard.locatie(_rowSelection - 1, _columnSelection));
        } else {
            Framework.SendMessageToUser("Helaas, je hebt gemist op positie " + _playerTwoBoard.locatie(_rowSelection - 1, _columnSelection));
        }

        // Reset de selecties voor de volgende beurt
        _columnSelection = -1;
        _rowSelection = 0;
    }

    private void zetTegenstander() {
        _playerOneTurn = false;
        Random random = new Random();

        int row, col;
        do {
            row = random.nextInt(8);
            col = random.nextInt(8);
        } while (!_playerOneBoard.isValidPosition(row, col));

        // Voer het schot uit op het bord van de speler
        boolean hit = schiet(_playerOneBoard, row, col);

        // Toon de resultaten van het schot aan de speler
        if (hit) {
            Framework.SendMessageToUser("De tegenstander heeft een schip geraakt op positie " + _playerOneBoard.locatie(row, col));
        } else {
            Framework.SendMessageToUser("De tegenstander heeft gemist op positie " + _playerOneBoard.locatie(row, col));
        }
    }

    private boolean isGameOver() {
        if (schepenGezonken(_playerOneBoard)){
            _winner = _playerTwo;
            _stats.winner = _playerTwo.getName();
        } else if (schepenGezonken(_playerTwoBoard)){
            _winner = _playerOne;
            _stats.winner = _playerOne.getName();
        }
        return _winner != null;
    }

    private void resultaat() {
        Framework.SendMessageToUser("Het spel is voorbij!");

        if (schepenGezonken(_playerOneBoard)) {
            Framework.SendMessageToUser("De tegenstander heeft gewonnen!");
        } else {
            Framework.SendMessageToUser("Je hebt gewonnen!");
        }
    }

    public void plaatsSchepen(IBoard board) {
        Random random = new Random();

        // Hier worden de schip sizes gedefineerd
        // Dit kan eventueel ook later gelinked worden aan namen
        int[] shipSizes = {2, 2, 3, 4, 5};
        int totalsquares = Arrays.stream(shipSizes).sum(); // get a sum of all the items in the array.
        int attempts = 0;
        boolean valid = false;

        do {
            attempts ++;
            int placedSquares = 0;
            for (int grootte : shipSizes) {
                int row, col;
                boolean horizontaal = random.nextBoolean(); // Random ligging van ship
                int cycles = 0;

                // Hier checked hij of het ship juist geplaatst wordt
                do {
                    row = random.nextInt(board.getHeight());
                    col = random.nextInt(board.getWidth());
                    cycles ++;
                } while (!isValidPositionForShip(board, row, col, grootte, horizontaal) && cycles < 100);

                // Will attempt 100 times... After that, probably impossible...
                if (cycles > 99){
                    // Invalid
                    break;
                }

                plaatsSchipOpBord(board, row, col, grootte, horizontaal);
                placedSquares += grootte;
            }

            // The combination is invalid, start over.
            if (totalsquares != placedSquares){
                board.clear();
//                System.out.println("Invalid config, attempt " + attempts);
                continue;
            }


            valid = true;
        } while (!valid);


    }

    public static void plaatsSchipOpBord(IBoard board, int row, int col, int grootte, boolean horizontaal) {
        // Hier gaat het ship horizontaal via col
        if (horizontaal) {
            for (int i = 0; i < grootte; i++) {
                board.setValue(row, col + i, FieldValues.SHIP.getValue());
            }
        }
        // Hier gaat het ship verticaal via row
        else {
            for (int i = 0; i < grootte; i++) {
                board.setValue(row + i, col, FieldValues.SHIP.getValue());
            }
        }
    }

    public static boolean isValidPositionForShip(IBoard board,int row, int col, int size, boolean horizontal){
        if (!board.isValidPosition(row, col)){
            return false;
        }
        // If the boat is left-to-right, make sure that the column coordinate plus the length of it does not exceed the
        // width of the playing board.
        if(horizontal) {
            if (!board.isValidPosition(row, col + size) ) {
                return false;
            }
            for (int i = 0; i < size; i ++){
                if (board.getValue(row, col + i) != FieldValues.EMPTY.getValue()){
                    return false;
                }

                if (HasNeighbors(board, row, col + i)) return false;

            }
            return true;
        }
        // Do the same for up-to-down
        if (row + size < board.getHeight()){
            if (!board.isValidPosition(row + size, col) ) {
                return false;
            }
            for (int i = 0; i < size; i ++){
                if (board.getValue(row + i, col) != FieldValues.EMPTY.getValue()){
                    return false;
                }


                if (HasNeighbors(board, row + i, col)) return false;
            }
            return true;
        }
        return false;
    }

    private static boolean HasNeighbors(IBoard board, int row, int col){
        // Check left, right, up and down of this coordinate to see if there is a ship there.
        for(Vector2D offset : _boatOffsetChecks){
            int x = row + offset.X;
            int y = col + offset.Y;

            // If the coordinate to check is outside of bounds, we don't have to check it.
            if (!board.isValidPosition(x, y)) continue;
            if (board.getValue(x, y) != FieldValues.EMPTY.getValue()){
                return true;
            }
        }
        return false;
    }

    private boolean schepenGezonken(IBoard board) {
        int[][] boardData = board.getValues();
        // Loop door het bord en controleer of er nog 'O' (schepen) aanwezig zijn
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                if (boardData[i][j] == Zeeslag.FieldValues.SHIP.getValue()) {
                    // Er is nog minstens één schip aanwezig
                    return false;
                }
            }
        }
        // Alle schepen zijn gezonken
        return true;

    }

    public void plaatsSchip(IBoard board, int row, int col) {
        // Hier wordt er gecontroleerd of de posiitie juist is.
        // Zo ja krijgt de speler een melding dat het plaatsen gelukt is.
        if (canPlacePiece(board, row, col)) {
            board.setValue(row, col, Zeeslag.FieldValues.SHIP.getValue());
            System.out.println("Schip geplaatst op positie " + board.locatie(row, col));
        }
        else {
            System.out.println("je kan hier geen schip plaatsen");
        }
    }

    private boolean canPlacePiece(IBoard board, int row, int col) {
        // Hier wordt er gekeken of de positie juist is die gekozen werd op het veld
        return board.isValidPosition(row, col) && board.getValue(row, col) == Zeeslag.FieldValues.EMPTY.getValue();
    }

    public boolean schiet(IBoard board, int row, int col) {
        // Controleer of de zet binnen het bord valt
        if (!board.isValidPosition(row, col)) {

            System.out.println(row + " -> " + col);

            System.out.println("Ongeldige positie. Probeer opnieuw.");
            return false;
        }

        // Controleer of er een schip op de opgegeven positie is
        if (board.getValue(row, col) == Zeeslag.FieldValues.SHIP.getValue()) {
            board.setValue(row, col,Zeeslag.FieldValues.HIT.getValue()); // Markeer het getroffen schip
//            System.out.println("Gefeliciteerd! Je hebt een schip geraakt op positie " + board.locatie(row, col));
            // Controleert of het schip is gezonken
            if (isSchipGezonken(board, row, col)) {
//                System.out.println("Helaas, je schip is gezonken op positie " + board.locatie(row, col));
            }
            return true;
        } else if (board.getValue(row, col) == Zeeslag.FieldValues.EMPTY.getValue()) {
            board.setValue(row, col, Zeeslag.FieldValues.MISS.getValue()); // Markeer de gemiste schoten
//            System.out.println("Helaas, je hebt gemist op positie " + board.locatie(row, col));
        } else {
            System.out.println("Je hebt hier al geschoten. Probeer een andere positie.");
        }
        return false;
    }

    private boolean isSchipGezonken(IBoard board, int row, int col) {
        // Controleer horizontaal
        int countHorizontaal = 0;
        for (int j = 0; j < 8; j++) {
            if (board.getValue(row, j) == Zeeslag.FieldValues.HIT.getValue()) {
                countHorizontaal++;
            }
        }

        // Controleer verticaal
        int countVerticaal = 0;
        for (int i = 0; i < 8; i++) {
            if (board.getValue(i, col) == Zeeslag.FieldValues.HIT.getValue()) {
                countVerticaal++;
            }
        }

        // Als alle vakjes van het schip zijn geraakt, is het schip gezonken
        // TODO This needs to be different
        return countHorizontaal == 5 || countVerticaal == 5;
    }
}
