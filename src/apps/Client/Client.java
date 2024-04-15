package apps.Client;

import apps.Client.Controller.ClientController;
import apps.Records.IpPort;

import java.util.ArrayList;

public class Client {

    public static void main(String[] args) {

        ArrayList<IpPort> ports = new ArrayList<>();
        ports.add(new IpPort("localhost", 1099));

        ClientController controller = new ClientController(ports, 1);

        controller.start();
    }

}

class Client2 {

    public static void main(String[] args) {
//        ClientController controller = new ClientController();
//
//        controller.start();
    }

}
