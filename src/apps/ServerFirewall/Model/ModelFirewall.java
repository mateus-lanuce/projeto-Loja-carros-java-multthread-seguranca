package apps.ServerFirewall.Model;

import apps.Categoria;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Interfaces.ServerGetawayInterface;
import apps.Interfaces.ServerLoja.ServerLojaInterface;
import apps.Interfaces.UsersInterface;
import apps.Records.Carro;
import apps.Records.IpPort;
import apps.Records.User;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedList;

public class ModelFirewall {
    ServerLojaInterface serverLojaConnection;
    UsersInterface authConnection;

    LinkedList<ServerLojaInterface> replicDBsConnected;
    LinkedList<ServerLojaInterface> replicDBsTotal;
    int idPreferencia;
    int currentConection;

    public ModelFirewall(ArrayList<IpPort> portsDB, int idPreferenciaDB, IpPort AuthServer) {
        this.replicDBsConnected = new LinkedList<>();
        this.replicDBsTotal = new LinkedList<>();
        this.connectDB(portsDB);
        this.connectAuth(AuthServer);
        this.idPreferencia = idPreferenciaDB;
        validateReplicas();
    }
                         
    public Carro adicionar(Carro carro) throws IllegalArgumentException, RemoteException {
        this.validateReplicas();
        return serverLojaConnection.adicionar(carro);
    }

    
    public Carro remover(String renavam) throws IllegalArgumentException, RemoteException {
        this.validateReplicas();
        return serverLojaConnection.remover(renavam);
    }

    
    public LinkedList<Carro> removerPorNome(String nome) throws RemoteException {
        this.validateReplicas();
        return serverLojaConnection.removerPorNome(nome);
    }

    
    public LinkedList<Carro> getCarros(Categoria categoria) throws RemoteException {
        this.validateReplicas();
        return serverLojaConnection.getCarros(categoria);
    }

    
    public LinkedList<Carro> getCarrosByNome(String nome) throws RemoteException {
        this.validateReplicas();
        return serverLojaConnection.getCarrosByNome(nome);
    }

    
    public Carro getCarroByRenavam(String renavam) throws RemoteException {
        this.validateReplicas();
        return serverLojaConnection.getCarroByRenavam(renavam);
    }

    
    public Carro alterar(String renavam, Carro carro) throws IllegalArgumentException, RemoteException {
        this.validateReplicas();
        return serverLojaConnection.alterar(renavam, carro);
    }

    
    public int getQuantidade() throws RemoteException {
        this.validateReplicas();
        return serverLojaConnection.getQuantidade();
    }

    
    public User login(String email, String password) throws RemoteException {
        this.validateReplicas();
        return authConnection.login(email, password);
    }

    
    public void addUser(User user) throws RemoteException {
        this.validateReplicas();
        authConnection.addUser(user);
    }

    private void validateReplicas() {
        this.replicDBsConnected.clear();
        this.replicDBsTotal.forEach(replica -> {
            try {
                if (replica.isAlive()) {
                    replicDBsConnected.add(replica);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        if (replicDBsConnected.isEmpty()) {
            System.out.println("Nenhuma replica disponivel");
        } else {
            this.currentConection = newMainConnection();
            this.serverLojaConnection = replicDBsConnected.get(currentConection);
        }

    }

    private int newMainConnection(){
        if(idPreferencia >= replicDBsConnected.size()){
            this.currentConection = replicDBsConnected.size() - 1;
            return  currentConection;
        }
        return idPreferencia;
    }

    private void connectDB(ArrayList<IpPort> ports) {
        for (IpPort port : ports){
            try {
                Registry registryDB = LocateRegistry.getRegistry(port.ip(), port.port());
                ServerLojaInterface serverDB = (ServerLojaInterface) registryDB.lookup("Lojas");
                replicDBsTotal.add(serverDB);
                System.out.println("Conexão com o servidor de loja "+ port.ip() + " feita na porta " + port.port());
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Erro ao conectar com o servidor "+ port.ip() + " na porta " + port.port());
                e.printStackTrace();
            }
        }
    }

    private void connectAuth(IpPort AuthServer) {
        try {
            Registry registryAuth = LocateRegistry.getRegistry(AuthServer.ip(), AuthServer.port());
            this.authConnection = (UsersInterface) registryAuth.lookup("Users");
            System.out.println("Conexão com o servidor de autenticação estabelecida");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
