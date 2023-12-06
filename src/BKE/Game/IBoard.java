package BKE.Game;

public interface IBoard {
    public void printBoard();

    public void plaatsSchip(int row, int col);

    public boolean isValidPosition(int row, int col);

    public boolean schepenGezonken();
}
