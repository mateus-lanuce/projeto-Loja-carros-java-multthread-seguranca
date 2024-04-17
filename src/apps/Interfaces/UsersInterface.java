package apps.Interfaces;

import apps.Records.Message;
import apps.Records.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UsersInterface extends Remote, ServerSecurity {
    Message login (Message userString) throws RemoteException;

    void addUser(User user) throws RemoteException;
}
