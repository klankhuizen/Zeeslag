package BKE.UI;

import java.io.Closeable;

public interface IUserInterface extends Closeable {

    public void Start();

    public void UpdateFields(int[][] playerOne, int[][] playerTwo);

}
