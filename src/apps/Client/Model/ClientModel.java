package apps.Client.Model;

import apps.Categoria;
import apps.Interfaces.ServerGetawayInterface;
import apps.Interfaces.ServerLoja.ServerLojaInterface;
import apps.Records.Carro;
import apps.Records.IpPort;
import apps.Records.User;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * classe responsavel por lidar com as conexões com o servidor de gateway
 */
public class ClientModel {

    ServerGetawayInterface serverGetaway;

    LinkedList<ServerGetawayInterface> replicGetawayConnected;
    LinkedList<ServerGetawayInterface> replicGetawayTotal;
    int idPreferencia;
    int currentConection;

    public ClientModel(ArrayList<IpPort> ports, int idPreferencia) {
        this.replicGetawayConnected = new LinkedList<>();
        this.replicGetawayTotal = new LinkedList<>();
        this.connectGetaway(ports);
        this.idPreferencia = idPreferencia;
        validateReplicas();
    }

    private void connectGetaway(ArrayList<IpPort> ports) {
        for (IpPort port : ports){
            try {
                Registry registryDB = LocateRegistry.getRegistry(port.ip(), port.port());
                ServerGetawayInterface serverGetaway = (ServerGetawayInterface) registryDB.lookup("Getaway");
                replicGetawayTotal.add(serverGetaway);
                System.out.println("Conexão com o servidor de getaway " + port.ip() + " feita na porta " + port.port());
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Erro ao conectar com o servidor de getaway "+ port.ip() + " na porta " + port.port());
                e.printStackTrace();
            }
        }
    }

    private void validateReplicas() {
        this.replicGetawayConnected.clear();
        this.replicGetawayTotal.forEach(replica -> {
            try {
                if (replica.isAlive()) {
                    replicGetawayConnected.add(replica);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        if (replicGetawayConnected.isEmpty()) {
            System.out.println("Nenhuma replica disponivel");
        } else {
            this.currentConection = newMainConnection();
            this.serverGetaway = replicGetawayConnected.get(currentConection);
        }

    }

    private int newMainConnection(){
        if(idPreferencia >= replicGetawayConnected.size()){

            if (replicGetawayConnected.size() == 1) {
                return 0;
            }

            this.currentConection = replicGetawayConnected.size() - 1;
            return  currentConection;
        }
        return idPreferencia;
    }

//    public User autenticar(String login, String senha) {
//        try {
//            return serverGetaway.login(login, senha);
//        } catch (RemoteException e) {
//            throw new RuntimeException(e);
//        }
//    }

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
