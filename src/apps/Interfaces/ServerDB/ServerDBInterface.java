package apps.Interfaces.ServerDB;

import apps.Records.Carro;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

public interface ServerDBInterface extends DBCarrosInterface, Remote {
    /**
     * Verifica se o servidor está ativo.
     * @return true, se não houver problemas.
     * @throws RemoteException
     */
    boolean isAlive() throws RemoteException;

    Carro adicionar(Carro carro, boolean sync) throws IllegalArgumentException, RemoteException;

    Carro remover(String renavam, boolean sync) throws IllegalArgumentException, RemoteException;

    LinkedList<Carro> removerPorNome(String nome, boolean sync) throws IllegalArgumentException, RemoteException;

    Carro alterar(String renavam, Carro carro, boolean sync) throws IllegalArgumentException, RemoteException;
}
