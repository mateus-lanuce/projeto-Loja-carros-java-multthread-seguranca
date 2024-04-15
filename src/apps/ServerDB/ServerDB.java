package apps.ServerDB;

import apps.Records.IpPort;
import apps.ServerDB.Controller.ControlDB;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class ServerDB {

    public ServerDB(int port) {
        try {
            ArrayList<IpPort> replicsPorts = new ArrayList<>();

            IpPort port1 = new IpPort("", 1100);
            IpPort port2 = new IpPort("", 1101);
            // IpPort port3 = new IpPort("", 1102);

            replicsPorts.add(port1);
            replicsPorts.add(port2);
            // replicsPorts.add(port3);

            //remover a porta que é a mesma do servidor
            replicsPorts.removeIf(porta -> porta.port() == port);

            //cria o objeto remoto
            ControlDB bancoDados = new ControlDB(replicsPorts);

            Registry registry = LocateRegistry.createRegistry(port);

            //registra o objeto remoto
            registry.rebind("Carros", bancoDados);
            System.out.println("Servidor de banco de dados pronto na porta: " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerDB serverDB = new ServerDB(1100);

    }

}

class ServerDB2 {

    ServerDB2(int port) {
        try {
            ArrayList<IpPort> replicsPorts = new ArrayList<>();

            IpPort port1 = new IpPort("", 1100);
            IpPort port2 = new IpPort("", 1101);
            // IpPort port3 = new IpPort("", 1102);

            replicsPorts.add(port1);
            replicsPorts.add(port2);
            // replicsPorts.add(port3);

            //remover a porta que é a mesma do servidor
            replicsPorts.removeIf(porta -> porta.port() == port);

            //cria o objeto remoto
            ControlDB bancoDados = new ControlDB(replicsPorts);

            Registry registry = LocateRegistry.createRegistry(port);

            //registra o objeto remoto
            registry.rebind("Carros", bancoDados);
            System.out.println("Servidor de banco de dados pronto na porta: " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerDB2 serverDB = new ServerDB2(1101);
    }
}

class ServerDB3 {
    public static void main(String[] args) {
        new ServerDB(1102);
    }
}
