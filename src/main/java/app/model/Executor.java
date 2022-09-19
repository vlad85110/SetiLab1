package app.model;

import app.exceptions.ParseConfigException;
import app.parser.ConfigParser;
import app.secure.MessageSecure;
import app.view.View;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.concurrent.*;

public class Executor {
    private final MulticastSocket socket;
    private final Thread receiver;
    private final ScheduledExecutorService sender;
    private final ScheduledExecutorService cleaner;
    private final InetSocketAddress socketAddress;
    private final NetworkInterface networkInterface;
    private ScheduledFuture<?> cleanerFuture;
    private ScheduledFuture<?> senderFuture;
    private final SenderRunnable senderRunnable;
    private final CleanerRunnable cleanerRunnable;

    private final long timeBetweenClean;
    private final long timeBetweenSend;

    public Executor(View view, String address) throws IOException, ParseConfigException {
        int port;
        boolean encrypt;
        long disconnectTime;
        String keyPath;

        ConfigParser parser = new ConfigParser();
        var config = parser.getConfig();

        try {
            if (address == null) {
                address = config.get("ipAddress");
            }

            port = Integer.parseInt(config.get("port"));
            timeBetweenClean = Long.parseLong(config.get("timeBetweenClean"));
            disconnectTime = Long.parseLong(config.get("disconnectTime"));
            timeBetweenSend = Long.parseLong(config.get("timeBetweenSend"));
            encrypt = Boolean.parseBoolean(config.get("encrypt"));
        } catch (NumberFormatException e) {
            throw new ParseConfigException(e);
        }

        socket = new MulticastSocket(port);
        networkInterface = socket.getNetworkInterface();
        socketAddress = new InetSocketAddress(address, port);
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

        senderRunnable = new SenderRunnable(socket, address, port, secure);
        cleanerRunnable = new CleanerRunnable(counter, view, disconnectTime);
        sender = Executors.newSingleThreadScheduledExecutor();
        cleaner = Executors.newSingleThreadScheduledExecutor();

        ReceiverRunnable receiverRunnable = new ReceiverRunnable(socket, counter, view, secure);
        receiver = new Thread(receiverRunnable);
    }

    public void execute() {
        receiver.start();
        senderFuture = sender.scheduleWithFixedDelay(senderRunnable, 1, timeBetweenSend,
                TimeUnit.MILLISECONDS);
        cleanerFuture = cleaner.scheduleWithFixedDelay(cleanerRunnable, 1, timeBetweenClean,
                TimeUnit.MILLISECONDS);
    }


    public void exit() {
        senderFuture.cancel(true);
        senderRunnable.sendExit();

        cleanerFuture.cancel(true);

        receiver.interrupt();
        try {
            receiver.join();
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
