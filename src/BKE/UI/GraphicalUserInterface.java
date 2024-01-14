package BKE.UI;

import BKE.Framework;
import BKE.Game.IBoard;
import BKE.UI.GUI.BattleShipPanel;

import javax.swing.*;

public class GraphicalUserInterface implements IUserInterface {

    JFrame _frame;

    BattleShipPanel _playerOne;
    BattleShipPanel _playerTwo;

    @Override
    public void Start() {
        _frame = new JFrame("BATTLESHIP");


        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setSize(600, 600);

        BoxLayout layoutOne = new BoxLayout(_frame.getContentPane(), BoxLayout.Y_AXIS);

        _frame.setLayout(layoutOne);

        IBoard playerBoard = Framework.GetCurrentGame().GetPlayerBoard();
        IBoard opponentBoard = Framework.GetCurrentGame().GetOpponentBoard();
        _playerOne = new BattleShipPanel(opponentBoard.getHeight(), opponentBoard.getWidth(), false, (x, y) -> {
//            System.out.println("PLAYERONE " + x + "," + y);
            Framework.GetCurrentGame().HandleInput(y+1 + "" + ((char)(x + 'A')) );
        });
        _playerTwo = new BattleShipPanel(playerBoard.getHeight(), playerBoard.getWidth(), true, (x, y) -> {
            System.out.println("PLAYERTWO " + x + "," + y);
        });


        JLabel txtPlayerOne = new JLabel("OPPONENT BOARD");
        JLabel txtPlayerTwo = new JLabel("PLAYER BOARD");

        _frame.getContentPane().add(txtPlayerOne);
        _frame.getContentPane().add(_playerOne);
        _frame.getContentPane().add(txtPlayerTwo);
        _frame.getContentPane().add(_playerTwo);
        _frame.setVisible(true);

        Framework.GetCurrentGame().RequestUpdate();
    }

    public void UpdateFields(int[][] playerOneField, int[][] playerTwoField){
        _playerOne.UpdateField(playerOneField);
        _playerTwo.UpdateField(playerTwoField);
    }
}

