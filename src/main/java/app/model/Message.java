package app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Message {
    @JsonProperty("status")
    private Status status;
    @JsonProperty("num")
    private double num;
    @JsonProperty("address")
    private String address;

    public Message(Status status, double num) throws UnknownHostException {
        this.status = status;
        this.address = Inet4Address.getLocalHost().getHostAddress();
        this.num = num;
    }

    public Message() {}

    public Status getStatus() {
        return status;
    }

    public double getNum() {
        return num;
    }

    public String getAddress() {
        return address;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
