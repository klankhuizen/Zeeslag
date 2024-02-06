package BKE.UI.GUI;

import BKE.Framework;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerConnectionPanel extends JDialog {

    private static final String IPV4_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";

    Pattern _pattern;

    public ServerConnectionPanel() {

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        _pattern = Pattern.compile(IPV4_REGEX);

        Label label = new Label("Server");
        TextField serverField = new TextField("127.0.0.1");

        Label label2 = new Label("Port");
        TextField portField = new TextField("7789");

        Label userNameLabel = new Label("User Name");
        TextField userNameTextField = new TextField("spraakgebrek");

        Label errorLabel = new Label("");

        JPanel lowerButtons = new JPanel();

        lowerButtons.setLayout(new BoxLayout(lowerButtons, BoxLayout.X_AXIS));

        JButton cancelButton = new JButton("Cancel");
        JButton connectButton = new JButton("Connect");

        cancelButton.addActionListener(e -> {
            this.setVisible(false);
            this.dispose();
        });

        connectButton.addActionListener(e -> {
            Matcher matcher = _pattern.matcher(serverField.getText());
            if (!matcher.matches()){
                errorLabel.setText("IP Address invalid");
                return;
            }

            if (userNameTextField.getText() .isEmpty()){
                errorLabel.setText("Username is empty");
                return;
            }

            if (portField.getText().isEmpty()){
                errorLabel.setText("Port is empty");
                return;
            }

            int port;
            try{
                port = Integer.parseInt(portField.getText());

            } catch (Exception ex){
                errorLabel.setText("Invalid port");
                return;
            }

            try {
                Framework.StartNetwork(serverField.getText(), port, userNameTextField.getText());
            } catch (IOException | InterruptedException ex) {
                errorLabel.setText("Could not connect: " + ex.getMessage());
                throw new RuntimeException(ex);
            }

            this.setVisible(false);
            this.dispose();
        });

        this.getContentPane().add(errorLabel);
        this.getContentPane().add(label);
        this.getContentPane().add(serverField);
        this.getContentPane().add(label2);
        this.getContentPane().add(portField);
        this.getContentPane().add(userNameLabel);
        this.getContentPane().add(userNameTextField);

        lowerButtons.add(cancelButton);
        lowerButtons.add(connectButton);
        this.getContentPane().add(lowerButtons);

        this.pack();
    }
}
