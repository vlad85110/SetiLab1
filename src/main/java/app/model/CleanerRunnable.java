package app.model;

import app.view.View;

import java.util.ArrayList;

public class CleanerRunnable extends AbstractAppRunnable {
    private final Counter counter;
    private final View view;
    private final long timeBetweenClean;
    private final long disconnectTime;

    public CleanerRunnable(Counter counter, View view, long timeBetweenClean, long disconnectTime) {
        this.timeBetweenClean = timeBetweenClean;
        this.disconnectTime = disconnectTime;
        this.counter = counter;
        this.view = view;
    }

    @Override
    public void workMethod() {
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
    public long getDelay() {
        return timeBetweenClean;
    }

    @Override
    public void stopRunnable() {
        super.stopRunnable();
        counter.clearAddresses();
        counter.clearNums();
    }
}
