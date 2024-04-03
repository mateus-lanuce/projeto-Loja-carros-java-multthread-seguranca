package apps.ServerAuth.Entity;

import apps.Interfaces.UsersInterface;
import apps.Records.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Users extends UnicastRemoteObject implements UsersInterface {

    private static final long serialVersionUID = 1L;

    private final ArrayList<User> users;
    public Users() throws RemoteException {
        super();
        users = new ArrayList<>();
    }


    @Override
    public User login(String email, String password) {
        //segurança é tudo kkkkkk
        //não é a melhor forma de fazer isso, mas é o que temos pra hoje
        //não façam isso em casa
        //não façam isso em produção
        //não façam isso em lugar nenhum
        //não façam isso
        //não
        return users.stream().filter(user -> user.email().equals(email) && user.password().equals(password)).findFirst().orElse(null);
    }

    public void addUser(User user) {
        users.add(user);
        System.out.println("Usuário adicionado: " + user);
    }
}
