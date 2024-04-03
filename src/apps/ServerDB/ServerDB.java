package apps.ServerDB;

import apps.ServerDB.Entity.CarrosHashMap;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerDB {

    public ServerDB() {
        try {

            //cria o objeto remoto
            CarrosHashMap carros = new CarrosHashMap();

            Registry registry = LocateRegistry.createRegistry(1099);

            //registra o objeto remoto
            registry.rebind("Carros", carros);
            System.out.println("Servidor de banco de dados pronto");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerDB();
    }

}
