package BKE.UI.GUI;

import BKE.Framework;
import BKE.Network.Command.GetGamesCommand;
import BKE.Network.Command.SubscribeCommand;

import javax.swing.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

public class NetworkPanel implements Closeable {
    JComboBox<String> _gamesList;

    JFrame _frame;

    JButton _subscribeButton;

    public NetworkPanel() {
        _gamesList = new JComboBox<String>();
        JLabel header = new JLabel("Select a game");
        _subscribeButton = new JButton("Subscribe to selected game");

        _subscribeButton.addActionListener(e -> {
            subscribeToGame(_gamesList.getSelectedItem().toString());
        });

        _frame = new JFrame();
        _frame.setLayout(new BoxLayout(_frame.getContentPane(), BoxLayout.Y_AXIS));

        _frame.getContentPane().add(header);
        _frame.getContentPane().add(_gamesList);
        _frame.getContentPane().add(_subscribeButton);
        _frame.pack();

        _frame.setVisible(true);

        GetGamesList();
    }

    public void UpdatePlayerList(String[] players){
        // Not really needed
    }

    public void GetGamesList(){
        SwingUtilities.invokeLater(() -> {
            String[] games = new String[0];
            try {
                games = Framework.sendNetworkMessage(new GetGamesCommand());
                UpdateGamesList(games);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void UpdateGamesList(String[] games){
       _gamesList.removeAllItems();
       for (String g : games){
           _gamesList.addItem(g);
       }
    }

    public void subscribeToGame(String game){

        SwingUtilities.invokeLater(() -> {
            try {
                String[] response = Framework.sendNetworkMessage(new SubscribeCommand(game));

                System.out.println(Arrays.toString(response));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void close() throws IOException {
        this._frame.setVisible(false);
    }
}
