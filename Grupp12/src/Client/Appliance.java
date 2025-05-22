package Client;

import se.mau.DA343A.VT25.projekt.Buffer;
import se.mau.DA343A.VT25.projekt.net.SecurityTokens;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;


public class Appliance extends JFrame {
    protected JSlider slider;
    private JLabel label;
    private JPanel panel;
    private JFrame frame;
    private String name;
    private int maxPower;
    private int currentPower;
    private Buffer<Integer> powerBuffer;
    private SecurityTokens token;
    private Socket soc;
    private DataInputStream input;
    private DataOutputStream output;



    public Appliance(String name, int maxPower) {
        this.name = name;
        this.maxPower = maxPower;
        this.slider = new JSlider(0, maxPower, 0);
        setCurrentPower(maxPower /2);
        this.panel = new JPanel();
        this.frame = new JFrame(name);
        this.label = new JLabel(name);
        this.powerBuffer = new Buffer<>();
        this.token = new SecurityTokens("grupp12");
    }

    public String getName() {
        return name;
    }

    public int getMaxPower() {
        return maxPower;
    }

    public JFrame createSliderPanel() {
        slider.setPaintTrack(true);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(maxPower / 10);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentPower = slider.getValue();

                label.setText(name + " energy consumption: " + currentPower + " watts.");
                getPowerBuffer().put(currentPower);
            }
        });
        /*
        slider.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentPower = slider.getValue();
                getPowerBuffer().put(currentPower);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                currentPower = slider.getValue();
                getPowerBuffer().put(currentPower);
            }

            @Override
            public void mouseReleased(MouseEvent e) {


            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        */
        panel.add(slider);
        panel.add(label);

        frame.add(panel);

        label.setText(name + " energy consumption: " + 0 + " watts.");

        frame.setSize(500, 200);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                    stopConnection();

            }
        });

        return frame;
    }
/*
    @Override
    public void stateChanged(ChangeEvent e) {
        label.setText(name + " energy consumption: " + currentPower + " watts.");
    }
*/
    public void openConnection(){
        String response;
        System.out.println("Socket try");
        try {
            // initializing Socket

            System.out.println("Creating socket");
            soc = new Socket();
            //System.out.println("Binding");
            //soc.bind(new InetSocketAddress("127.0.0.1", 55558));
            System.out.println("Connecting to server");
            soc.connect(new InetSocketAddress("127.0.0.1", 55556));
            System.out.println("Create datastreams");
            input = new DataInputStream(soc.getInputStream());
            output = new DataOutputStream(soc.getOutputStream());
            System.out.println("Socket created");

            String secToken = token.generateToken();
            output.writeUTF(secToken);
            System.out.println("Token: " + secToken);

            response = input.readUTF();
            System.out.println("Response: " + response);

            while(true) {
                int value = powerBuffer.get();
                output.writeUTF(String.valueOf(value));
                System.out.println("Power: " + value);
                output.flush();
            }

        } catch (InterruptedException | IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        }
    }

    public void stopConnection() {
        try {
            if (output != null) output.close();
            if (soc != null) soc.close();
            System.out.println(name + " disconnected from server.");
        } catch (IOException e) {
            System.out.println("Disconnection eror: " + e.getMessage());}
    }

    public Buffer<Integer> getPowerBuffer() {
        return powerBuffer;
    }
    private void setCurrentPower(int i) {
        this.currentPower = i;
    }

    public double getCurrentPower(){
        return slider.getValue();
    }
    }


