package apps.ServerDB;

import apps.ServerDB.Controller.ControlDB;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerDB {

    public ServerDB() {
        try {

            //cria o objeto remoto
            ControlDB carros = new ControlDB();

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
