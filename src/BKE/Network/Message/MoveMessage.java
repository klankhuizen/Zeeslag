package BKE.Network.Message;

import BKE.Game.Variants.Zeeslag;

public class MoveMessage {
    private int _loc;
    private String _playerName;
    private Zeeslag.FieldValues _value;

    public MoveMessage(int loc, String playerName, String data) {
        _loc = loc;
        _playerName = playerName;
        switch (data){
            case "GEZONKEN":
                _value = Zeeslag.FieldValues.GEZONKEN;
                break;
            case "BOEM":
                _value = Zeeslag.FieldValues.HIT;
                break;
            case "PLONS":
                _value = Zeeslag.FieldValues.MISS;
                break;
            default:
                throw new RuntimeException("Wrong move message: " + data);
        }
    }

    public String getPlayerName() { return _playerName; }

    public int getLocation() { return _loc; }

    public Zeeslag.FieldValues getValue(){ return _value; }
}
