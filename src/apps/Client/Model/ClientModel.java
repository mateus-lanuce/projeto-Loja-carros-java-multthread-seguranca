package apps.Client.Model;

import apps.Categoria;
import apps.Interfaces.ServerGetawayInterface;
import apps.Records.Carro;
import apps.Records.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;

/**
 * classe responsavel por lidar com as conexões com o servidor de gateway
 */
public class ClientModel {

    ServerGetawayInterface serverGetaway;
    Registry registry;

    public ClientModel() {
        this.connect();
    }

    public void connect() {
        // se conectar com o servidor de gateway que é o intermediário entre o cliente e os servidores de banco de dados e autenticação
        try {
            // pegar o registro do servidor de gateway
            this.registry = LocateRegistry.getRegistry(1101);

            this.serverGetaway = (ServerGetawayInterface) registry.lookup("Getaway");

            System.out.println("Conectado com o servidor de gateway");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public User autenticar(String login, String senha) {
        try {
            return serverGetaway.login(login, senha);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Carro adicionar(Carro carro) {
        try {
            return serverGetaway.adicionar(carro);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<Carro> apagarPorNome(String nome) {
        try {
            return serverGetaway.removerPorNome(nome);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Carro apagarPorRenavam(String renavam) {
        try {
            return serverGetaway.remover(renavam);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<Carro> getCarros(Categoria categoria) {
        try {
            LinkedList<Carro> carros = serverGetaway.getCarros(categoria);
            return serverGetaway.getCarros(categoria);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Carro getCarroPorRenavam(String renavam) {
        try {
            return serverGetaway.getCarroByRenavam(renavam);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<Carro> getCarrosPorNome(String nome) {
        try {
            return serverGetaway.getCarrosByNome(nome);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Carro alterar(String renavam, Carro carro) {
        try {
            return serverGetaway.alterar(renavam, carro);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public int getQuantidade() {
        try {
            return serverGetaway.getQuantidade();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
