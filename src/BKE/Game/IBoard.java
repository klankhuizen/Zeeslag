package BKE.Game;

import BKE.Helper.Vector2D;

public interface IBoard {

    /**
     * Ensures the chosen position is valid for this board
     * @param row X coordinate
     * @param col Y Coordinate
     * @return
     */
    public boolean isValidPosition(int row, int col);

    public boolean isValidPosition(int pos);

    /**
     * Gets the current value on coordinates as string
     * @param x X coordinate
     * @param y Y coordinate
     * @return String value
     */
    String locatie(int x, int y);

    /**
     * Get the current board
     * @return board
     */
    int[][] getValues();

    /**
     * Get the width of the board
     * @return width
     */
    int getWidth();

    /**
     * Get the height of the board
     * @return height
     */
    int getHeight();

    /**
     * Set the value of a coordinate
     * @param x X Coord
     * @param y Y Coord
     * @param value the new value
     */
    void setValue(int x, int y, int value);

    /**
     * Get the current value at x, y
     * @param x X Coord
     * @param y Y Coord
     * @return int value
     */
    int getValue(int x, int y);

    /**
     * Resetting the board.
     */
    void clear();

    Vector2D getFromNetworked(int loc);
}
