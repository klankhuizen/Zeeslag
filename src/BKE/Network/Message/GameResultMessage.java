package BKE.Network.Message;

public class GameResultMessage {
    private int _playerOneScore = 0;
    private int _playerTwoScore = 0;
    private String _comment;
    public GameResultMessage(int _playerOneScore, int _playerTwoScore, String _comment) {
        this._playerOneScore = _playerOneScore;
        this._playerTwoScore = _playerTwoScore;
        this._comment = _comment;
    }
    public int getPlayerOneScore(){
        return _playerOneScore;
    }
    public int getPlayerTwoScore(){
        return _playerTwoScore;
    }
    public String getComment(){
        return _comment;
    }
}
