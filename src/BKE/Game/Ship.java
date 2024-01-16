package BKE.Game;

import BKE.Game.Board;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private int size;
    private List<Positie> posities;

    public Ship(int size) {
        this.size = size;
        this.posities = new ArrayList<>();
    }

    public int getsize() {
        return size;
    }

    public List<Positie> getPosities() {
        return posities;
    }

    public void voegPositieToe(Positie positie) {
        posities.add(positie);
    }

    // Methode om het schip op het bord te plaatsen met de juiste size
    public void plaatsOpBord(Board board, int startRow, int startCol, boolean horizontaal) {
        posities.clear(); // Leeg de oude posities voordat je nieuwe toevoegt

        // Horizontaal via de cols
        if (horizontaal) {
            for (int i = 0; i < size; i++) {
                int row = startRow;
                int col = startCol + i;
                posities.add(new Positie(row, col));
                board.plaatsSchip(row, col);
            }
        }
        // Verticaal via de rows
        else {
            for (int i = 0; i < size; i++) {
                int row = startRow + i;
                int col = startCol;
                posities.add(new Positie(row, col));
                board.plaatsSchip(row, col);
            }
        }
    }
}

class Positie {
    private int row;
    private int col;

    public Positie(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
