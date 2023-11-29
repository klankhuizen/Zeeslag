/*
 * Gebruik deze klasse als basis om de verbinding met de server te realiseren
 * je kan het hele protocol van de server met deze klasse testen, probeer bijvoorbeeld de command /login <naam>
 * en kijk wat de server terug geeft
 *
 * om de client aan te maken gebruik Client client = new Client();
 * daarna client.run();
 */
package BKE.Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client implements Runnable {

    private String hostName = "127.0.0.1"; // localhost address
    private int portNumber = 7789; // port nummer van de server

    private Socket client; // aanmaken van de socket
    private BufferedReader in; // aanmaken van input reader
    private PrintWriter out; // aanmaken van output writer
    private boolean done; // boolean voor het bewaren of we klaar zijn (voor disconnect)

    private String currentPlayer; // houdt de huidige speler bij

    @Override
    public void run() {
        try {
            client = new Socket(hostName, portNumber);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread thread = new Thread(inputHandler);
            thread.start();

            String inputMessage;
            while ((inputMessage = in.readLine()) != null) {
                System.out.println(inputMessage);

                if (inputMessage.startsWith("SVR GAME MATCH")) {
                    System.out.println("Nu bezig met een match!");
                    // logica om huidige speler bij te houden
                    String[] parts = inputMessage.split(" ");
                    currentPlayer = parts[5].replace("\"", ""); // Haal de speler uit de berichttekst
                } else if (inputMessage.startsWith("SVR GAME MOVE")) {
                    // Verwerkt een zetbericht
                    processMoveResult(inputMessage);
                } else if (inputMessage.startsWith("SVR GAME")) {
                    // Verwerkt een resultaatbericht van de match
                    processMatchResult(inputMessage);
                } else if (inputMessage.startsWith("SVR GAME CHALLENGE")) {
                    // Verwerkt een uitdaging
                    processChallenge(inputMessage);
                } else if (inputMessage.startsWith("SVR GAME CHALLENGE CANCELLED")) {
                    // Verwerkt een geannuleerde uitdaging
                    processCancelledChallenge(inputMessage);
                }
            }
        } catch (IOException e) {
            
        }
    }

    private void processMoveResult(String moveMessage) {
        String[] parts = moveMessage.split(" ");
        String player = parts[3].replace("\"", ""); // Haal de speler uit de berichttekst
        String move = parts[5].replace("\"", ""); // Haal de zet uit de berichttekst
        String details = parts[7].replace("\"", ""); // Haal de details uit de berichttekst

        System.out.println("Er is een zet gedaan door " + player + ": " + move);
        System.out.println("Reactie van het spel: " + details);
    }

    private void processMatchResult(String matchResult) {
        String[] parts = matchResult.split(" ");
        String playerResult = parts[3].replace("\"", ""); // Haal het resultaat van de speler uit de berichttekst
        String scorePlayer1 = parts[5].replace("\"", ""); // Haal de score van speler 1 uit de berichttekst
        String scorePlayer2 = parts[7].replace("\"", ""); // Haal de score van speler 2 uit de berichttekst
        String comment = parts[9].replace("\"", ""); // Haal het commentaar uit de berichttekst

        System.out.println("De match is afgelopen.");
        System.out.println("Resultaat voor " + currentPlayer + ": " + playerResult);
        System.out.println("Score speler 1: " + scorePlayer1);
        System.out.println("Score speler 2: " + scorePlayer2);
        System.out.println("Commentaar: " + comment);
    }

    private void processChallenge(String challengeMessage) {
        String[] parts = challengeMessage.split(" ");
        String challenger = parts[5].replace("\"", ""); // Haal de uitdager uit de berichttekst
        String gameType = parts[7].replace("\"", ""); // Haal het speltype uit de berichttekst
        String challengeNumber = parts[9].replace("\"", ""); // Haal het uitdagingsnummer uit de berichttekst

        System.out.println(challenger + " heeft je uitgedaagd voor een speltype: " + gameType);
        System.out.println("Eerder gemaakte uitdagingen zijn komen te vervallen.");
        System.out.println("Gebruik quotes als er een spatie in een naam zit.");
    }

    private void processCancelledChallenge(String cancelledChallenge) {
        String[] parts = cancelledChallenge.split(" ");
        String challengeNumber = parts[5].replace("\"", ""); // Haal het uitdagingsnummer uit de berichttekst

        System.out.println("Uitdaging " + challengeNumber + " is vervallen.");
        System.out.println("Mogelijke oorzaken: speler heeft een andere uitdaging gestart, speler is een match begonnen, speler heeft de verbinding verbroken.");
    }

    private void sendAcceptChallenge(String challengeNumber) {
        // Stuur het acceptatiebericht naar de server
        out.println("challenge accept " + challengeNumber);
    }

    public void subscribeToGame(String gameType) {
        out.println("subscribe " + gameType);
        System.out.println("Je hebt je ingeschreven voor het speltype: " + gameType);
    }

    public void makeMove(int move) {
        out.println("move " + move);
        System.out.println("Je hebt zet " + move + " gedaan.");
    }

    /**
     * Deze methode verbreekt de verbinding met de server
     */
    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            //we negeren een exception omdat we toch de verbinding verbreken
        }
    }

    /**
     * Deze methode logt in met de opgegeven gebruikersnaam parameter
     *
     * @param name
     */
    public void login(String name) {
        System.out.println("Logging in as " + name);
        out.println("login " + name);
    }


    class InputHandler implements Runnable {

        private Scanner scanner;

        InputHandler() {
            this.scanner = new Scanner(System.in);
        }

        @Override
        public void run() {
            try {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inputReader.readLine();
                    if (message.equalsIgnoreCase("/quit")) {
                        inputReader.close();
                        shutdown();
                    }
                    if (message.startsWith("/login")) {
                        String[] i = message.split(" ", 2);
                        String name = i[1];
                        login(name);
                    } else if (message.equalsIgnoreCase("/get gamelist")) {
                        out.println("get gamelist");
                    } else if (message.equalsIgnoreCase("/get playerlist")) {
                        out.println("get playerlist");
                    } else if (message.startsWith("/subscribe")) {
                        String[] parts = message.split(" ", 2);
                        String gameType = parts[1];
                        out.println("subscribe " + gameType);
                    } else if (message.startsWith("/challenge")) {
                        String[] parts = message.split(" ");
                        String player = parts[1].replace("\"", ""); // Haal de speler uit de berichttekst
                        String gameType = parts[2].replace("\"", ""); // Haal het speltype uit de berichttekst
                        out.println("challenge " + player + " " + gameType);
                    } else if (message.startsWith("/challenge accept")) {
                        String[] parts = message.split(" ");
                        String challengeNumber = parts[2].replace("\"", ""); // Haal het uitdagingsnummer uit de berichttekst
                        sendAcceptChallenge(challengeNumber);
                    //else if (message.startsWith("/move")) {
                   //         int move = getMoveFromUser();
                   //         makeMove(move);
                    //    }
                    } else {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

        public int getMoveFromUser() {
            int move = -1;
            boolean validInput = false;

            while (!validInput) {
                Scanner scanner = null;
                try {
                    System.out.print("Voer je zet in: ");
                    move = scanner.nextInt();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Ongeldige invoer. Voer a.u.b. een geldig getal in.");
                    scanner.nextLine(); // Wis de ongeldige invoer
                }
            }

            return move;
        }
    }
