package BKE.UI;

import BKE.Framework;
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

        _playerOne = new BattleShipPanel( 10, 10, (x, y) -> {
            System.out.println("PLAYERONE " + x + "," + y);
        });
        _playerTwo = new BattleShipPanel(10, 10, (x, y) -> {
            System.out.println("PLAYERTWO " + x + "," + y);
            Framework.GetCurrentGame().HandleInput(x+1 + "" + ((char)(y + 'A')) );
        });


        JLabel txtPlayerOne = new JLabel("PLAYER ONE");
        JLabel txtPlayerTwo = new JLabel("PLAYER TWO");

        _frame.getContentPane().add(txtPlayerOne);
        _frame.getContentPane().add(_playerOne);
        _frame.getContentPane().add(txtPlayerTwo);
        _frame.getContentPane().add(_playerTwo);
        _frame.setVisible(true);
    }

    public void UpdateFields(int[][] playerOneField, int[][] playerTwoField){
        _playerOne.UpdateField(playerOneField);
        _playerTwo.UpdateField(playerTwoField);
    }
}

