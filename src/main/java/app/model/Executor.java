package app.model;

import app.exceptions.ParseConfigException;
import app.parser.ConfigParser;
import app.secure.MessageSecure;
import app.view.View;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Executor {
    private final MulticastSocket socket;
    private final Sender sender;
    private final Receiver receiver;
    private final Cleaner cleaner;
    private final View view;

    private final InetSocketAddress socketAddress;
    private final NetworkInterface networkInterface;


    public Executor(View view) throws IOException, ParseConfigException {
        int port, encrypt;
        long timeBetweenClean, disconnectTime, timeBetweenSend;
        String hostName, keyPath;

        ConfigParser parser = new ConfigParser();
        var config = parser.getConfig();

        try {
            hostName = config.get("ipAddress");
            port = Integer.parseInt(config.get("port"));
            timeBetweenClean = Long.parseLong(config.get("timeBetweenClean"));
            disconnectTime = Long.parseLong(config.get("disconnectTime"));
            timeBetweenSend = Long.parseLong(config.get("timeBetweenSend"));
            encrypt = Integer.parseInt(config.get("encrypt"));
        } catch (NumberFormatException e) {
            throw new ParseConfigException(e);
        }

        this.view = view;

        socket = new MulticastSocket(port);
        networkInterface = socket.getNetworkInterface();
        socketAddress = new InetSocketAddress(hostName, port);
        socket.joinGroup(socketAddress, networkInterface);

        Counter counter = new Counter();

        MessageSecure secure;
        if (encrypt == 1) {
            keyPath = config.get("keyPath");
            if (keyPath == null) {
                throw new ParseConfigException("keyPath");
            }

            secure = new MessageSecure(keyPath);
        } else {
            secure = null;
        }

        sender = new Sender(socket, hostName, port, timeBetweenSend, secure);
        receiver = new Receiver(socket, counter, view, secure);
        cleaner = new Cleaner(counter, view, timeBetweenClean, disconnectTime);
    }

    public MulticastSocket getSocket() {
        return socket;
    }

    public View getView() {
        return view;
    }

    public void execute() {
        sender.start();
        receiver.start();
        cleaner.start();
    }


    public void exit() {
        sender.stopThread();
        receiver.stopThread();
        receiver.interrupt();
        cleaner.stopThread();
        cleaner.interrupt();

        try {
            sender.join();
            receiver.join();
            cleaner.join();
        } catch (InterruptedException e) {
           throw new RuntimeException(e);
        }

        try {
            socket.leaveGroup(socketAddress, networkInterface);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        socket.close();
    }
}
