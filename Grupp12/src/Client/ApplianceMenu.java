package Client;

import se.mau.DA343A.VT25.projekt.IAppExitingCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ApplianceMenu extends JFrame implements ItemListener, IAppExitingCallback {
    private JPanel panel;
    private JFrame frame;
    private JComboBox applianceComboBox;
    private JLabel selectedAppliance;
    private final JButton addButton = new JButton("Add");
    private ArrayList<Appliance> appList = new ArrayList<>();

    public void initializeApplianceMenu() {
        JLabel addAppliance = new JLabel("Select appliance: ");
        selectedAppliance = new JLabel("Light bulb selected");
        String[] appliances = {"Light bulb", "Microwave", "Refrigerator", "Computer"};
        applianceComboBox = new JComboBox(appliances);
        applianceComboBox.addItemListener(this);

        panel = new JPanel();
        panel.add(addAppliance);
        panel.add(applianceComboBox);
        panel.add(selectedAppliance);
        panel.add(addButton);


        frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.add(panel);
        frame.setTitle("Appliance Menu");
        frame.setSize(600, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Här är för att få ett nytt fönster att poppa upp
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(appliances[applianceComboBox.getSelectedIndex()].equals("Light bulb")) {
                    Appliance app = new Appliance("Light bulb", 10);
                    JFrame applianceFrame = app.createSliderPanel();
                    appList.add(app);
                    applianceFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            app.stopConnection();
                        }
                    });
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            app.openConnection();
                        }
                    });
                    t.start();
                }else if(appliances[applianceComboBox.getSelectedIndex()].equals("Microwave")) {
                    Appliance app = new Appliance("Microwave", 50);
                    JFrame applianceFrame = app.createSliderPanel();
                    appList.add(app);
                    applianceFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            app.stopConnection(); // Stoppa anslutningen när fönstret stängs
                        }
                    });
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            app.openConnection();
                        }
                    });
                    t.start();
                }else if(appliances[applianceComboBox.getSelectedIndex()].equals("Computer")) {
                    Appliance app = new Appliance("Computer", 20);
                    JFrame applianceFrame = app.createSliderPanel();
                    appList.add(app);
                    applianceFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            app.stopConnection(); // Stoppa anslutningen när fönstret stängs
                        }
                    });
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            app.openConnection();
                        }
                    });
                    t.start();
                }else if(appliances[applianceComboBox.getSelectedIndex()].equals("Refrigerator")) {
                    Appliance app = new Appliance("Refrigerator", 30);
                    JFrame applianceFrame = app.createSliderPanel();
                    appList.add(app);
                    applianceFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            app.stopConnection();
                        }
                    });
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            app.openConnection();
                        }
                    });
                    t.start();
                }
            }
        });
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == applianceComboBox) {
            String text = (String) applianceComboBox.getSelectedItem();
            selectedAppliance.setText(text + " selected");
        }

    }

    @Override
    public void exiting() {
        for (Appliance appliance : appList){
            appliance.stopConnection();
        }
    }
}
