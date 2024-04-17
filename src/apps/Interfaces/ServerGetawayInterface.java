package apps.Interfaces;

import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Records.Message;
import apps.Records.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerGetawayInterface extends Remote, DBCarrosInterface {
    /**
     * Verifica se o servidor está ativo.
     * @return true, se não houver problemas.
     * @throws RemoteException
     */
    boolean isAlive() throws RemoteException;

    User login(String email, String password) throws RemoteException;

    void addUser(User user) throws RemoteException;
}