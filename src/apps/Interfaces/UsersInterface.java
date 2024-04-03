package apps.Interfaces;

import apps.Records.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UsersInterface extends Remote {
    User login (String email, String password) throws RemoteException;

    void addUser(User user) throws RemoteException;
}
