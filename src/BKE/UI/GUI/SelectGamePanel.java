package BKE.UI.GUI;

import BKE.Framework;
import BKE.Game.IGame;
import BKE.Game.Player.HumanPlayer;
import BKE.Game.Player.IPlayer;
import BKE.Game.Player.ZeeslagAIPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

public class SelectGamePanel extends JDialog {


    public SelectGamePanel() {

        this.getContentPane().removeAll();

        BoxLayout layoutOne = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);

        this.setLayout(layoutOne);

        HashMap<String, java.lang.reflect.Type> availableGames = Framework.GetAvailableGames();
        Set<String> keys = availableGames.keySet();

        JComboBox comboBox = new JComboBox(keys.toArray());

        JPanel lowerButtons = new JPanel();

        JButton startGame = new JButton("Start Game");
        JButton cancel = new JButton("Cancel");

        lowerButtons.add(startGame);
        lowerButtons.add(cancel);
        lowerButtons.setLayout(new BoxLayout(lowerButtons, BoxLayout.X_AXIS));

        startGame.addActionListener(l -> {
            try {
                Framework.UnloadCurrentGame();
                try {

                    IPlayer playerOne = new HumanPlayer("Hooman");
                    IPlayer playerTwo = new ZeeslagAIPlayer("COMPOOTER");

                    Framework.LoadGame(availableGames.get(comboBox.getSelectedItem().toString()), playerOne, playerTwo);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally{
                this.setVisible(false);
                this.dispose();
            }
        });

        cancel.addActionListener(l -> {

            try {
                Framework.UnloadCurrentGame();
            } catch(Exception e) {
                throw new RuntimeException(e);
            } finally {
                this.setVisible(false);
                this.dispose();
            }

        });


        this.getContentPane().add(comboBox);

        this.getContentPane().add(lowerButtons);


        this.setTitle("Select a game...");

        // Re-size the dialog based on the components inside.
        this.pack();

    }
}
