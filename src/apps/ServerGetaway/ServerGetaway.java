package apps.ServerGetaway;

import apps.Categoria;
import apps.Interfaces.CarrosInterface;
import apps.Interfaces.ServerGetawayInterface;
import apps.Interfaces.UsersInterface;
import apps.Records.Carro;
import apps.Records.User;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

public class ServerGetaway extends UnicastRemoteObject implements ServerGetawayInterface {

    private Registry registryDB;
    private CarrosInterface serverDB;

    private Registry registryAuth;
    private UsersInterface serverAuth;

    public ServerGetaway() throws RemoteException {
        super();
        try {
             //se conecta com o servidor de banco de dados
             this.connectDB();
                //se conecta com o servidor de autenticação
            this.connectAuth();
        } catch (Exception e) {
             e.printStackTrace();
        }
    }

    @Override
    public Carro adicionar(Carro carro) throws RemoteException, IllegalArgumentException {
       return serverDB.adicionar(carro);
    }

    @Override
    public Carro remover(String renavam) throws RemoteException, IllegalArgumentException {
        return serverDB.remover(renavam);
    }

    @Override
    public LinkedList<Carro> removerPorNome(String nome) throws RemoteException, IllegalArgumentException {

        LinkedList<Carro> carrosRemovidos = serverDB.removerPorNome(nome);

        if (carrosRemovidos == null) {
            throw new IllegalArgumentException("não existe um carro com o nome " + nome);
        }

        return carrosRemovidos;
    }

    @Override
    public LinkedList<Carro> getCarros(Categoria categoria) throws RemoteException, RuntimeException {

        LinkedList<Carro> carros = serverDB.getCarros(categoria);

        if (carros == null && categoria != null) {
            throw new RuntimeException("não existem carros com a categoria " + categoria);
        } else if (carros == null) {
            throw new RuntimeException("não existem carros cadastrados");
        }

        return carros;
    }

    @Override
    public LinkedList<Carro> getCarrosByNome(String nome) throws RemoteException, IllegalArgumentException {

            LinkedList<Carro> carros = serverDB.getCarrosByNome(nome);

            if (carros == null) {
                throw new IllegalArgumentException("não existem carros com o nome " + nome);
            }

            return carros;
    }

    @Override
    public Carro alterar(String renavam, Carro carro) throws RemoteException, IllegalArgumentException {
        return serverDB.alterar(renavam, carro);
    }

    @Override
    public int getQuantidade() throws RemoteException {
        try {
            return serverDB.getQuantidade();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Carro getCarroByRenavam(String renavam) {
        try {
            return serverDB.getCarroByRenavam(renavam);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User login(String email, String senha) {
        try {
            return serverAuth.login(email, senha);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void addUser(User user) {
        try {
            serverAuth.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void connectDB() {
        try {
            registryDB = LocateRegistry.getRegistry(1099);
            serverDB = (CarrosInterface) registryDB.lookup("Carros");
            System.out.println("Conexão com o servidor de banco de dados estabelecida");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectAuth() {
        try {
            registryAuth = LocateRegistry.getRegistry(1100);
            serverAuth = (UsersInterface) registryAuth.lookup("Users");
            System.out.println("Conexão com o servidor de autenticação estabelecida");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            //cria o objeto remoto
            ServerGetawayInterface serverGetaway = new ServerGetaway();

            Registry registry = LocateRegistry.createRegistry(1101);

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

}
