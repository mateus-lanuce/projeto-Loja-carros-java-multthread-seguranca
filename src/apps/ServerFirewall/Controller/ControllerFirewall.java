package apps.ServerFirewall.Controller;

import apps.Categoria;
import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Interfaces.ServerGetawayInterface;
import apps.Interfaces.ServerLoja.ServerLojaInterface;
import apps.Interfaces.UsersInterface;
import apps.Records.Carro;
import apps.Records.IpPort;
import apps.Records.User;
import apps.ServerFirewall.Model.ModelFirewall;
import apps.ServerLoja.Model.ModelCarrosLoja;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

public class ControllerFirewall extends UnicastRemoteObject implements ServerGetawayInterface, ServerDBInterface {
    private final ModelFirewall model;

    public ControllerFirewall(ArrayList<IpPort> portsDB, int idPreferenciaDB, IpPort AuthServer) throws RemoteException {
        super();
        this.model = new ModelFirewall(portsDB, idPreferenciaDB, AuthServer);
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
        return model.getCarroByRenavam(renavam);
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

    @Override
    public User login(String email, String password) throws RemoteException {
        return null;
    }

    @Override
    public void addUser(User user) throws RemoteException {

    }
}
