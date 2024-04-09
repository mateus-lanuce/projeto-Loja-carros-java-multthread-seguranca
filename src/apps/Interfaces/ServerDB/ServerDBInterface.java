package apps.Interfaces.ServerDB;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerDBInterface extends DBCarrosInterface, Remote {
    /**
     * Verifica se o servidor está ativo.
     * @return true, se não houver problemas.
     * @throws RemoteException
     */
    boolean isAlive() throws RemoteException;
}