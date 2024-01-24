package BKE.UI;

import BKE.Framework;
import BKE.Game.IBoard;
import BKE.UI.GUI.BattleShipPanel;
import BKE.UI.GUI.SelectGamePanel;
import BKE.UI.GUI.ServerConnectionPanel;

import javax.swing.*;
import java.io.IOException;

public class GraphicalUserInterface implements IUserInterface {

    /**
     * The frame
     */
    JFrame _frame;

    /**
     * Player one pane
     */
    BattleShipPanel _playerOne;

    /**
     * Player two pane
     */
    BattleShipPanel _playerTwo;

    /**
     * The text area to display status messages from the game.
     */
    JTextArea _textArea;

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
            Framework.GetCurrentGame().HandleInput( y+1 + "" + ((char)(x + 'A')) );
        });

        _playerTwo = new BattleShipPanel(playerBoard.getHeight(), playerBoard.getWidth(), true, (x, y) -> {
            System.out.println("PLAYERTWO " + x + "," + y);
        });

        JLabel txtPlayerOne = new JLabel("OPPONENT BOARD");
        JLabel txtPlayerTwo = new JLabel("PLAYER BOARD");

        _textArea = new JTextArea(1,16);
        JScrollPane _textScrollPane  = new JScrollPane(_textArea);

        _textArea.setLineWrap(true);
        _textArea.setWrapStyleWord(true);
        _textArea.setEditable(false);

        _frame.getContentPane().add(txtPlayerOne);
        _frame.getContentPane().add(_playerOne);
        _frame.getContentPane().add(txtPlayerTwo);
        _frame.getContentPane().add(_playerTwo);
        _frame.getContentPane().add(_textScrollPane);

        Framework.GetCurrentGame().RequestUpdate();
    }

    /**
     * Update the title of the window
     */
    private void UpdateTitle(){
        SwingUtilities.invokeLater(() -> {
            String title = "NO GAME SELECTED";
            if (Framework.GetCurrentGame() != null){
                title = Framework.GetCurrentGame().GetGameName();
            }

            _frame.setTitle(title);
        });

    }

    /**
     * Update the fields on the game
     * @param playerOneField Player one field matrix
     * @param playerTwoField Player two field matrix
     */
    public void UpdateFields(int[][] playerOneField, int[][] playerTwoField){
        SwingUtilities.invokeLater(() -> {
            _playerOne.UpdateField(playerOneField);
            _playerTwo.UpdateField(playerTwoField);
        });
    }

    @Override
    public void SendMessageToUser(String message) {
        SwingUtilities.invokeLater(() -> {
            if (_textArea != null){
                _textArea.append(message + "\n");
            }
        });
    }

    public void CreateMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Menu");

        JMenu networkMenu = new JMenu("Network");

        JMenuItem menuItemNG = new JMenuItem("New Game");
        JMenuItem menuItemCloseGame = new JMenuItem("Stop Game");
        JMenuItem menuItemClose = new JMenuItem("Close");

        JMenuItem menuItemConnectToServer = new JMenuItem("Connect...");
        JMenuItem menuItemDisconnectFromServer = new JMenuItem("Disconnect");

        menuItemNG.addActionListener(e -> {
            System.out.println("Start New Game");
            SelectGamePanel sgp = new SelectGamePanel();
            sgp.setVisible(true);
        });

        menuItemCloseGame.addActionListener(e -> {
            try {
                Framework.UnloadCurrentGame();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        menuItemClose.addActionListener(e -> {

            System.out.println("CLOSE GAME");

            try {

                Framework.GetCurrentGame().close();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        menuItemConnectToServer.addActionListener(e -> {
            ServerConnectionPanel svp = new ServerConnectionPanel();
            svp.setVisible(true);
        });

        menuItemDisconnectFromServer.addActionListener(e -> {
            try {
                Framework.StopNetwork();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        menu.add(menuItemNG);
        menu.add(menuItemCloseGame);
        menu.add(menuItemClose);

        networkMenu.add(menuItemConnectToServer);
        networkMenu.add(menuItemDisconnectFromServer);

        menuBar.add(menu);
        menuBar.add(networkMenu);

        _frame.setJMenuBar(menuBar);
    }

    @Override
    public void close() throws IOException {
        _playerOne.close();
        _playerTwo.close();
        _frame.getContentPane().removeAll();
        _playerOne = null;
        _playerTwo = null;
        _frame.repaint();
    }
}

