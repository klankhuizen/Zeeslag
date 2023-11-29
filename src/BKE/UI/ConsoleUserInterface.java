package BKE.UI;

import BKE.ApplicationState;
import java.io.IOException;
import java.util.Scanner;

public class ConsoleUserInterface implements IUserInterface {

    private InputHandler _inputHandler;

    private ApplicationState _scannerState = ApplicationState.HALTED;

    public ConsoleUserInterface() {
        _inputHandler = new InputHandler(_scannerState);
    }

    @Override
    public void Start() {

        _inputHandler.run();
    }

    class InputHandler implements Runnable {

        private Scanner scanner;

        private ApplicationState _state;

        InputHandler(ApplicationState state) {
            _state = state;
            this.scanner = new Scanner(System.in);
        }

        @Override
        public void run() {

            try {
                _state = ApplicationState.RUNNING;
                System.out.println("Welkom! Wat wil je gaan doen?");
                System.out.println("[1] - Offline spelen tegen de computer");
                System.out.println("[2] - Online spelen tegen de anderen");
                System.out.println("[Q] - Afsluiten");

                while (_state == ApplicationState.RUNNING) {
                    String message = this.scanner.next();
                    if (message.isEmpty()){
                        continue;
                    }

                    char choice = message.charAt(0);
                    switch (choice){

                        case '1':
                        case '2':
                            System.out.println("dit is nog niet beschikbaar. Probeer een andere keuze!");
                            break;

                        case 'Q':
                        case 'q':
                            _state = ApplicationState.HALTED;
                            System.exit(0);
                            break;

                        default:
                            System.out.println("Keuze niet herkend! Probeer een van de bovenstaande opties.");
                            break;
                    }


                }
            } catch (Exception e) {
                System.exit(1);
            }
        }
    }

}
