package app.model;

import app.exceptions.MulticastException;
import app.exceptions.WriteSocketException;
import app.secure.MessageSecure;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.*;
import java.util.Random;

public class SenderRunnable extends AbstractMulticastRunnable implements Runnable {
    private final String hostName;
    private final int port;
    private final double uniqueNum;
    private final MessageSecure secure;

    public SenderRunnable(MulticastSocket socket, String hostName, int port, MessageSecure secure) {
        super(socket);

        this.hostName = hostName;
        this.port = port;
        this.secure = secure;

        Random random = new Random();
        this.uniqueNum = random.nextDouble();
    }

    public void workMethod() throws MulticastException {
        sendDgram(Status.Live);
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
            throw new WriteSocketException(e);
        }
    }

    private String createSelfInfoString(Status status) throws MulticastException{
        Message message;
        try {
            message = new Message(status, uniqueNum);
        } catch (UnknownHostException e) {
            throw new MulticastException(e);
        }

        String json;
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new MulticastException(e);
        }

        return json;
    }

    @Override
    public void run() {
        try {
            workMethod();
        } catch (MulticastException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendExit() {
        try {
            sendDgram(Status.Exit);
        } catch (MulticastException e) {
            //
        }
    }
}
