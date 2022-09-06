package app.model;

import app.view.View;

import java.util.ArrayList;

public class Cleaner extends AbstractAppThread {
    private final Counter counter;
    private final View view;
    private final long timeBetweenClean;
    private final long disconnectTime;

    public Cleaner(Counter counter, View view, long timeBetweenClean, long disconnectTime) {
        super("cleaner");
        this.timeBetweenClean = timeBetweenClean;
        this.disconnectTime = disconnectTime;
        this.counter = counter;
        this.view = view;
    }

    @Override
    public void workMethod() {
        try {
            Thread.sleep(1000 * timeBetweenClean);
        } catch (InterruptedException e) {
            //
        }

        var curTime = System.currentTimeMillis();
        var nums = counter.getNums();
        var disconnected = new ArrayList<Double>();

        for (var entry: nums.entrySet()) {
            var num = entry.getKey();

            if (Math.abs(entry.getValue() - curTime) >= disconnectTime * 1000) {
                disconnected.add(num);
            }
        }

        for (var num : disconnected) {
            var address = counter.getAddressByNum(num);
            counter.delAddress(address, num);
            counter.delNum(num);
        }

        view.updateOnline(counter.getNumsCnt());
        view.updateAddresses(new ArrayList<>(counter.getAddresses().keySet()));
    }

    @Override
    public void stopThread() {
        super.stopThread();
        counter.clearAddresses();
        counter.clearNums();
    }
}
