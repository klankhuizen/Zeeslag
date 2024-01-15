package BKE.Game.Variants;

import BKE.ApplicationState;
import BKE.Framework;
import BKE.Game.Board;
import BKE.Game.IBoard;
import BKE.Game.IGame;

import java.awt.*;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Zeeslag implements IGame {

    private final String _name = "ZEESLAG";
    private ApplicationState _state;
    private IBoard _playerBoard;
    private IBoard _opponentBoard;

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
            plaatsSchipRandom(_playerBoard);

            // Schepen voor tegenstander plaatsen
            plaatsSchipRandom(_opponentBoard);

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

        System.out.println("Input: " + input + " translates to " + _rowSelection + " "+ _columnSelection);

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
        System.out.println("Closing Zeeslag");
        if (_thread != null){
            _thread.stop();
            _thread = null;
        }
        _state = ApplicationState.HALTED;
    }

    public Zeeslag() {}

    private void plaatsSchipRandom(IBoard board) {
        Random random = new Random();
        // Plaatst schepen op willekeurige locaties op het bord
        for (int i = 1; i <= 5; i++) {
            int row = random.nextInt(8);
            int col = random.nextInt(8);

            // Controleer of de willekeurig gekozen positie geldig is
            // zo niet, probeer opnieuw
            while (!board.isValidPosition(row, col)) {
                row = random.nextInt(8);
                col = random.nextInt(8);
            }

            board.plaatsSchip(row, col);
        }
    }

    private void zetSpeler() throws InterruptedException {
        // De speler kan een vak kiezen om op de schieten
        // Dit doet de speler door het nummer en de letter
        // van de bij behorende row en col aan te geven
        _playerTurn = true;

        _columnSelection = -1;
        _rowSelection = 0;

        System.out.println("Jouw beurt:");
        System.out.print("Voer coordinaten in:(1-8 + A-H) (1A, 4D, 8B)");

        while(_rowSelection == 0 && _columnSelection < 0){
            Thread.sleep(100);
        }

        // Voer het schot uit op het bord van de tegenstander
        boolean hit = _opponentBoard.schiet(_rowSelection - 1, _columnSelection);

        // Toon de resultaten van het schot aan de speler
        if (hit) {
            System.out.println("Gefeliciteerd! Je hebt een schip geraakt op positie " + _opponentBoard.locatie(_rowSelection - 1, _columnSelection));
        } else {
            System.out.println("Helaas, je hebt gemist op positie " + _opponentBoard.locatie(_rowSelection - 1, _columnSelection));
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
        boolean hit = _playerBoard.schiet(row, col);

        // Toon de resultaten van het schot aan de speler
        if (hit) {
            System.out.println("De tegenstander heeft een schip geraakt op positie " + _playerBoard.locatie(row, col));
        } else {
            System.out.println("De tegenstander heeft gemist op positie " + _playerBoard.locatie(row, col));
        }
    }
/*
    private boolean vraagOpnieuwSpelen() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Wil je het spel opnieuw spelen? (ja/nee): ");
        String antwoord = scanner.nextLine().toLowerCase();
        return antwoord.equals("ja");
    }
*/

    // schepenGezonken is niet klaar en moet nog gemaakt worden.
    private boolean isGameOver() {
        return _playerBoard.schepenGezonken() || _opponentBoard.schepenGezonken();
    }

    private void Resultaat() {
        System.out.println("Het spel is voorbij!");

        if (_playerBoard.schepenGezonken()) {
            System.out.println("De tegenstander heeft gewonnen!");
        } else {
            System.out.println("Je hebt gewonnen!");
        }

        /*if (vraagOpnieuwSpelen()) {
            resetSpel();
        } else {
            _state = ApplicationState.HALTED;
        }*/
    }
/*
    private void resetSpel() {
        // Zet de borden opnieuw op
        _playerBoard = new Board();
        _opponentBoard = new Board();

        // Start een nieuwe ronde
        start();
    }
*/
    private void SwitchSides(){
        _playerTurn = !_playerTurn;
        if (_playerTurn){
            System.out.println("Jouw beurt:");
            System.out.print("Voer de rij in (1-8): ");
        }
    }


}
