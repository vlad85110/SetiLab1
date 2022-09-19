package app.model;


import app.exceptions.MulticastException;

public abstract class AbstractAppRunnable {
    private volatile boolean stopFlag = false;

    public void stopRunnable() {
        stopFlag = true;
    }

    abstract public void workMethod() throws MulticastException;

    abstract public long getDelay();
}
