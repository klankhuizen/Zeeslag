package BKE.Game.Variants;

import BKE.ApplicationState;
import BKE.Framework;
import BKE.Game.Board;
import BKE.Game.IBoard;
import BKE.Game.IGame;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Zeeslag implements IGame {

    private final String _name = "ZEESLAG";
    private ApplicationState _state;
    private IBoard _playerBoard;
    private IBoard _opponentBoard;

    private Offset[] _boatOffsetChecks = {
            new Offset(-1, 0),
            new Offset(1, 0),
            new Offset(0, -1),
            new Offset(0, 1)
    };
    private boolean _playerTurn;
    private int _rowSelection;
    private int _columnSelection;

    private Thread _thread;

    public enum FieldValues {

        // This is some bullshit, Java!
        EMPTY(0), HIT(1), MISS(2), SHIP(3);

        private final int value;
        private FieldValues(int val){
            this.value = val;
        }

        public int getValue() {
            return value;
        }

    }

    private class Offset{
        int X = 0, Y = 0;
        public Offset(int x, int y){
            X = x;
            Y = y;
        }
    }

    @Override
    public void start() {

        System.out.println("Starting Zeeslag");
        System.out.println("De ronde van Zeeslag zal zo beginnen");

        if (_thread != null){
            _thread.stop();
            _thread = null;
        }

        _thread = new Thread(() -> {
            // Schepen voor speler plaatsen
            plaatsSchepen(_playerBoard);

            // Schepen voor tegenstander plaatsen
            plaatsSchepen(_opponentBoard);

            // Spelronde
            while (!isGameOver()) {


                // Beurt van speler
                try {
                    zetSpeler();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Framework.UpdateUI(_opponentBoard.getBoard(), _playerBoard.getBoard());

                // Controleer of het spel voorbij is
                if (isGameOver()) {
                    break;
                }

                // Beurt van tegenstander
                zetTegenstander();
                Framework.UpdateUI(_opponentBoard.getBoard(), _playerBoard.getBoard());
            }

            // Toon het resultaat
            Resultaat();
        });

        _thread.start();
    }

    @Override
    public void initialize() {
        System.out.println("Initializing Zeeslag");

        // zet bord op
        _playerBoard = new Board(8, 8);
        _opponentBoard = new Board(8, 8);
        _state = ApplicationState.RUNNING;
    }

    @Override
    public void HandleInput(String input) {
        // Hier komt the user-input binnen

        // ongeldige input negeren
        if (!_playerTurn || input == null || input.isEmpty()) return;

        if (input.length() != 2) return;

        _rowSelection = Integer.parseInt(String.valueOf(input.charAt(0)));
        _columnSelection = input.charAt(1) - 'A';
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
        return _playerBoard;
    }

    public IBoard GetOpponentBoard(){
        return _opponentBoard;
    }

    public void RequestUpdate() {
        Framework.UpdateUI(_opponentBoard.getBoard(), _playerBoard.getBoard());
    }

    @Override
    public boolean getIsNetworked() {
        return false;
    }

    @Override
    public String GetGameName() {
        return  _name;
    }

    @Override
    public void close() throws IOException {
        if (_thread != null){
            _thread.stop();
            _thread = null;
        }
        _state = ApplicationState.HALTED;
    }

    public Zeeslag() {}

    private void zetSpeler() throws InterruptedException {
        // De speler kan een vak kiezen om op de schieten
        // Dit doet de speler door het nummer en de letter
        // van de bij behorende row en col aan te geven
        _playerTurn = true;

        _columnSelection = -1;
        _rowSelection = 0;

        Framework.SendMessageToUser("Jouw beurt:");
        Framework.SendMessageToUser("Voer coordinaten in:(1-8 + A-H) (1A, 4D, 8B)");

        while(_rowSelection == 0 && _columnSelection < 0){
            Thread.sleep(100);
        }

        // Voer het schot uit op het bord van de tegenstander
        boolean hit = schiet(_opponentBoard, _rowSelection - 1, _columnSelection);

        // Toon de resultaten van het schot aan de speler
        if (hit) {
            Framework.SendMessageToUser("Gefeliciteerd! Je hebt een schip geraakt op positie " + _opponentBoard.locatie(_rowSelection - 1, _columnSelection));
        } else {
            Framework.SendMessageToUser("Helaas, je hebt gemist op positie " + _opponentBoard.locatie(_rowSelection - 1, _columnSelection));
        }

        // Reset de selecties voor de volgende beurt
        _columnSelection = -1;
        _rowSelection = 0;
    }

    private void zetTegenstander() {
        _playerTurn = false;
        Random random = new Random();

        int row, col;
        do {
            row = random.nextInt(8);
            col = random.nextInt(8);
        } while (!_playerBoard.isValidPosition(row, col));

        char colChar = (char) ('A' + col);

        // Voer het schot uit op het bord van de speler
        boolean hit = schiet(_playerBoard, row, col);

        // Toon de resultaten van het schot aan de speler
        if (hit) {
            Framework.SendMessageToUser("De tegenstander heeft een schip geraakt op positie " + _playerBoard.locatie(row, col));
        } else {
            Framework.SendMessageToUser("De tegenstander heeft gemist op positie " + _playerBoard.locatie(row, col));
        }
    }

    // schepenGezonken is niet klaar en moet nog gemaakt worden.
    private boolean isGameOver() {
        return schepenGezonken(_playerBoard) || schepenGezonken(_opponentBoard);
    }

    private void Resultaat() {
        Framework.SendMessageToUser("Het spel is voorbij!");

        if (schepenGezonken(_playerBoard)) {
            Framework.SendMessageToUser("De tegenstander heeft gewonnen!");
        } else {
            Framework.SendMessageToUser("Je hebt gewonnen!");
        }
    }

    private void SwitchSides(){
        _playerTurn = !_playerTurn;
        if (_playerTurn){
            System.out.println("Jouw beurt:");
            System.out.print("Voer de rij in (1-8): ");
        }
    }

    public void plaatsSchepen(IBoard board) {
        Random random = new Random();

        // Hier worden de schip sizes gedefineerd
        // Dit kan eventueel ook later gelinked worden aan namen
        int[] shipSizes = {2, 2, 3, 4, 5};
        int totalsquares = Arrays.stream(shipSizes).sum();
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

            if (totalsquares != placedSquares){
                board.clear();
                System.out.println("Invalid config, attempt " + attempts);
                continue;
            }


            valid = true;
        } while (!valid);


    }

    private void plaatsSchipOpBord(IBoard board, int row, int col, int grootte, boolean horizontaal) {
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

    private boolean isValidPositionForShip(IBoard board,int row, int col, int size, boolean horizontal){
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

    private boolean HasNeighbors(IBoard board, int row, int col){
        // Check left, right, up and down of this coordinate to see if there is a ship there.
        for(Offset offset : _boatOffsetChecks){
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

        int[][] boardData = board.getBoard();

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
            System.out.println("Ongeldige positie. Probeer opnieuw.");
            return false;
        }

        // Controleer of er een schip op de opgegeven positie is
        if (board.getValue(row, col) == Zeeslag.FieldValues.SHIP.getValue()) {
            board.setValue(row, col,Zeeslag.FieldValues.HIT.getValue()); // Markeer het getroffen schip
            System.out.println("Gefeliciteerd! Je hebt een schip geraakt op positie " + board.locatie(row, col));
            // Controleert of het schip is gezonken
            if (isSchipGezonken(board, row, col)) {
                System.out.println("Helaas, je schip is gezonken op positie " + board.locatie(row, col));
            }
            return true;
        } else if (board.getValue(row, col) == Zeeslag.FieldValues.EMPTY.getValue()) {
            board.setValue(row, col, Zeeslag.FieldValues.MISS.getValue()); // Markeer de gemiste schoten
            System.out.println("Helaas, je hebt gemist op positie " + board.locatie(row, col));
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
