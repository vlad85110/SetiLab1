package app.model;


import app.exceptions.MulticastException;
import app.exceptions.ParseJsonException;

public abstract class AbstractAppThread extends Thread {
    private volatile boolean stopFlag = false;

    public AbstractAppThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        while (!stopFlag) {
            try {
                workMethod();
            } catch (ParseJsonException e) {
                //
            } catch (MulticastException e) {
                stopThread();
            }
        }
    }

    public void stopThread() {
        stopFlag = true;
    }

    abstract public void workMethod() throws MulticastException;
}
