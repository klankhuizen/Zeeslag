package BKE.UI;

import BKE.Framework;
import BKE.Game.IBoard;
import BKE.Game.Player.IPlayer;
import BKE.Network.Message.GameResultMessage;
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
    BattleShipPanel _playerOnePane;

    /**
     * Player two pane
     */
    BattleShipPanel _playerTwoPane;

    /**
     * The text area to display status messages from the game.
     */
    JTextArea _textArea;

    /**
     * The name for player one
     */
    JLabel _playerOneName = new JLabel("PLAYER 1");

    /**
     * The name for player two
     */
    JLabel _playerTwoName = new JLabel("Player 2");

    @Override
    public void Start() {
        SwingUtilities.invokeLater(() -> {
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
        });
    }

    /**
     * Initialize the boards for player one and two.
     */
    private void InitializeBoards(){
        SwingUtilities.invokeLater(() -> {
            if (Framework.GetCurrentGame() == null) return;
            IBoard playerBoard = Framework.GetCurrentGame().GetPlayerBoard();
            IBoard opponentBoard = Framework.GetCurrentGame().GetOpponentBoard();

            _playerOnePane = new BattleShipPanel(playerBoard.getHeight(), playerBoard.getWidth(), !Framework.GetCurrentGame().getPlayerOne().isRemote(), (x, y) -> {
                Framework.GetCurrentGame().HandleInput( x+1 + "" + ((char)(y + 'A')) );
            });

            _playerTwoPane = new BattleShipPanel(opponentBoard.getHeight(), opponentBoard.getWidth(), !Framework.GetCurrentGame().getPlayerTwo().isRemote(), (x, y) -> {
                System.out.println("PLAYERTWO " + x + "," + y);
            });

            _playerOneName = new JLabel("OPPONENT BOARD");
            _playerTwoName = new JLabel("PLAYER BOARD");

            _textArea = new JTextArea(1,16);
            JScrollPane _textScrollPane  = new JScrollPane(_textArea);

            _textArea.setLineWrap(true);
            _textArea.setWrapStyleWord(true);
            _textArea.setEditable(false);

            _frame.getContentPane().add(_playerOneName);
            _frame.getContentPane().add(_playerOnePane);
            _frame.getContentPane().add(_playerTwoName);
            _frame.getContentPane().add(_playerTwoPane);
            _frame.getContentPane().add(_textScrollPane);

            // Request an update from the framework after the panels are ready.
            Framework.GetCurrentGame().RequestUpdate();
        });
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
     * @param playerOne Player one field matrix
     * @param playerTwo Player two field matrix
     */
    public void UpdateFields(IPlayer playerOne, IPlayer playerTwo){
        SwingUtilities.invokeLater(() -> {
            _playerOnePane.UpdateField(playerOne.getBoard().getValues());
            _playerTwoPane.UpdateField(playerTwo.getBoard().getValues());

            _playerOneName.setText(playerOne.getName());
            _playerTwoName.setText(playerTwo.getName());
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

    @Override
    public void setWinner(GameResultMessage gsm) {
        String[] appendices = {"LOSER", "DRAW", "WINNER"};

        _playerOneName.setText(_playerOneName.getText() + " - " + appendices[gsm.getPlayerOneScore() + 1]);
        _playerTwoName.setText(_playerTwoName.getText() + " - " + appendices[gsm.getPlayerTwoScore() + 1]);
    }

    /**
     * Creates a menu bar and menu bar accessories.
     */
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
        _playerOnePane.close();
        _playerTwoPane.close();
        _frame.getContentPane().removeAll();
        _playerOnePane = null;
        _playerTwoPane = null;
        _frame.repaint();
    }
}

