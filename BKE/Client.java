/*
 * Gebruik deze klasse als basis om de verbinding met de server te realiseren
 * je kan het hele protocol van de server met deze klasse testen, probeer bijvoorbeeld de command /login <naam>
 * en kijk wat de server terug geeft
 *
 * om de client aan te maken gebruik Client client = new Client();
 * daarna client.run();
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    private String hostName = "127.0.0.1"; // localhost address
    private int portNumber = 7789; // port nummer van de server

    private Socket client; // aanmaken van de socket
    private BufferedReader in; // aanmaken van input reader
    private PrintWriter out; // aanmaken van output writer
    private boolean done; // boolean voor het bewaren of we klaar zijn (voor disconnect)


    @Override
    public void run()
    {
        try {
            client = new Socket(hostName, portNumber); // we maken een nieuwe Socket genaamd client op de hostname en portname van de server later moet de hostname en port uit args[0] en args[1] gepakt worden zodat we kunnen verbinden met de toeirnooi server
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inputHandler = new InputHandler(); // we maken een InputHander aan, deze is hier beneden gemaakt staat niet in aparte classe
            Thread thread = new Thread(inputHandler); // we zetten de inputhandler op een apparte thread
            thread.start(); //we starten de thread, gebruik nooit run(); dat start geen apparte thread

            String inputMessage; // we maken een input message aan
            while((inputMessage = in.readLine()) != null) { // het moet in een loop zodat we input kunnen blijven verwerken
                System.out.println(inputMessage);
            }
        } catch (IOException e) {
            //TODO handle
        }
    }

    /**
     * Deze methode verbreekt de verbinding met de server
     */
    public void shutdown()
    {
        done = true;
        try {
            in.close();
            out.close();
            if(!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            //we negeren een exception omdat we toch de verbinding verbreken
        }
    }

    /**
     * Deze methode logt in met de opgegeven gebruikersnaam parameter
     * @param name
     */
    public void login(String name)
    {
        System.out.println("Logging in as " + name);
        out.println("login " + name);
    }

    class InputHandler implements Runnable
    {

        @Override
        public void run() {
            try {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inputReader.readLine();
                    if(message.equalsIgnoreCase("/quit")) { // we kijken eerst of de gebruiker de disconnect command gebruikt
                        inputReader.close();
                        shutdown();
                    }
                    if(message.startsWith("/login")) { // daarna kijken we of de gebruiker de login command gebruikt
                        String[] i = message.split(" ", 2);
                        String name = i[1];
                        login(name);
                        //out.println("login " + name);
                    } else { // anders printen we het message
                        out.println(message);
                    }
                }
            } catch (IOException e) { // als er een exception is verbreken we gewoon de verbinding
                shutdown();
            }

        }

    }

}
