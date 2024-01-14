package BKE.UI;

import BKE.UI.GUI.BattleShipPanel;

import javax.swing.*;

public class GraphicalUserInterface implements IUserInterface {

    JFrame _frame;

    @Override
    public void Start() {
        _frame = new JFrame("TEST GUI YOUR MOM");


        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setSize(600, 600);

        BoxLayout layoutOne = new BoxLayout(_frame.getContentPane(), BoxLayout.Y_AXIS);

        _frame.setLayout(layoutOne);

        BattleShipPanel playerOne = new BattleShipPanel( 10, 10, (x, y) -> {
            System.out.println("PLAYERONE " + x + "," + y);
        });
        BattleShipPanel playerTwo = new BattleShipPanel(10, 10, (x, y) -> {
            System.out.println("PLAYERTWO " + x + "," + y);
        });


        JLabel txtPlayerOne = new JLabel("PLAYER ONE");
        JLabel txtPlayerTwo = new JLabel("PLAYER TWO");

        _frame.getContentPane().add(txtPlayerOne);
        _frame.getContentPane().add(playerOne);
        _frame.getContentPane().add(txtPlayerTwo);
        _frame.getContentPane().add(playerTwo);
        _frame.setVisible(true);
    }

    public void UpdateField(int[][] field){
        int length = field.length;
        int height = field[0].length;
    }
}

