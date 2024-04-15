package apps.ServerAuth;

import apps.Interfaces.UsersInterface;
import apps.ServerAuth.Entity.Users;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerAuth {

    public ServerAuth() {
        try {
            //cria o objeto remoto
            UsersInterface users = new Users();

            Registry registry = LocateRegistry.createRegistry(1141);

            //registra o objeto remoto
            registry.rebind("Users", users);
            System.out.println("Servidor de autenticação pronto");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerAuth();
    }
}
