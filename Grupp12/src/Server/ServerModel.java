package Server;

import java.util.List;
import java.util.Vector;

public class ServerModel {

    private double totalConsumption;
    private final List<Double> powerList = new Vector<>(5);

    public ServerModel() {
        this.totalConsumption = 0;

    }

    public synchronized double calcTotalConsumption() {

        double total = 0;

        for (int i = 1; i < powerList.size(); i++) {
            double prevPower = powerList.get(i - 1);
            if (powerList.get(i) < prevPower) {
                double min = Math.min(prevPower, powerList.get(i));
                total += min;
            } else {
                total = prevPower;
            }
        }
        return total;

    }


    public synchronized void addToPowerList(String power) {
        powerList.add(Double.parseDouble(power));
    }

    public synchronized double getTotalConsumption() {
        return totalConsumption;
    }

    public synchronized void update() {
        this.totalConsumption = calcTotalConsumption();
    }

}
