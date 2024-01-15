package BKE.UI;

import BKE.Framework;
import BKE.Game.IBoard;
import BKE.Game.IGame;
import BKE.UI.GUI.BattleShipPanel;
import BKE.UI.GUI.SelectGamePanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class GraphicalUserInterface implements IUserInterface {

    JFrame _frame;

    BattleShipPanel _playerOne;
    BattleShipPanel _playerTwo;

    @Override
    public void Start() {
        if (_frame == null){

            _frame = new JFrame("");

        }

        UpdateTitle();

        _frame.getContentPane().removeAll();

        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setSize(400, 800);

        BoxLayout layoutOne = new BoxLayout(_frame.getContentPane(), BoxLayout.Y_AXIS);

        _frame.setLayout(layoutOne);

        InitializeBoards();

        CreateMenuBar();

        _frame.setVisible(true);

    }

    private void InitializeBoards(){

       if (Framework.GetCurrentGame() == null) return;


        IBoard playerBoard = Framework.GetCurrentGame().GetPlayerBoard();
        IBoard opponentBoard = Framework.GetCurrentGame().GetOpponentBoard();
        _playerOne = new BattleShipPanel(opponentBoard.getHeight(), opponentBoard.getWidth(), false, (x, y) -> {
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

        Framework.GetCurrentGame().RequestUpdate();

    }

    private void UpdateTitle(){
        String title = "NO GAME SELECTED";
        if (Framework.GetCurrentGame() != null){
            title = Framework.GetCurrentGame().GetGameName();
        }

        _frame.setTitle(title);
    }

    public void UpdateFields(int[][] playerOneField, int[][] playerTwoField){
        _playerOne.UpdateField(playerOneField);
        _playerTwo.UpdateField(playerTwoField);
    }

    public void CreateMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Menu");

        JMenu gameMenu = new JMenu("Game");
        JMenuItem menuItemNG = new JMenuItem("New Game");
        JMenuItem menuItemClose = new JMenuItem("Close");

        menuItemNG.addActionListener(e -> {

            System.out.println("Start New Game");
            SelectGamePanel sgp = new SelectGamePanel();
            sgp.setVisible(true);
        });

        menuItemClose.addActionListener(e -> {

            System.out.println("CLOSE GAME");

            try {
                Framework.GetCurrentGame().close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        menu.add(menuItemNG);
        menu.add(menuItemClose);

        menuBar.add(menu);

        _frame.setJMenuBar(menuBar);
    }
}

