package Server;

import se.mau.DA343A.VT25.projekt.LiveXYSeries;
import se.mau.DA343A.VT25.projekt.ServerGUI;
import se.mau.DA343A.VT25.projekt.net.ListeningSocket;
import se.mau.DA343A.VT25.projekt.net.ListeningSocketConnectionWorker;
import se.mau.DA343A.VT25.projekt.net.SecurityTokens;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import javax.swing.*;

public class Server extends ListeningSocket{
    private ServerModel model;
    private ServerGUI serverGUI;
    private LiveXYSeries<Double> series;
    private final HashMap<SocketAddress, ClientData> clientHashMap = new HashMap<>();

    public Server(int listeningPort) {
        super(listeningPort);
        this.model = new ServerModel();
        this.series = new LiveXYSeries<>("Total consumption", 20);
        this.serverGUI = new ServerGUI("Energy Meter");
        SwingUtilities.invokeLater(()-> {serverGUI.createAndShowUI();});
    }


    @Override
    public ListeningSocketConnectionWorker createNewConnectionWorker() {
        return new ListeningSocketConnectionWorker() {
            @Override
            public void newConnection(SocketAddress socketAddress, DataInput dataInput, DataOutput dataOutput) {
                String request, response;
                SecurityTokens token = new SecurityTokens("grupp12");
               try{
                   try {
                       request = dataInput.readUTF();
                       System.out.println(request);
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
                   boolean check = token.verifyToken(request);
                   System.out.println(check+"inside Server Class new ListeningSocketConnectionWorker");
                   if(check){
                       /*
                       String messageConnected = "Client from address: "+socketAddress+" has connected";
                       SwingUtilities.invokeLater(()-> {serverGUI.addLogMessage(messageConnected);
                           serverGUI.addSeries(series);
                       });
                        */
                       LiveXYSeries<Double> newAppSeries = new LiveXYSeries<>("Client: "+ socketAddress, 20);
                       ClientData clientData = new ClientData(newAppSeries);
                       clientHashMap.put(socketAddress, clientData);
                       SwingUtilities.invokeLater(() -> {
                          String message = "Client from address: " + socketAddress+ " has connected";
                          serverGUI.addLogMessage(message);
                          serverGUI.addSeries(newAppSeries);
                       });

                       clientData.setTimer(new Timer(1000, e -> {
                         synchronized (clientHashMap) {
                             ClientData data = clientHashMap.get(socketAddress);
                             if(data != null && clientData.isConnected() ){
                                 updateSeries(data.getConsumption(), data.getSeries());
                             }
                         }
                       }));
                       clientData.getTimer().setInitialDelay(1000);
                       clientData.getTimer().start();

                       dataOutput.writeUTF("true");


                       while (true){
                           try{
                               request = dataInput.readUTF();
                               double consumption = Double.parseDouble(request);

                                synchronized (clientHashMap){
                                    ClientData data = clientHashMap.get(socketAddress);
                                    if (data != null){
                                        data.setConsumption(consumption);
                                        model.addToPowerList(String.valueOf(consumption));
                                    }
                                }
                               System.out.println("Client "+ socketAddress+": "+ consumption);
                           } catch (IOException e) {

                               System.out.println("Client from address: "+socketAddress+" has disconnected.");
                               handleDisconnection(socketAddress);
                               break;

                           } catch (NumberFormatException e){
                               System.err.println("Invalid number format from client: "+request);
                           }
                       }

                       dataOutput.writeUTF("true");
                        while (true) {
                            try {
                                request = dataInput.readUTF();
                                model.addToPowerList(request);
                                System.out.println(request);
                            }catch (IOException e) {
                                System.out.println("Client from address " + socketAddress + " disconnected");
                                break;
                            }
                        }
                   } else {
                       response = "false";
                       dataOutput.writeUTF(response);
                   }
               }catch (IOException e) {
                   System.out.println("Client from address: "+socketAddress+" failed to validate token");
               }
            }
        };
    }

    private void handleDisconnection(SocketAddress socketAddress) {
        SwingUtilities.invokeLater(() -> {
            synchronized (clientHashMap) {
                ClientData data = clientHashMap.get(socketAddress);
                if (data != null) {
                    data.setConnected(false);
                    if (data.getTimer() != null) {
                        data.getTimer().stop();
                    }

                    String message = "Client from address: " + socketAddress + " has disconnected";
                    serverGUI.addLogMessage(message);
                }
            }
        });
    }


    public synchronized LiveXYSeries<Double> getSeries(){
        return this.series;
    }

    public synchronized void updateSeries(Double y, LiveXYSeries<Double> series){
        long currentTime = System.currentTimeMillis()/1000;
        SwingUtilities.invokeLater(() -> series.addValue((double) currentTime, y));
    }

    public synchronized void updateCurrentConsumptionData() {
        serverGUI.setTotalConsumption(model.getTotalConsumption());
    }

}
