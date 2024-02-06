package BKE.Network;

import java.io.IOException;

public interface INetworkClient {
    String[] send(NetworkCommand command) throws IOException, InterruptedException;

    void connect(String host, int port);

    int status();

    void disconnect() throws IOException;

    void setUserName(String userName);
}
