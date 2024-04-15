package apps.Interfaces;

import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Interfaces.ServerDB.ServerDBInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerGetawayInterface extends Remote, UsersInterface, DBCarrosInterface {
    /**
     * Verifica se o servidor está ativo.
     * @return true, se não houver problemas.
     * @throws RemoteException
     */
    boolean isAlive() throws RemoteException;
}