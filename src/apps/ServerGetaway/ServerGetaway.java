package apps.ServerGetaway;

import apps.Categoria;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Interfaces.ServerFirewall.ServerFirewallInterface;
import apps.Interfaces.ServerGetawayInterface;
import apps.Interfaces.ServerLoja.ServerLojaInterface;
import apps.Records.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

public class ServerGetaway extends UnicastRemoteObject implements ServerGetawayInterface {

    private ServerFirewallInterface serverFirewallConnection;

    LinkedList<ServerFirewallInterface> replicFirewallConnected;
    LinkedList<ServerFirewallInterface> replicFirewallTotal;
    int idPreferencia;
    int currentConection;

    public ServerGetaway(ArrayList<IpPort> portsDB, int idPreferenciaFirewall) throws RemoteException {
        super();
        try {
            this.replicFirewallConnected = new LinkedList<>();
            this.replicFirewallTotal = new LinkedList<>();
            this.connectFirewall(portsDB);
            this.idPreferencia = idPreferenciaFirewall;
            validateReplicas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Carro adicionar(Carro carro) throws RemoteException, IllegalArgumentException {
       return serverFirewallConnection.adicionar(carro);
    }

    @Override
    public Carro remover(String renavam) throws RemoteException, IllegalArgumentException {
        return serverFirewallConnection.remover(renavam);
    }

    @Override
    public LinkedList<Carro> removerPorNome(String nome) throws RemoteException, IllegalArgumentException {

        LinkedList<Carro> carrosRemovidos = serverFirewallConnection.removerPorNome(nome);

        if (carrosRemovidos == null) {
            throw new IllegalArgumentException("não existe um carro com o nome " + nome);
        }

        return carrosRemovidos;
    }

    @Override
    public LinkedList<Carro> getCarros(Categoria categoria) throws RemoteException, RuntimeException {

        LinkedList<Carro> carros = serverFirewallConnection.getCarros(categoria);

        if (carros == null && categoria != null) {
            throw new RuntimeException("não existem carros com a categoria " + categoria);
        } else if (carros == null) {
            throw new RuntimeException("não existem carros cadastrados");
        }

        return carros;
    }

    @Override
    public LinkedList<Carro> getCarrosByNome(String nome) throws RemoteException, IllegalArgumentException {

            LinkedList<Carro> carros = serverFirewallConnection.getCarrosByNome(nome);

            if (carros == null) {
                throw new IllegalArgumentException("não existem carros com o nome " + nome);
            }

            return carros;
    }

    @Override
    public Carro alterar(String renavam, Carro carro) throws RemoteException, IllegalArgumentException {
        return serverFirewallConnection.alterar(renavam, carro);
    }

    @Override
    public int getQuantidade() throws RemoteException {
        try {
            return serverFirewallConnection.getQuantidade();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Carro getCarroByRenavam(String renavam) {
        try {
            return serverFirewallConnection.getCarroByRenavam(renavam);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public User login(String email, String senha) {
//        try {
//            return serverFirewallConnection.login(email, senha);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
        return null;
    }

    @Override
    public Message login(Message userString) throws RemoteException {
        return null;
    }

    @Override
    public void addUser(User user) {
        try {
            serverFirewallConnection.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void validateReplicas() {
        this.replicFirewallConnected.clear();
        this.replicFirewallTotal.forEach(replica -> {
            try {
                if (replica.isAlive()) {
                    replicFirewallConnected.add(replica);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        if (replicFirewallConnected.isEmpty()) {
            System.out.println("Nenhuma replica de firewall disponivel");
        } else {
            this.currentConection = newMainConnection();
            this.serverFirewallConnection = replicFirewallConnected.get(currentConection);
        }

    }

    private int newMainConnection(){
        if(idPreferencia >= replicFirewallConnected.size()){

            if (replicFirewallConnected.size() == 1) {
                return 0;
            }

            this.currentConection = replicFirewallConnected.size() - 1;
            return  currentConection;
        }
        return idPreferencia;
    }

    private void connectFirewall(ArrayList<IpPort> ports) {
        for (IpPort port : ports){
            try {
                Registry registryDB = LocateRegistry.getRegistry(port.ip(), port.port());
                ServerFirewallInterface serverFirewall = (ServerFirewallInterface) registryDB.lookup("Firewall");
                replicFirewallTotal.add(serverFirewall);
                System.out.println("Conexão com o servidor de firewall "+ port.ip() + " feita na porta " + port.port());
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Erro ao conectar com o servidor de firewall "+ port.ip() + " na porta " + port.port());
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }

    public static void main(String[] args) {

        try {
            ArrayList<IpPort> ports = new ArrayList<>();
            IpPort port1 = new IpPort("", 1121);
            ports.add(port1);

            //cria o objeto remoto
            ServerGetawayInterface serverGetaway = new ServerGetaway(ports, 1);

            Registry registry = LocateRegistry.createRegistry(1131);

            //registra o objeto remoto
            registry.rebind("Getaway", serverGetaway);

            //adicionar dados de teste 12 carros de 3 categorias
            Carro carro1 = new Carro("123", "Fusca", Categoria.ECONOMICO, 1970, 10000);
            Carro carro2 = new Carro("124", "Fiat Uno", Categoria.ECONOMICO, 1990, 15000);
            Carro carro3 = new Carro("125", "Kombi", Categoria.ECONOMICO, 1980, 20000);

            Carro carro4 = new Carro("126", "Gol", Categoria.INTERMEDIARIO, 2000, 25000);
            Carro carro5 = new Carro("127", "Palio", Categoria.INTERMEDIARIO, 2005, 30000);
            Carro carro6 = new Carro("128", "Celta", Categoria.INTERMEDIARIO, 2005, 30000);

            Carro carro7 = new Carro("129", "Civic", Categoria.EXECUTIVO, 2010, 50000);
            Carro carro8 = new Carro("130", "Corolla", Categoria.EXECUTIVO, 2015, 60000);
            Carro carro9 = new Carro("131", "Fusion", Categoria.EXECUTIVO, 2015, 60000);

            Carro carro10 = new Carro("132", "BMW 320", Categoria.EXECUTIVO, 2020, 120000);
            Carro carro11 = new Carro("133", "Mercedes Classe C", Categoria.EXECUTIVO, 2020, 150000);
            Carro carro12 = new Carro("134", "Audi A4", Categoria.EXECUTIVO, 2020, 130000);

            serverGetaway.adicionar(carro1);
            serverGetaway.adicionar(carro2);
            serverGetaway.adicionar(carro3);
            serverGetaway.adicionar(carro4);
            serverGetaway.adicionar(carro5);
            serverGetaway.adicionar(carro6);
            serverGetaway.adicionar(carro7);
            serverGetaway.adicionar(carro8);
            serverGetaway.adicionar(carro9);
            serverGetaway.adicionar(carro10);
            serverGetaway.adicionar(carro11);
            serverGetaway.adicionar(carro12);

            //adicionar dados de teste 3 usuários
            User jorgeClient = new User("jorge@jorge.com", "123", true);
            User anaAdmin = new User("ana@ana.com", "123", false);
            User pedroClient = new User("pedro@exemplo.com", "456", true);
            User carlaAdmin = new User("carla@exemplo.com", "789", false);
            User tiagoClient = new User("tiago@exemplo.com", "101112", true);

            serverGetaway.addUser(jorgeClient);
            serverGetaway.addUser(anaAdmin);
            serverGetaway.addUser(pedroClient);
            serverGetaway.addUser(carlaAdmin);
            serverGetaway.addUser(tiagoClient);


            System.out.println("Servidor de gateway pronto");
        } catch (Exception e) {
        e.printStackTrace();
        }

        //testar a conexão com o servidor de banco de dados
//        try {
//
//            Registry registry = LocateRegistry.getRegistry(1099);
//
//            //cria o objeto remoto
//            CarrosInterface carrosInterface = (CarrosInterface) Naming.lookup("Carros");
//
//            //testar
//            Carro jorge = new Carro("123", "Fusca", Categoria.ECONOMICO, 1970, 10000);
//            carrosInterface.adicionar(jorge);
//
//            System.out.println("Carro adicionado com sucesso");
//
//            Carro carro = carrosInterface.getCarroByRenavam("123");
//
//            System.out.println("Carro encontrado: " + carro);
//
//            System.out.println("Conexão com o servidor de banco de dados estabelecida");
//
//            //testar a conexão com o servidor de autenticação
//            //cria o objeto remoto
//            UsersInterface usersInterface = (UsersInterface) Naming.lookup("Users");
//
//            //testar
//            usersInterface.addUser(new User("jorge@jorge.com", "123", false));
//
//            System.out.println("Usuário adicionado com sucesso");
//
//            boolean login = usersInterface.login("jorge@jorge.com", "123");
//
//            System.out.println("Login: " + login);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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
