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
    private final Thread sender;
    private final Thread receiver;
    private final Thread cleaner;
    private final View view;

    private final InetSocketAddress socketAddress;
    private final NetworkInterface networkInterface;

    private final SenderRunnable senderRunnable;
    private final ReceiverRunnable receiverRunnable;
    private final CleanerRunnable cleanerRunnable;


    public Executor(View view) throws IOException, ParseConfigException {
        int port;
        boolean encrypt;
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
            encrypt = Boolean.parseBoolean(config.get("encrypt"));
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
        if (encrypt) {
            keyPath = config.get("keyPath");
            if (keyPath == null) {
                throw new ParseConfigException("keyPath");
            }

            secure = new MessageSecure(keyPath);
        } else {
            secure = null;
        }

        senderRunnable = new SenderRunnable(socket, hostName, port, timeBetweenSend, secure);
        receiverRunnable = new ReceiverRunnable(socket, counter, view, secure);
        cleanerRunnable = new CleanerRunnable(counter, view, timeBetweenClean, disconnectTime);

        sender = new Thread(senderRunnable);
        receiver = new Thread(receiverRunnable);
        cleaner = new Thread(cleanerRunnable);
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
        senderRunnable.stopRunnable();
        receiverRunnable.stopRunnable();
        cleanerRunnable.stopRunnable();

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
