package app.model;

import app.exceptions.MulticastException;
import app.exceptions.ParseJsonException;
import app.exceptions.ReadSocketException;
import app.secure.MessageSecure;
import app.view.View;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class Receiver extends AbstractMulticastThread {
    private final byte [] buffer;
    private final Counter counter;
    private final View view;
    private final MessageSecure secure;

    public Receiver(MulticastSocket socket, Counter counter, View view, MessageSecure secure) {
        super("receiver", socket);
        this.counter = counter;
        this.view = view;
        this.secure = secure;
        this.buffer = new byte[80];
    }

    public Receiver(MulticastSocket socket, Counter counter, View view) {
        this(socket, counter, view, null);
    }


    @Override
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

        JSONObject object;
        int isApp;
        double num;
        String address;
        Status status;

        try {
            object = new JSONObject(str);
            num = ((BigDecimal)object.get("num")).doubleValue();
            isApp = (int)object.get("isApp");
            address = (String)object.get("address");
            status = Status.valueOf(String.valueOf(object.get("status")));
        } catch (JSONException e) {
            throw new ParseJsonException(e);
        }

        if (isApp == 1) {
            var isNeedUpdate = chooseAction(num ,address, status);

            if (isNeedUpdate) {
                view.updateOnline(counter.getNumsCnt());
                view.updateAddresses(new ArrayList<>(counter.getAddresses().keySet()));
            }
        }
    }

    private boolean chooseAction(double num, String address, @NotNull Status status) {
        boolean isNeedUpdate;

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
