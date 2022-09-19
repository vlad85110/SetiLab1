package app.model;

import java.util.HashMap;
import java.util.HashSet;

public class Counter {
    private final HashMap<Double, Long> nums = new HashMap<>();
    private final HashMap<String, HashSet<Double>> addresses = new HashMap<>();

    private final HashMap<Double, String> numAddressesMap = new HashMap<>();

    public boolean addNum(double num) {
        var time = System.currentTimeMillis();
        return nums.put(num, time) == null;
    }

    public HashMap<Double, Long> getNums() {
        return nums;
    }

    public boolean addAddress(String address, double num) {
        var addressNums = addresses.get(address);
        numAddressesMap.put(num, address);

        if (addressNums != null) {
            return addressNums.add(num);
        } else {
            addressNums = new HashSet<>();
            addressNums.add(num);
            var res = addresses.put(address, addressNums);
            return res == null;
        }
    }

    public void clearAddresses() {
        addresses.clear();
    }

    public void clearNums() {
        nums.clear();
    }

    public void delNum(double num) {
        nums.remove(num);
    }

    public void delAddress(String address, double num) {
        var addressNums = addresses.get(address);
        numAddressesMap.remove(num);

        if (addressNums != null) {
            addressNums.remove(num);
            if (addressNums.isEmpty()) {
                 addresses.remove(address);
            }
        }
    }

    public String getAddressByNum(double num) {
        return numAddressesMap.get(num);
    }

    public HashMap<String, HashSet<Double>> getAddresses() {
        return addresses;
    }

    public int getNumsCnt() {
        return nums.size();
    }
}
