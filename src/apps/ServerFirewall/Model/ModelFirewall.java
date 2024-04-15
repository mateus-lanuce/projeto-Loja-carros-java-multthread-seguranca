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

public class ModelFirewall implements ServerGetawayInterface {
    ServerLojaInterface serverLojaConnection;
    UsersInterface authConnection;

    LinkedList<ServerLojaInterface> replicDBsConnected;
    LinkedList<ServerLojaInterface> replicDBsTotal;
    int idPreferencia;
    int currentConection;

    public ModelFirewall(ArrayList<IpPort> portsDB, int idPreferenciaDB, IpPort AuthServer) {
        this.connectDB(portsDB);
        this.connectAuth(AuthServer);
        this.idPreferencia = idPreferenciaDB;
        this.replicDBsConnected = new LinkedList<>();
        this.replicDBsTotal = new LinkedList<>();
        validateReplicas();
    }
                         @Override
    public Carro adicionar(Carro carro) throws IllegalArgumentException, RemoteException {
        this.validateReplicas();
        return serverLojaConnection.adicionar(carro);
    }

    @Override
    public Carro remover(String renavam) throws IllegalArgumentException, RemoteException {
        this.validateReplicas();
        return serverLojaConnection.remover(renavam);
    }

    @Override
    public LinkedList<Carro> removerPorNome(String nome) throws RemoteException {
        this.validateReplicas();
        return serverLojaConnection.removerPorNome(nome);
    }

    @Override
    public LinkedList<Carro> getCarros(Categoria categoria) throws RemoteException {
        this.validateReplicas();
        return serverLojaConnection.getCarros(categoria);
    }

    @Override
    public LinkedList<Carro> getCarrosByNome(String nome) throws RemoteException {
        this.validateReplicas();
        return serverLojaConnection.getCarrosByNome(nome);
    }

    @Override
    public Carro getCarroByRenavam(String renavam) throws RemoteException {
        this.validateReplicas();
        return serverLojaConnection.getCarroByRenavam(renavam);
    }

    @Override
    public Carro alterar(String renavam, Carro carro) throws IllegalArgumentException, RemoteException {
        this.validateReplicas();
        return serverLojaConnection.alterar(renavam, carro);
    }

    @Override
    public int getQuantidade() throws RemoteException {
        this.validateReplicas();
        return serverLojaConnection.getQuantidade();
    }

    @Override
    public User login(String email, String password) throws RemoteException {
        this.validateReplicas();
        return authConnection.login(email, password);
    }

    @Override
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
        if(idPreferencia > replicDBsConnected.size()){

            if (replicDBsConnected.size() == 1) {
                return 0;
            }

            this.currentConection = replicDBsConnected.size() - 1;
            return  currentConection;
        }
        return idPreferencia;
    }

    private void connectDB(ArrayList<IpPort> ports) {
        for (IpPort port : ports){
            try {
                Registry registryDB = LocateRegistry.getRegistry(port.ip(), port.port());
                ServerDBInterface serverDB = (ServerDBInterface) registryDB.lookup("Lojas");
                replicDBsTotal.add((ServerLojaInterface) serverDB);
                System.out.println("Conexão com o servidor "+ port.ip() + " feita na porta " + port.port());
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
