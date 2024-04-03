package apps.Client;

import apps.Categoria;
import apps.Client.Controller.ClientController;
import apps.Interfaces.CarrosInterface;
import apps.Interfaces.ServerGetawayInterface;
import apps.Records.Carro;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) {
        ClientController controller = new ClientController();

        controller.start();
    }

}

class Client2 {

    public static void main(String[] args) {
        ClientController controller = new ClientController();

        controller.start();
    }

}
