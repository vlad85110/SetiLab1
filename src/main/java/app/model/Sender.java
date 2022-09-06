package app.model;

import app.exceptions.MulticastException;
import app.exceptions.WriteSocketException;
import app.secure.MessageSecure;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.net.*;
import java.util.Random;

public class Sender extends AbstractMulticastThread {
    private final String hostName;
    private final int port;
    private final double uniqueNum;
    private final long timeBetweenSend;
    private final MessageSecure secure;

    public Sender(MulticastSocket socket, String hostName, int port, long timeBetweenSend, MessageSecure secure) {
        super("sender", socket);

        this.hostName = hostName;
        this.port = port;
        this.timeBetweenSend = timeBetweenSend;
        this.secure = secure;

        Random random = new Random();
        this.uniqueNum = random.nextDouble();
    }

    public Sender(MulticastSocket socket, String hostName, int port, long timeBetweenSend) {
        this(socket, hostName, port, timeBetweenSend, null);
    }

    @Override
    public void workMethod() throws MulticastException {
        try {
            sleep(1000 * timeBetweenSend);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendDgram(Status.Live);
    }

    @Override
    public void stopThread() {
        super.stopThread();

        try {
            this.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            sendDgram(Status.Exit);
        } catch (MulticastException e) {
            //
        }
    }

    public void sendDgram(Status status) throws MulticastException {
        byte[] data;
        if (secure != null) {
            data = secure.encrypt(createSelfInfoString(status));
        } else {
            data = createSelfInfoString(status).getBytes();
        }

        DatagramPacket packet = new DatagramPacket(data, data.length);

        try {
            packet.setAddress(InetAddress.getByName(hostName));
            packet.setPort(port);
        } catch (UnknownHostException e) {
            throw new WriteSocketException(e);
        }

        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new WriteSocketException();
        }
    }

    private String createSelfInfoString(Status status) throws MulticastException{
        JSONObject object = new JSONObject();

        try {
            object.put("status", status);
            object.put("isApp", 1);
            object.put("num", uniqueNum);
            object.put("address", Inet4Address.getLocalHost().getHostAddress());
        } catch (JSONException | UnknownHostException e) {
            throw new MulticastException(e);
        }

        return object.toString();
    }
}
