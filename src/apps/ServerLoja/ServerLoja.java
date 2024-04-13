package apps.ServerLoja;

import apps.Records.IpPort;
import apps.ServerDB.Controller.ControlDB;
import apps.ServerDB.ServerDB;
import apps.ServerLoja.Controller.ControlLoja;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class ServerLoja {
    public ServerLoja() {
        try {
            IpPort port1 = new IpPort("", 1099);
            IpPort port2 = new IpPort("", 1100);
            IpPort port3 = new IpPort("", 1101);
            ArrayList<IpPort> ports = new ArrayList<>();
            ports.add(port1);
            ports.add(port2);
            ports.add(port3);

            //cria o objeto remoto
            ControlLoja lojas = new ControlLoja(ports, 1);

            Registry registry = LocateRegistry.createRegistry(1099);

            //registra o objeto remoto
            registry.rebind("Lojas", lojas);
            System.out.println("Servidor de loja pronto");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerDB();
    }

}
