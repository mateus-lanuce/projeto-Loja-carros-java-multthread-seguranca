package apps.Interfaces.ServerLoja;

import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Interfaces.ServerDB.ServerDBInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerLojaInterface extends DBCarrosInterface, Remote {
    /**
     * Verifica se o servidor está ativo.
     * @return true, se não houver problemas.
     */
    boolean isAlive() throws RemoteException;
}
