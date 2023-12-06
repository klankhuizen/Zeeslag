import java.util.Scanner;
import java.util.Random;

public class Game {
    private Board playerBoard;
    private Board opponentBoard;

    public Game() {
        playerBoard = new Board();
        opponentBoard = new Board();
    }

    public void playGame() {
        System.out.println("De ronde van Zeeslag zal zo beginnen");

        // Schepen voor speler plaatsen
        plaatsSchipRandom(playerBoard);

        // Schepen voor tegenstander plaatsen
        plaatsSchipRandom(opponentBoard);

        // Spelronde
        while (!isGameOver()) {
            System.out.println("\nJouw bord:");
            playerBoard.printBoard();

            System.out.println("\nBord van de tegenstander:");
            opponentBoard.printBoard();

            // Beurt van speler
            zetSpeler();

            // Controleer of het spel voorbij is
            if (isGameOver()) {
                break;
            }

            // Beurt van tegenstander
            zetTegenstander();
        }

        // Toon het resultaat
        Resultaat();
    }


    private void plaatsSchipRandom(Board board) {
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

    private void zetSpeler() {
        // De speler kan een vak kiezen om op de schieten
        // Dit doet de speler door het nummer en de letter
        // van de bij behorende row en col aan te geven
        Scanner scanner = new Scanner(System.in);
        System.out.println("Jouw beurt:");
        System.out.print("Voer de rij in (1-8): ");
        int row = scanner.nextInt() - 1;

        System.out.print("Voer de kolom in (A-H): ");
        char colChar = scanner.next().charAt(0);
        int col = colChar - 'A';
    }

    private void zetTegenstander() {
    
    }


    // schepenGezonken is niet klaar en moet nog gemaakt worden.
    private boolean isGameOver() {
        return playerBoard.shepenGezonken() || opponentBoard.shepenGezonken();
    }

    private void Resultaat() {
        System.out.println("Het spel is voorbij!");

        if (playerBoard.shepenGezonken()) {
            System.out.println("De tegenstander heeft gewonnen!");
        } else {
            System.out.println("Je hebt gewonnen!");
        }
    }
}
