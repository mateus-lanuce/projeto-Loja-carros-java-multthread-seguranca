package apps.ServerLoja.Controller;

import apps.Categoria;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Interfaces.ServerLoja.ServerLojaInterface;
import apps.Records.Carro;
import apps.ServerLoja.Model.ModelCarrosLoja;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.LinkedList;

public class ControlLoja implements ServerLojaInterface {
    private ModelCarrosLoja model;

    LinkedList<ServerDBInterface> replicDBsConnected;
    LinkedList<ServerDBInterface> replicDBsTotal;
    Registry connection;
    Registry connectionLider;

    public ControlLoja(Registry connection, Registry connectionLider) {
        validateReplicas();
        this.model = new ModelCarrosLoja(connection, connectionLider);
    }

    private void validateReplicas() {
        replicDBsConnected.clear();
        replicDBsTotal.forEach(replica -> {
            try {
                if (replica.isAlive()) {
                    replicDBsConnected.add(replica);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Carro adicionar(Carro carro) throws IllegalArgumentException, RemoteException {
        return model.adicionar(carro);
    }

    @Override
    public Carro remover(String renavam) throws IllegalArgumentException, RemoteException {
        return model.remover(renavam);
    }

    @Override
    public LinkedList<Carro> removerPorNome(String nome) throws RemoteException {
        return model.removerPorNome(nome);
    }

    @Override
    public LinkedList<Carro> getCarros(Categoria categoria) throws RemoteException {
        return model.getCarros(categoria);
    }

    @Override
    public LinkedList<Carro> getCarrosByNome(String nome) throws RemoteException {
        return model.getCarrosByNome(nome);
    }

    @Override
    public Carro getCarroByRenavam(String renavam) throws RemoteException {
        return getCarroByRenavam(renavam);
    }

    @Override
    public Carro alterar(String renavam, Carro carro) throws IllegalArgumentException, RemoteException {
        return model.alterar(renavam, carro);
    }

    @Override
    public int getQuantidade() throws RemoteException {
        return model.getQuantidade();
    }

    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }
}
