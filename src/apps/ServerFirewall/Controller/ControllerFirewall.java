package apps.ServerFirewall.Controller;

import apps.Categoria;
import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Interfaces.ServerFirewall.ServerFirewallInterface;
import apps.Interfaces.ServerGetawayInterface;
import apps.Interfaces.ServerLoja.ServerLojaInterface;
import apps.Interfaces.UsersInterface;
import apps.Records.*;
import apps.ServerFirewall.Model.ModelFirewall;
import apps.ServerLoja.Model.ModelCarrosLoja;

import javax.security.auth.login.LoginException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

public class ControllerFirewall extends UnicastRemoteObject  {
    private final ModelFirewall model;

    public ControllerFirewall(ArrayList<IpPort> portsDB, int idPreferenciaDB, IpPort AuthServer) throws RemoteException {
        super();
        this.model = new ModelFirewall(portsDB, idPreferenciaDB, AuthServer);
    }

   
    public Carro adicionar(Carro carro) throws IllegalArgumentException, RemoteException {
        return model.adicionar(carro);
    }

   
    public Carro remover(String renavam) throws IllegalArgumentException, RemoteException {
        return model.remover(renavam);
    }

   
    public LinkedList<Carro> removerPorNome(String nome) throws RemoteException {
        return model.removerPorNome(nome);
    }

   
    public LinkedList<Carro> getCarros(Categoria categoria) throws RemoteException {
        return model.getCarros(categoria);
    }

   
    public LinkedList<Carro> getCarrosByNome(String nome) throws RemoteException {
        return model.getCarrosByNome(nome);
    }

   
    public Carro getCarroByRenavam(String renavam) throws RemoteException {
        return model.getCarroByRenavam(renavam);
    }

   
    public Carro alterar(String renavam, Carro carro) throws IllegalArgumentException, RemoteException {
        return model.alterar(renavam, carro);
    }

   
    public int getQuantidade() throws RemoteException {
        return model.getQuantidade();
    }

   
    public boolean isAlive() throws RemoteException {
        return true;
    }

   
    public User login(String email, String password) throws RemoteException {
        try {
            return model.login(email, password);
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }

   
    public void addUser(User user) throws RemoteException {
        model.addUser(user);
    }

   
    public void setClientSalt(String clientSalt) throws Exception {

    }

   
    public String getServerSalt() throws Exception {
        return "";
    }

   
    public void setClientPublicKey(PublicKey clientPublicKey) throws Exception {

    }

   
    public PublicKey getServerPublicKey() throws Exception {
        return null;
    }

   
    public Message serverLogin(Message message) throws Exception {
        return null;
    }


    public Message encryptMessage(String message) throws Exception {
        return null;
    }
}
