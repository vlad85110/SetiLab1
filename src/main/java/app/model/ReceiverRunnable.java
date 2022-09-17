package app.model;

import app.exceptions.MulticastException;
import app.exceptions.ParseJsonException;
import app.exceptions.ReadSocketException;
import app.secure.MessageSecure;
import app.view.View;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class ReceiverRunnable extends AbstractMulticastRunnable implements Runnable {
    private final byte [] buffer;
    private final Counter counter;
    private final View view;
    private final MessageSecure secure;

    public ReceiverRunnable(MulticastSocket socket, Counter counter, View view, MessageSecure secure)
            throws SocketException {
        super(socket);
        socket.setSoTimeout(1000 * 2);
        this.counter = counter;
        this.view = view;
        this.secure = secure;
        this.buffer = new byte[80];
    }

    @Override
    public void run() {
        while (true) {
            try {
                workMethod();
            } catch (MulticastException e) {
                //
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void workMethod() throws MulticastException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            throw new ReadSocketException(e);
        }

        String str;
        if (secure != null) {
            str = secure.decipher(packet.getData());
        } else {
            str = new String(packet.getData());
        }
        System.out.println(str);

        ObjectMapper mapper = new ObjectMapper();
        Message message;
        try {
            message = mapper.readValue(str, Message.class);
        } catch (JsonProcessingException e) {
            throw new ReadSocketException(e);
        }

        var isNeedUpdate = chooseAction(message);
        if (isNeedUpdate) {
            view.updateOnline(counter.getNumsCnt());
            view.updateAddresses(new ArrayList<>(counter.getAddresses().keySet()));
        }
    }

    private boolean chooseAction(Message message) {
        boolean isNeedUpdate;
        double num = message.getNum();
        String address = message.getAddress();
        Status status = message.getStatus();

        switch (status) {
            case Live:
                var isAddNum = counter.addNum(num);
                var isAddAddress = counter.addAddress(address, num);
                isNeedUpdate =  isAddNum || isAddAddress;
                return isNeedUpdate;
            case Exit:
                counter.delAddress(address, num);
                counter.delNum(num);
                return true;
        }

        return false;
    }
}
