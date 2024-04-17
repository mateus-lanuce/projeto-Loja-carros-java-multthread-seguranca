package apps.ServerFirewall.Model;

import apps.Categoria;
import apps.Interfaces.ServerLoja.ServerLojaInterfaceInterface;
import apps.Interfaces.UsersInterface;
import apps.MessageType;
import apps.Records.*;
import apps.Utils.*;

import javax.crypto.SecretKey;
import javax.security.auth.login.LoginException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedList;

public class ModelFirewall {
    ServerLojaInterfaceInterface serverLojaConnection;
    UsersInterface authConnection;

    LinkedList<ServerLojaInterfaceInterface> replicDBsConnected;
    LinkedList<ServerLojaInterfaceInterface> replicDBsTotal;
    int idPreferencia;
    int currentConection;

    //segurança
    private RSA rsa = new RSA();
    private String authServerSalt;
    private PublicKey publicKeyAuthServer;
    private SecretKey secretKeyAuthServer;

    private String salt;
    private PublicKey publicKey;
    private String password = "senha";

    public ModelFirewall(ArrayList<IpPort> portsDB, int idPreferenciaDB, IpPort AuthServer) {
        this.salt = SaltValue.getSaltString(10);
        this.publicKey = rsa.getPublicKey();


        this.replicDBsConnected = new LinkedList<>();
        this.replicDBsTotal = new LinkedList<>();
        this.connectDB(portsDB);
        this.connectAuth(AuthServer);
        this.idPreferencia = idPreferenciaDB;
        validateReplicas();

        //adicionar carros iniciais
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

        //adicionar dados de teste 3 usuários
        User jorgeClient = new User("jorge@jorge.com", "123", true);
        User anaAdmin = new User("ana@ana.com", "123", false);
        User pedroClient = new User("pedro@exemplo.com", "456", true);
        User carlaAdmin = new User("carla@exemplo.com", "789", false);
        User tiagoClient = new User("tiago@exemplo.com", "101112", true);

        try {
            this.adicionar(carro1);
            this.adicionar(carro2);
            this.adicionar(carro3);
            this.adicionar(carro4);
            this.adicionar(carro5);
            this.adicionar(carro6);
            this.adicionar(carro7);
            this.adicionar(carro8);
            this.adicionar(carro9);
            this.adicionar(carro10);
            this.adicionar(carro11);
            this.adicionar(carro12);

            this.addUser(jorgeClient);
            this.addUser(anaAdmin);
            this.addUser(pedroClient);
            this.addUser(carlaAdmin);
            this.addUser(tiagoClient);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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

    
    public User login(String email, String password) throws RemoteException, LoginException {
        //this.validateReplicas();

        //criar o objeto user
        User user = new User(email, password, true);

        Message response = authConnection.login(encryptAuthMessage(user.toString()));

        DecryptedMessage responseDecrypted = decryptAuthMessage(response);

        if (responseDecrypted.messageType() == MessageType.SUCCESS) {
            return User.fromString(responseDecrypted.message());
        } else {
            throw new LoginException(responseDecrypted.message());
        }
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
                ServerLojaInterfaceInterface serverDB = (ServerLojaInterfaceInterface) registryDB.lookup("Lojas");
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

            //fazer login no servidor de autenticação
            this.authConnection.setClientSalt(salt);
            this.authServerSalt = this.authConnection.getServerSalt();
            this.publicKeyAuthServer = this.authConnection.getServerPublicKey();
            this.authConnection.setClientPublicKey(publicKey);

            //gerar a chave secreta
            this.secretKeyAuthServer = AES.getKeyFromPassword(password, salt);

            Message responseEncrypted = this.authConnection.serverLogin(encryptAuthMessage("login"));
            DecryptedMessage responseDecrypted = decryptAuthMessage(responseEncrypted);

            if (responseDecrypted.messageType() == MessageType.SUCCESS) {
                System.out.println("Conexão com o servidor de autenticação estabelecida");
                System.out.println("Login no servidor de autenticação feito com sucesso");
            } else {
                throw new LoginException("Erro ao fazer login no servidor de autenticação: " + responseDecrypted.message());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * criptografar a mensagem a ser enviada
     * não tem um type na mensagem enviada ao servidor de autenticação
     * @param message
     * @return
     */
    private Message encryptAuthMessage(String message){
        String encryptedVernan = CifraVernam.encrypt(message, salt);
        String encryptedMessage = AES.encryptPasswordBased(encryptedVernan, secretKeyAuthServer);
        String hmacValue = Hmac.generateHmac(message, authServerSalt);

        //assinar o hmac
        String signature = rsa.sign(hmacValue);

        Message messageObject = new Message(encryptedMessage, hmacValue, signature);

        System.out.println();
        System.out.println("Mensagem descriptografada enviada: " + message);
        System.out.println("Mensagem criptografada enviada: " + messageObject);
        System.out.println();

        return messageObject;
    }

    /**
     * descriptografar a mensagem recebida
     * as mensagens que vem do servidor tem um campo Type que indica o tipo da mensagem
     * @param message
     * @return
     */
    private DecryptedMessage decryptAuthMessage(Message message) {
        String decryptedMessage = AES.decryptPasswordBased(message.message(), secretKeyAuthServer);
        String decryptedVernam = CifraVernam.decrypt(decryptedMessage, salt);

        String generatedHmac = Hmac.generateHmac(decryptedVernam, authServerSalt);

        //verificar a assinatura do hmac
        if (!rsa.verify(message.hmac(), message.signature(), publicKeyAuthServer)) {
            return new DecryptedMessage("Assinatura inválida", MessageType.ERROR);
        }

        //verificar se o HMAC da mensagem recebida é igual ao HMAC gerado
        if (!generatedHmac.equals(message.hmac())) {
            return new DecryptedMessage("HMAC inválido", MessageType.ERROR);
        }

        System.out.println();
        System.out.println("Mensagem criptografada recebida: " + message);
        System.out.println("Mensagem descriptografada recebida: " + decryptedVernam);
        System.out.println();

        String[] data = decryptedVernam.split(" : ");
        String messageDecrypted = data[0];
        MessageType type = MessageType.valueOf(data[1]);

        return new DecryptedMessage(messageDecrypted, type);
    }
}
