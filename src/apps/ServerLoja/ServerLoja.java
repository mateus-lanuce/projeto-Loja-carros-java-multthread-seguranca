package apps.ServerLoja;

import apps.ServerDB.Controller.ControlDB;
import apps.ServerDB.ServerDB;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerLoja {
    public ServerLoja() {
        try {

            //cria o objeto remoto
            ControlDB carros = new ControlDB();

            Registry registry = LocateRegistry.createRegistry();

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
