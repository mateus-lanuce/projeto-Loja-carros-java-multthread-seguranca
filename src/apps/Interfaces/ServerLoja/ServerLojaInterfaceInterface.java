package apps.Interfaces.ServerLoja;

import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Interfaces.ServerSecurityInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerLojaInterfaceInterface extends DBCarrosInterface, Remote, ServerSecurityInterface {
    /**
     * Verifica se o servidor está ativo.
     * @return true, se não houver problemas.
     */
    boolean isAlive() throws RemoteException;
}
