package BKE.Game;

import BKE.Helper.Vector2D;

public class Board implements IBoard {
    /**
     * The board matrix
     */
    private int[][] board;

    /**
     * Height of the board
     */
    private int _height;

    /**
     * Width of the board
     */
    private int _width;

    /**
     * Create a new board
     * @param width greater than 1
     * @param height greater than 1
     */
    public Board(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Board must be 1x1 minimum.");
        }

        _width = width;
        _height = height;
        board = new int[width][height];
    }

    @Override
    public boolean isValidPosition(int row, int col) {
        return row > -1 && row < _height && col > -1 && col < _width;
    }

    @Override
    public int[][] getBoard() {
        return board.clone(); // Clone because we don't want them to be able to alter the board
    }

    @Override
    public int getWidth() {
        return _width;
    }

    @Override
    public int getHeight() {
        return _height;
    }

    @Override
    public String locatie(int row, int col) {
        // Hierdoor kan de locatienaam gegeven worden van een bepaald vak
        char colNaam = (char) ('A' + col);
        return colNaam + Integer.toString(row + 1);
    }

    @Override
    public void setValue(int x, int y, int value){
        if (isValidPosition(x, y)){
            board[x][y] = value;
        }
    }

    @Override
    public int getValue(int x, int y) {
        return board[x][y];
    }

    @Override
    public void clear() {
        board = new int[_width][_height];
    }

    @Override
    public Vector2D getFromNetworked(int loc) {
        int x = loc % _width;
        int y = Math.floorDiv(loc, _width);

        return new Vector2D(x, y);
    }
}

