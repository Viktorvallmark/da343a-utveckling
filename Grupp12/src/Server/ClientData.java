package Server;
import se.mau.DA343A.VT25.projekt.LiveXYSeries;

import javax.swing.*;


public class ClientData {

    private LiveXYSeries<Double> series;
    private double consumption;
    private boolean isConnected;
    private Timer timer;

    ClientData(LiveXYSeries<Double> series) {
        this.series = series;
        this.consumption = 0.0;
        this.isConnected = true;
    }

    public LiveXYSeries<Double> getSeries() {
        return series;
    }

    public double getConsumption() {
        return consumption;
    }
    public Timer getTimer(){
        return timer;
    }

    public boolean isConnected(){
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setTimer(Timer timer){
        this.timer = timer;
    }

    public void setSeries(LiveXYSeries<Double> series) {
        this.series = series;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }
}
