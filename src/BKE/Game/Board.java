package BKE.Game;

import BKE.Game.Player.Player;

public class Board implements IBoard {

    private char[][] veld;

    public Board() {
        // Hier wordt er een bord van 8 bij 8 gedefineerd
        veld = new char[8][8];
        BoardOn();

    }

    private void BoardOn() {
        // Hier wordt een loop gemaakt om het veld te activeren met lege
        // vakjes door middel van -
        for (int i  = 0; i < 8; i ++) {

            for (int j = 0; j < 8; j++) {

                veld[i][j] = '-';

            }
        }
    }

    private boolean juistePositie(int row, int col) {
        // Hier wordt er gekeken of de positie juist is die gekozen werd op het veld
        return row >= 0 && row < 8 && col >= 0 && col < 8 && veld[row][col] == '-';

    }

    public void printBoard() {
        // Hier worden de verschilende vakken letters gegeven (de bovenste rij)
        // Dit is om het overzichtelijk te maken voor de speler in welk vak hij zijn ship
        // plaatst en zal op schieten
        System.out.println("  A B C D E F G H");
        // Door middel van een loop wordt het bord uit geprint met de actuele informatie
        for (int i = 0; i < 8; i++) {

            System.out.print((i + 1) + " ");

            for (int j = 0; j < 8; j++) {

                System.out.print(veld[i][j] + " ");

            }

            System.out.println();

        }

        System.out.println();

    }

    public void plaatsSchip(int row, int col) {
        // Hier wordt er gecontroleerd of de posiitie juist is.
        // Zo ja krijgt de speler een melding dat het plaatsen gelukt is.
        if (juistePositie(row, col)) {

            veld[row][col] = 'O';

            System.out.println("Schip geplaatst op positie " + locatie(row, col));

        }

        else {

            System.out.println("je kan hier geen schip plaatsen");

        }
    }

    public boolean isValidPosition(int row, int col) {
        // Controleer of de zet binnen het bord valt
        // Ik weet niet of dit zal werken
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    @Override
    public boolean schepenGezonken() {
        // Loop door het bord en controleer of er nog 'O' (schepen) aanwezig zijn
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (veld[i][j] == 'O') {
                    // Er is nog minstens één schip aanwezig
                    return false;
                }
            }
        }
        // Alle schepen zijn gezonken
        return true;

    }

    public boolean schiet(int row, int col) {
        // Controleer of de zet binnen het bord valt
        if (!isValidPosition(row, col)) {
            System.out.println("Ongeldige positie. Probeer opnieuw.");
            return false;
        }

        // Controleer of er een schip op de opgegeven positie is
        if (veld[row][col] == 'O') {
            veld[row][col] = 'X'; // Markeer het getroffen schip
            System.out.println("Gefeliciteerd! Je hebt een schip geraakt op positie " + locatie(row, col));
            return true;
        } else if (veld[row][col] == '-') {
            veld[row][col] = 'M'; // Markeer de gemiste schoten
            System.out.println("Helaas, je hebt gemist op positie " + locatie(row, col));
        } else {
            System.out.println("Je hebt hier al geschoten. Probeer een andere positie.");
        }
        return false;
    }

    public String locatie(int row, int col) {
        // Hierdoor kan de locatienaam gegeven worden van een bepaald vak
        char colNaam = (char) ('A' + col);

        return colNaam + Integer.toString(row + 1);


    }
}

