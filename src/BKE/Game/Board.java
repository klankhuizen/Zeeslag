package BKE.Game;

import BKE.Game.Variants.Zeeslag;

public class Board implements IBoard {
    private int[][] board;

    private int _height;
    private int _width;

    public Board(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Board must be 1x1 minimum.");
        }

        _width = width;
        _height = height;
        board = new int[width][height];
        BoardOn();

    }

    private void BoardOn() {
        // Hier wordt een loop gemaakt om het veld te activeren met lege
        // vakjes door middel van -
        for (int i  = 0; i < _height; i ++) {

            for (int j = 0; j < _width; j++) {

                board[i][j] = Zeeslag.FieldValues.EMPTY.getValue();

            }
        }
    }

    private boolean juistePositie(int row, int col) {
        // Hier wordt er gekeken of de positie juist is die gekozen werd op het veld
        return row >= 0 && row < _height && col >= 0 && col < _width && board[row][col] == 0;

    }

    public void plaatsSchip(int row, int col) {
        // Hier wordt er gecontroleerd of de posiitie juist is.
        // Zo ja krijgt de speler een melding dat het plaatsen gelukt is.
        if (juistePositie(row, col)) {
            board[row][col] = Zeeslag.FieldValues.SHIP.getValue();
            System.out.println("Schip geplaatst op positie " + locatie(row, col));
        }
        else {
            System.out.println("je kan hier geen schip plaatsen");
        }
    }

    public boolean isValidPosition(int row, int col) {
        // Controleer of de zet binnen het bord valt
        // Ik weet niet of dit zal werken
        return row >= 0 && row < _height && col >= 0 && col < _width;
    }

    @Override
    public boolean schepenGezonken() {
        // Loop door het bord en controleer of er nog 'O' (schepen) aanwezig zijn
        for (int i = 0; i < _width; i++) {
            for (int j = 0; j < _height; j++) {
                if (board[i][j] == Zeeslag.FieldValues.SHIP.getValue()) {
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
        if (board[row][col] == Zeeslag.FieldValues.SHIP.getValue()) {
            board[row][col] = Zeeslag.FieldValues.HIT.getValue(); // Markeer het getroffen schip
            System.out.println("Gefeliciteerd! Je hebt een schip geraakt op positie " + locatie(row, col));
            // Controleert of het schip is gezonken
            if (isSchipGezonken(row, col)) {
                System.out.println("Helaas, je schip is gezonken op positie " + locatie(row, col));
            }
            return true;
        } else if (board[row][col] == Zeeslag.FieldValues.EMPTY.getValue()) {
            board[row][col] = Zeeslag.FieldValues.MISS.getValue(); // Markeer de gemiste schoten
            System.out.println("Helaas, je hebt gemist op positie " + locatie(row, col));
        } else {
            System.out.println("Je hebt hier al geschoten. Probeer een andere positie.");
        }
        return false;
    }

    @Override
    public int[][] getBoard() {
        return board;
    }

    @Override
    public int getWidth() {
        return _width;
    }

    @Override
    public int getHeight() {
        return _height;
    }

    private boolean isSchipGezonken(int row, int col) {
        // Controleer horizontaal
        int countHorizontaal = 0;
        for (int j = 0; j < 8; j++) {
            if (board[row][j] == Zeeslag.FieldValues.HIT.getValue()) {
                countHorizontaal++;
            }
        }

        // Controleer verticaal
        int countVerticaal = 0;
        for (int i = 0; i < 8; i++) {
            if (board[i][col] == Zeeslag.FieldValues.HIT.getValue()) {
                countVerticaal++;
            }
        }

        // Als alle vakjes van het schip zijn geraakt, is het schip gezonken
        // TODO This needs to be different
        return countHorizontaal == 5 || countVerticaal == 5;
    }

    public String locatie(int row, int col) {
        // Hierdoor kan de locatienaam gegeven worden van een bepaald vak
        char colNaam = (char) ('A' + col);
        return colNaam + Integer.toString(row + 1);
    }
}

