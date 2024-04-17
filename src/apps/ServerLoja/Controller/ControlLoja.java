package apps.ServerLoja.Controller;

import apps.Categoria;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Interfaces.ServerLoja.ServerLojaInterfaceInterface;
import apps.Records.Carro;
import apps.Records.IpPort;
import apps.Records.Message;
import apps.Records.PublicKey;
import apps.ServerLoja.Model.ModelCarrosLoja;
import apps.Utils.RSA;

import javax.crypto.SecretKey;
import java.io.Serial;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

public class ControlLoja extends UnicastRemoteObject implements ServerLojaInterfaceInterface {
    @Serial
    private static final long serialVersionUID = 1L;

    private ModelCarrosLoja model;

    LinkedList<ServerDBInterface> replicDBsConnected;
    LinkedList<ServerDBInterface> replicDBsTotal;
    ServerDBInterface connection;
    ServerDBInterface connectionLider;
    int idPreferencia;
    int currentConection;

    //segurança
    /**
     * salt para criptografia vai ser recebido do cliente na hora do login
     * será usado no AES para critografar e descriptografar baseado na senha da conta e no salt recebido
     */
    String clientSalt;
    /**
     * salt enviado para o cliente na hora do login para garantir a autenticidade das mensagens
     */
    String serverSalt;
    SecretKey secretKey;
    private RSA rsa = new RSA();

    private PublicKey publicKeyClient;

    private boolean isLogged = false;
    private String password;

    public ControlLoja(ArrayList<IpPort> ports, int idPreferencia) throws RemoteException {
        super();
        this.password = "senha";
        this.replicDBsConnected = new LinkedList<>();
        this.replicDBsTotal = new LinkedList<>();
        this.connectDB(ports);
        this.idPreferencia = idPreferencia;
        validateReplicas();
    }

    private void validateReplicas() {
        this.replicDBsConnected.clear();
        this.replicDBsTotal.forEach(replica -> {
            try {
                if (replica.isAlive()) {
                    replicDBsConnected.add(replica);
                    System.out.println("Conexão com o servidor " + replica + " feita com sucesso");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        if (replicDBsConnected.isEmpty()) {
            System.out.println("Nenhuma replica disponivel");
        } else {
            if (this.model == null) {
                this.model = new ModelCarrosLoja(replicDBsConnected.get(newMainConnection()), replicDBsConnected.get(0));
                return;
            }
            this.model.changeConnectedDB(replicDBsConnected.get(newMainConnection()), replicDBsConnected.get(0));
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
                ServerDBInterface serverDB = (ServerDBInterface) registryDB.lookup("Carros");
                replicDBsTotal.add(serverDB);
                System.out.println("Conexão com o servidor "+ port.ip() + " feita na porta " + port.port());
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Erro ao conectar com o servidor "+ port.ip() + " na porta " + port.port());
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
    public void setClientSalt(String clientSalt) throws Exception {

    }

    @Override
    public String getServerSalt() throws Exception {
        return "";
    }

    @Override
    public void setClientPublicKey(PublicKey clientPublicKey) throws Exception {

    }

    @Override
    public PublicKey getServerPublicKey() throws Exception {
        return null;
    }

    @Override
    public Message serverLogin(Message message) throws Exception {
        return null;
    }
}
