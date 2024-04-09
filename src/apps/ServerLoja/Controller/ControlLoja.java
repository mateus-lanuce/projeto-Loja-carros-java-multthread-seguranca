package apps.ServerLoja.Controller;

import apps.Categoria;
import apps.Interfaces.ServerLoja.ServerLojaInterface;
import apps.Records.Carro;

import java.rmi.RemoteException;
import java.util.LinkedList;

public class ControlLoja implements ServerLojaInterface {
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

    @Override
    public boolean isAlive() throws RemoteException {
        return false;
    }
}
