package apps.ServerLoja;

import apps.Records.IpPort;
import apps.ServerDB.Controller.ControlDB;
import apps.ServerDB.ServerDB;
import apps.ServerLoja.Controller.ControlLoja;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class ServerLoja {
    public ServerLoja(int port, int idPreferencia) {
        try {
            IpPort port1 = new IpPort("", 1100);
//            IpPort port2 = new IpPort("", 1101);
//            IpPort port3 = new IpPort("", 1102);
            ArrayList<IpPort> ports = new ArrayList<>();
            ports.add(port1);
//            ports.add(port2);
//            ports.add(port3);

            //cria o objeto remoto
            ControlLoja lojas = new ControlLoja(ports, idPreferencia);

            lojas.getQuantidade();

            Registry registry = LocateRegistry.createRegistry(port);

            //registra o objeto remoto
            registry.rebind("Lojas", lojas);
            System.out.println("Servidor de loja pronto na porta: " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerLoja(1111, 1);
    }

}

class ServerLoja2 {
    public static void main(String[] args) {
        new ServerLoja(1112, 2);
    }
}

class ServerLoja3 {
    public static void main(String[] args) {
        new ServerLoja(1113, 3);
    }
}
