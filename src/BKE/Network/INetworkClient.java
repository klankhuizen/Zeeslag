package BKE.Network;

import java.io.IOException;

public interface INetworkClient {
    String[] send(NetworkCommand command) throws IOException;

    void connect(String host, int port);

    int status();

    void disconnect() throws IOException;
}
