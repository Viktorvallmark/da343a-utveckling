package Server;

public class ServerLauncher {

    public static void main(String[] args) throws InterruptedException {
        ServerModel serverModel = new ServerModel();
        serverModel.calcTotalConsumption();


        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);

        new Thread(server).start();



    }
}
