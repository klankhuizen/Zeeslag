package BKE.UI;

import BKE.ApplicationState;
import BKE.Framework;
import BKE.Game.Variants.TicTacToe;
import BKE.Game.Variants.Zeeslag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUserInterface implements IUserInterface {

    private Map<Integer, Character> _printValues = Map.of(
           Zeeslag.FieldValues.EMPTY.getValue(), '-',
           Zeeslag.FieldValues.HIT.getValue(), 'X',
           Zeeslag.FieldValues.MISS.getValue(), 'M',
           Zeeslag.FieldValues.SHIP.getValue(), 'O'
    );

    private StartupChoiceHandler _inputHandler;

    private ApplicationState _scannerState = ApplicationState.HALTED;

    public ConsoleUserInterface() {
        _inputHandler = new StartupChoiceHandler(_scannerState);
    }

    @Override
    public void Start() {

        _inputHandler.run();
    }

    @Override
    public void UpdateFields(int[][] playerOne, int[][] playerTwo) {

        PrintBoard(playerOne, false);
        PrintBoard(playerTwo, true);

    }

    public void PrintBoard(int[][] board, boolean showShips){
        // Hier worden de verschilende vakken letters gegeven (de bovenste rij)
        // Dit is om het overzichtelijk te maken voor de speler in welk vak hij zijn ship
        // plaatst en zal op schieten
        StringBuilder horizontalLegend = new StringBuilder("  ");
        for (int i = 0; i < board.length; i++){
            horizontalLegend.append((char)('A' + i)).append(" ");
        }

        System.out.println(horizontalLegend);

        // Door middel van een loop wordt het bord uit geprint met de actuele informatie
        for (int i = 0; i < board.length; i++) {

            StringBuilder rowString = new StringBuilder((i + 1 + "")).append(" ");

            for (int j = 0; j < board[0].length; j++) {
                int value = board[i][j];
                if (showShips || Zeeslag.FieldValues.SHIP.getValue() != value){
                    rowString.append(((char)_printValues.get(value))).append(" ");
                } else{
                    rowString.append(((char)_printValues.get(Zeeslag.FieldValues.EMPTY.getValue()))).append(" ");
                }

            }

            System.out.println(rowString);

        }

        System.out.println();
    }

    static class StartupChoiceHandler implements Runnable {

        private Scanner scanner;

        private ApplicationState scannerState;

        StartupChoiceHandler(ApplicationState state) {
            scannerState = state;
            this.scanner = new Scanner(System.in);
        }

        private void ShowMenu(){
            System.out.println("------------------------------------------------");
            System.out.println("Welkom! Wat wil je gaan doen?");
            System.out.println("[1] - Zeeslag: Offline spelen tegen de computer");
            System.out.println("[2] - Zeeslag: Online spelen tegen de anderen");
            System.out.println("[3] - TicTacToe: Offline spelen tegen de computer");
            System.out.println("[4] - TicTacToe: Online spelen tegen de anderen");
            System.out.println("[Q] - Afsluiten");
        }

        @Override
        public void run() {

            try {
                scannerState = ApplicationState.RUNNING;

                ShowMenu();
                while (scannerState == ApplicationState.RUNNING) {
                    String message = this.scanner.next();
                    if (message.isEmpty()){
                        continue;
                    }

                    if (Framework.GetCurrentGame() != null){
                        handleInGameInput(message);
                    } else{
                        handleOutOfGameInput(message);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        /**
         * Handles input and passes it through to a currently playing game
         * @param message User input
         * @throws IOException Error with game
         */
        private void handleInGameInput(String message) throws IOException {
            // If the user presses Q whilst in a game, the game should quit
            if (message.length() == 1 && message.toLowerCase().charAt(0) == 'q'){
                Framework.UnloadCurrentGame();
                ShowMenu();
                return;
            }

            // The user did not press Q, the game continues and any input is passed
            // through to the game to handle.
            Framework.GetCurrentGame().HandleInput(message);
        }

        /**
         * Handles input when there is no game currently being played
         * @param message user input
         * @throws IOException Error with game
         */
        private void handleOutOfGameInput(String message) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            if (message.length() == 1) {
                char choice = message.charAt(0);
                switch (choice){

                    case '1':
                        Framework.LoadGame(Zeeslag.class, false);
                        break;
                    case '2':
                        Framework.LoadGame(Zeeslag.class, true);
                        break;

                    case '3':
                        Framework.LoadGame(TicTacToe.class, false);
                        break;
                    case '4':
                        Framework.LoadGame(TicTacToe.class, true);
                        break;

                    case 'Q':
                    case 'q':
                        scannerState = ApplicationState.HALTED;
                        System.exit(0);
                        break;

                    default:
                        if (Framework.GetCurrentGame() == null){
                            System.out.println("Keuze niet herkend! Probeer een van de bovenstaande opties.");
                            break;
                        }

                        Framework.GetCurrentGame().HandleInput(message);
                }
            } else {
                System.out.println("Invalid Input");
            }
        }
    }

}
