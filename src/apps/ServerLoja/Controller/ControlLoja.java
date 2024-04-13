package apps.ServerLoja.Controller;

import apps.Categoria;
import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Interfaces.ServerLoja.ServerLojaInterface;
import apps.Records.Carro;
import apps.Records.IpPort;
import apps.ServerLoja.Model.ModelCarrosLoja;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedList;

public class ControlLoja implements ServerLojaInterface {
    private ModelCarrosLoja model;

    LinkedList<ServerDBInterface> replicDBsConnected;
    LinkedList<ServerDBInterface> replicDBsTotal;
    ServerDBInterface connection;
    ServerDBInterface connectionLider;
    int idPreferencia;
    int currentConection;

    public ControlLoja(ArrayList<IpPort> ports, int idPreferencia) {
        connectDB(ports);
        validateReplicas();
        this.model = new ModelCarrosLoja(replicDBsConnected.get(newMainConnection()), replicDBsConnected.get(0));
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
        model.changeConnectedDB(replicDBsConnected.get(newMainConnection()), replicDBsConnected.get(0));
    }

    private int newMainConnection(){
        if(idPreferencia > replicDBsConnected.size()){
            currentConection = replicDBsConnected.size()-1;
            return  currentConection;
        }
        return idPreferencia;
    }

    private void connectDB(ArrayList<IpPort> ports) {
        for (IpPort port : ports){
            try {

                Registry registryDB = LocateRegistry.getRegistry(port.ip(), port.port());
                ServerDBInterface serverDB = (ServerDBInterface) registryDB.lookup("Carros");
                replicDBsTotal.add(serverDB);
                System.out.println("Conexão com o servidor "+ port.ip() + " feita na porta " + port.port());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
