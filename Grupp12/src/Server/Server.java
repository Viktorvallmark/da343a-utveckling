package Server;

import se.mau.DA343A.VT25.projekt.IAppExitingCallback;
import se.mau.DA343A.VT25.projekt.LiveXYSeries;
import se.mau.DA343A.VT25.projekt.ServerGUI;
import se.mau.DA343A.VT25.projekt.net.ListeningSocket;
import se.mau.DA343A.VT25.projekt.net.ListeningSocketConnectionWorker;
import se.mau.DA343A.VT25.projekt.net.SecurityTokens;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.SocketAddress;
import javax.swing.*;

public class Server extends ListeningSocket{
    private ServerModel model;
    private ServerGUI serverGUI;
    private LiveXYSeries<Double> series;
    private IAppExitingCallback callback;

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
                   System.out.println(check);
                   if(check){
                       String messageConnected = "Client from address: "+socketAddress+" has connected";
                       SwingUtilities.invokeLater(()-> {serverGUI.addLogMessage(messageConnected);
                           serverGUI.addSeries(series);
                       });

                       Thread newThread = new Thread(new Runnable() {
                           @Override
                           public void run() {
                               Timer timer = new Timer(1000, new ActionListener() {
                                   @Override
                                   public void actionPerformed(ActionEvent e) {
                                       model.calcTotalConsumption();
                                       System.out.println("Total consumption: " + model.calcTotalConsumption());

                                   }

                               });

                               timer.setInitialDelay(0);
                               timer.start();
                           }

                       });
                       newThread.start();


                       Timer timer2 = new Timer(1000, new ActionListener() {
                           @Override
                           public void actionPerformed(ActionEvent e) {
                               model.update();
                               updateSeries(model.getTotalConsumption());
                               updateCurrentConsumptionData();

                           }
                       });
                       SwingUtilities.invokeLater(()-> {
                           timer2.setInitialDelay(1000);
                           timer2.start();
                       });
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

    public synchronized LiveXYSeries<Double> getSeries(){
        return this.series;
    }

    public synchronized void updateSeries(Double y){
        long currentTime = System.currentTimeMillis()/1000;
        series.addValue((double) currentTime, y);

    }

    public synchronized void updateCurrentConsumptionData() {
        serverGUI.setTotalConsumption(model.getTotalConsumption());
    }

}
