package apps.ServerLoja.Model;

import apps.Categoria;
import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Records.Carro;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.LinkedList;

/**
 * classe responsavel por lidar com a conexão com o servidor de banco de dados de carros
 * e fazer as operações.
 */
public class ModelCarrosLoja implements DBCarrosInterface {

    private ServerDBInterface serverDBPrincipal; //leitura
    private ServerDBInterface serverDBLider; //escrita

    public ModelCarrosLoja(ServerDBInterface connection, ServerDBInterface connectionLider) {

        this.changeConnectedDB(connection, connectionLider);
    }

    @Override
    public Carro adicionar(Carro carro) throws IllegalArgumentException, RemoteException {

        return serverDBLider.adicionar(carro);
    }

    @Override
    public Carro remover(String renavam) throws IllegalArgumentException, RemoteException {
        return serverDBLider.remover(renavam);
    }

    @Override
    public LinkedList<Carro> removerPorNome(String nome) throws RemoteException {
        return serverDBLider.removerPorNome(nome);
    }

    @Override
    public LinkedList<Carro> getCarros(Categoria categoria) throws RemoteException {
        return serverDBPrincipal.getCarros(categoria);
    }

    @Override
    public LinkedList<Carro> getCarrosByNome(String nome) throws RemoteException {
        return serverDBPrincipal.getCarrosByNome(nome);
    }

    @Override
    public Carro getCarroByRenavam(String renavam) throws RemoteException {
        return serverDBPrincipal.getCarroByRenavam(renavam);
    }

    @Override
    public Carro alterar(String renavam, Carro carro) throws IllegalArgumentException, RemoteException {
        return serverDBLider.alterar(renavam, carro);
    }

    @Override
    public int getQuantidade() throws RemoteException {
        return serverDBPrincipal.getQuantidade();
    }

    public void changeConnectedDB(ServerDBInterface connection, ServerDBInterface connectionLider) {
        serverDBPrincipal = connection;
        serverDBLider = connectionLider;
    }



}
