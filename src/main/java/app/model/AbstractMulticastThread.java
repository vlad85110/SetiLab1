package app.model;

import java.net.MulticastSocket;

public abstract class AbstractMulticastThread extends AbstractAppThread {
    protected final MulticastSocket socket;

    public AbstractMulticastThread(String name, MulticastSocket socket) {
        super(name);
        this.socket = socket;
    }

    public MulticastSocket getSocket() {
        return socket;
    }
}
