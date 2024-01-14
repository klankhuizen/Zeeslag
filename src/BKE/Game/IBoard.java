package BKE.Game;

public interface IBoard {
    public void plaatsSchip(int row, int col);

    public boolean isValidPosition(int row, int col);

    public boolean schepenGezonken();

    String locatie(int i, int columnSelection);

    boolean schiet(int i, int columnSelection);

    int[][] getBoard();

    int getWidth();

    int getHeight();

}
