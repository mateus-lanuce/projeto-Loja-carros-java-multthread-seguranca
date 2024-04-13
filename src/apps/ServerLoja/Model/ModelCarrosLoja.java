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

    public ModelCarrosLoja(Registry connection, Registry connectionLider) {
        this.changeConnectedDB(connection, connectionLider);
    }

    @Override
    public Carro adicionar(Carro carro) throws IllegalArgumentException, RemoteException {
        return null;
    }

    @Override
    public Carro remover(String renavam) throws IllegalArgumentException, RemoteException {
        return null;
    }

    @Override
    public LinkedList<Carro> removerPorNome(String nome) throws RemoteException {
        return null;
    }

    @Override
    public LinkedList<Carro> getCarros(Categoria categoria) throws RemoteException {
        return null;
    }

    @Override
    public LinkedList<Carro> getCarrosByNome(String nome) throws RemoteException {
        return null;
    }

    @Override
    public Carro getCarroByRenavam(String renavam) throws RemoteException {
        return null;
    }

    @Override
    public Carro alterar(String renavam, Carro carro) throws IllegalArgumentException, RemoteException {
        return null;
    }

    @Override
    public int getQuantidade() throws RemoteException {
        return 0;
    }

    public void changeConnectedDB(Registry connection, Registry connectionLider) {
        this.connectToDB(connection, connectionLider);
    }

    private void connectToDB(Registry connectionPrincipal, Registry connectionLider) {

        try {
            // Pegar o registro do servidor de banco de dados principal
            this.serverDBPrincipal = (ServerDBInterface) connectionPrincipal.lookup("NomeconnectionPrincipal");

            // Pegar o registro do servidor de banco de dados líder
            this.serverDBLider = (ServerDBInterface) connectionLider.lookup("NomeServidorLider");

            System.out.println("Conectado com o servidor de gateway");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

}
