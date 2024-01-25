package BKE.Game.Variants;

import BKE.Helper.Vector2D;

public class Ship {
    private final Vector2D _begin;
    private int _hitpoints;
    private final int _length;
    private final boolean _horizontal;
    private final int _boardWidth;

    public Ship(Vector2D begin, boolean horizontal, int length, int boardWidth) {
        _begin = begin;
        _length = length;
        _hitpoints = length;
        _horizontal = horizontal;
        _boardWidth = boardWidth;
    }

    public Vector2D getBegin(){
        return _begin;
    }

    public Vector2D getEnd(){
        if (_horizontal){
            return new Vector2D(_begin.X + _length, _begin.Y);
        }
        return new Vector2D(_begin.X, _length + _begin.Y);
    }

    public void setSunk(){
        _hitpoints = 0;
    }

    public boolean isAlive() {
        return _hitpoints > 0;
    }

    public void hit(){
        _hitpoints--;
    }

    public int getNetworkBegin(){
        return _begin.Y * _boardWidth + _begin.X;
    }

    public int getNetworkEnd(){
        if (_horizontal){
            return _begin.Y * _boardWidth + _begin.X + _length - 1;
        }

        return _begin.Y * _boardWidth + _begin.X + _boardWidth * (_length - 1);
    }

    public boolean isHorizontal(){
        return _horizontal;
    }

    public int getLength(){
        return _length;
    }
}