package app.model;

import java.net.MulticastSocket;

public abstract class AbstractMulticastRunnable {
    protected final MulticastSocket socket;

    public AbstractMulticastRunnable(MulticastSocket socket) {
        this.socket = socket;
    }
}
