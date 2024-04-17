package apps.ServerGetaway;

import apps.Categoria;
import apps.Interfaces.ServerFirewall.ServerFirewallInterface;
import apps.Interfaces.ServerGetawayInterface;
import apps.MessageType;
import apps.Records.*;
import apps.Utils.*;

import javax.crypto.SecretKey;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class ServerGetaway extends UnicastRemoteObject implements ServerGetawayInterface {

    private ServerFirewallInterface serverFirewallConnection;

    LinkedList<ServerFirewallInterface> replicFirewallConnected;
    LinkedList<ServerFirewallInterface> replicFirewallTotal;
    int idPreferencia;
    int currentConection;

    //segurança
    private RSA rsa = new RSA();
    private String clientSalt;
    private String serverSalt;
    private PublicKey publicKeyClient;
    private SecretKey secretKey;
    private String password = "senha";
    private boolean isLogged = false;

    /**
     * salt para criptografia enviado para o servidor na hora do login
     * será usado no AES para critografar e descriptografar baseado na senha da conta e no salt recebido
     */
    String mySalt;
    private PublicKey myPublicKey;
    /**
     * salt recebido do servidor na hora do login para garantir a autenticidade das mensagens
     */
    String serverFirewallSalt;
    SecretKey secretKeyFirewall;
    private RSA rsaFirewall = new RSA();

    private PublicKey publicKeyServerFirewall;

    LinkedList<String> serverFirewallSalts;
    LinkedList<SecretKey> secretKeysFirewall;
    LinkedList<PublicKey> publicKeysFirewall;

    ArrayList<IpPort> ports;

    public ServerGetaway(ArrayList<IpPort> portsDB, int idPreferenciaFirewall) throws RemoteException {
        super();
        try {
            this.ports = portsDB;
            this.serverSalt = SaltValue.getSaltString(10);

            this.mySalt = SaltValue.getSaltString(10);
            this.myPublicKey = rsaFirewall.getPublicKey();

            //iniciar as listas
            this.serverFirewallSalts = new LinkedList<>();
            this.secretKeysFirewall = new LinkedList<>();
            this.publicKeysFirewall = new LinkedList<>();

            this.replicFirewallConnected = new LinkedList<>();
            this.replicFirewallTotal = new LinkedList<>();
            this.idPreferencia = idPreferenciaFirewall;
            validateReplicas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message adicionar(Message carro) throws RemoteException, IllegalArgumentException {

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(carro);

        //transformar a mensagem em um objeto carro
        Carro carroObject = Carro.fromString(decryptedMessage);

        //validar a conexão com o servidor de firewall
        validateReplicas();

        //criptografar a mensagem para enviar para o servidor de firewall
        Message carroEncrypted = encryptFirewallMessage(carroObject.toString());

        //enviar a mensagem para o servidor de firewall
        Message carroAdicionado = serverFirewallConnection.adicionar(carroEncrypted);

        //descriptografar a resposta
        DecryptedMessage response = decryptFirewallMessage(carroAdicionado);

        if (response.messageType() == MessageType.ERROR) {
            throw new IllegalArgumentException(response.message());
        }

        //transformar a mensagem em um objeto carro
        Carro carroAdicionadoObject = Carro.fromString(response.message());

        //criptografar a resposta
        return encryptMessage(carroAdicionadoObject.toString(), MessageType.SUCCESS);

//        Carro carroAdicionado = serverFirewallConnection.adicionar(carroObject);
//
//        //criptografar a resposta
//        return encryptMessage(carroAdicionado.toString(), MessageType.SUCCESS);

       //return serverFirewallConnection.adicionar(carro);
    }

    @Override
    public Message remover(Message renavam) throws RemoteException, IllegalArgumentException {

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(renavam);

        //validar a conexão com o servidor de firewall
        validateReplicas();

        //criptografar a mensagem para enviar para o servidor de firewall
        Message renavamEncrypted = encryptFirewallMessage(decryptedMessage);

        //enviar a mensagem para o servidor de firewall
        Message carroRemovido = serverFirewallConnection.remover(renavamEncrypted);

        //descriptografar a resposta
        DecryptedMessage response = decryptFirewallMessage(carroRemovido);

        if (response.messageType() == MessageType.ERROR) {
            throw new IllegalArgumentException(response.message());
        }

        //transformar a mensagem em um objeto carro
        Carro carroRemovidoObject = Carro.fromString(response.message());

        //criptografar a resposta
        return encryptMessage(carroRemovidoObject.toString(), MessageType.SUCCESS);

        //enviar a mensagem para o servidor de firewall
        //Carro carroRemovido = serverFirewallConnection.remover(decryptedMessage);



        //criptografar a resposta
        //return encryptMessage(carroRemovido.toString(), MessageType.SUCCESS);

        //return serverFirewallConnection.remover(renavam);
    }

    @Override
    public LinkedList<Message> removerPorNome(Message nome) throws RemoteException, IllegalArgumentException {

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(nome);

        //validar a conexão com o servidor de firewall
        validateReplicas();

        //criptografar a mensagem para enviar para o servidor de firewall
        Message nomeEncrypted = encryptFirewallMessage(decryptedMessage);

        //enviar a mensagem para o servidor de firewall
        LinkedList<Message> carrosRemovidos = serverFirewallConnection.removerPorNome(nomeEncrypted);

        LinkedList<Message> carrosRemovidosMessage = new LinkedList<>();

        //descriptografar a resposta
        carrosRemovidos.forEach(carro -> {
            DecryptedMessage response = decryptFirewallMessage(carro);
            if (response.messageType() == MessageType.SUCCESS) {
                carrosRemovidosMessage.add(encryptMessage(response.message(), MessageType.SUCCESS));
            } else {
                carrosRemovidosMessage.add(encryptMessage(response.message(), MessageType.ERROR));
            }
        });

        //criptografar a resposta
        return carrosRemovidosMessage;

        //enviar a mensagem para o servidor de firewall
//        LinkedList<Carro> carrosRemovidos = serverFirewallConnection.removerPorNome(decryptedMessage);
//
//        LinkedList<Message> carrosRemovidosMessage = new LinkedList<>();
//
//        carrosRemovidos.forEach(carro -> {
//            carrosRemovidosMessage.add(encryptMessage(carro.toString(), MessageType.SUCCESS));
//        });
//
//        if (carrosRemovidos.isEmpty()) {
//            throw new IllegalArgumentException("não existe um carro com o nome " + decryptedMessage);
//        }

        //return carrosRemovidosMessage;

//
//
//        LinkedList<Carro> carrosRemovidos = serverFirewallConnection.removerPorNome(nome);
//
//        if (carrosRemovidos == null) {
//            throw new IllegalArgumentException("não existe um carro com o nome " + nome);
//        }
//
//        return carrosRemovidos;
    }

    @Override
    public LinkedList<Message> getCarros(Message categoria) throws RemoteException, RuntimeException {

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(categoria);

        //validar a conexão com o servidor de firewall
        validateReplicas();

        //enviar a mensagem para o servidor de firewall

        LinkedList<Message> carros;

        if (decryptedMessage.equals("TODOS")) {
            //criptografar a mensagem para enviar para o servidor de firewall
            carros = serverFirewallConnection.getCarros(encryptFirewallMessage("TODOS"));
        } else {
            //criptografar a mensagem para enviar para o servidor de firewall
            carros = serverFirewallConnection.getCarros(encryptFirewallMessage(decryptedMessage));
        }

        LinkedList<Message> carrosMessage = new LinkedList<>();

        carros.forEach(carro -> {
            DecryptedMessage response = decryptFirewallMessage(carro);
            if (response.messageType() == MessageType.SUCCESS) {
                carrosMessage.add(encryptMessage(response.message(), MessageType.SUCCESS));
            } else {
                carrosMessage.add(encryptMessage(response.message(), MessageType.ERROR));
            }
        });

        if (carrosMessage.isEmpty()) {
            throw new RuntimeException("não existem carros cadastrados");
        }

        return carrosMessage;

//        LinkedList<Carro> carros = serverFirewallConnection.getCarros(categoria);
//
//        if (carros == null && categoria != null) {
//            throw new RuntimeException("não existem carros com a categoria " + categoria);
//        } else if (carros == null) {
//            throw new RuntimeException("não existem carros cadastrados");
//        }
//
//        return carros;
    }

    @Override
    public LinkedList<Message> getCarrosByNome(Message nome) throws RemoteException, IllegalArgumentException {

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(nome);

        //validar a conexão com o servidor de firewall
        validateReplicas();

        //criptografar a mensagem para enviar para o servidor de firewall
        Message nomeEncrypted = encryptFirewallMessage(decryptedMessage);

        //enviar a mensagem para o servidor de firewall
        LinkedList<Message> carros = serverFirewallConnection.getCarrosByNome(nomeEncrypted);

        LinkedList<Message> carrosMessage = new LinkedList<>();

        carros.forEach(carro -> {
            DecryptedMessage response = decryptFirewallMessage(carro);
            if (response.messageType() == MessageType.SUCCESS) {
                carrosMessage.add(encryptMessage(response.message(), MessageType.SUCCESS));
            } else {
                throw new IllegalArgumentException(response.message());
            }
        });

        if (carrosMessage.isEmpty()) {
            throw new IllegalArgumentException("não existem carros com o nome " + decryptedMessage);
        }

        return carrosMessage;

        //enviar a mensagem para o servidor de firewall
//        LinkedList<Carro> carros = serverFirewallConnection.getCarrosByNome(decryptedMessage);
//
//        LinkedList<Message> carrosMessage = new LinkedList<>();
//
//        carros.forEach(carro -> {
//            carrosMessage.add(encryptMessage(carro.toString(), MessageType.SUCCESS));
//        });
//
//        if (carrosMessage.isEmpty()) {
//            throw new IllegalArgumentException("não existem carros com o nome " + decryptedMessage);
//        }
//
//        return carrosMessage;

//            LinkedList<Carro> carros = serverFirewallConnection.getCarrosByNome(nome);
//
//            if (carros == null) {
//                throw new IllegalArgumentException("não existem carros com o nome " + nome);
//            }
//
//            return carros;
    }

    @Override
    public Message alterar(Message renavam, Message carro) throws RemoteException, IllegalArgumentException {

        //descriptografar a mensagem
        String decryptedRenavam = decryptMessage(renavam);
        String decryptedCarro = decryptMessage(carro);

        //transformar a mensagem em um objeto carro
        Carro carroObject = Carro.fromString(decryptedCarro);

        //validar a conexão com o servidor de firewall
        validateReplicas();

        //criptografar a mensagem para enviar para o servidor de firewall
        Message renavamEncrypted = encryptFirewallMessage(decryptedRenavam);
        Message carroEncrypted = encryptFirewallMessage(carroObject.toString());

        //enviar a mensagem para o servidor de firewall
        Message carroAlterado = serverFirewallConnection.alterar(renavamEncrypted, carroEncrypted);

        //descriptografar a resposta
        DecryptedMessage response = decryptFirewallMessage(carroAlterado);

        if (response.messageType() == MessageType.ERROR) {
            throw new IllegalArgumentException(response.message());
        }

        //transformar a mensagem em um objeto carro
        Carro carroAlteradoObject = Carro.fromString(response.message());

        //criptografar a resposta
        return encryptMessage(carroAlteradoObject.toString(), MessageType.SUCCESS);
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
    public Message getCarroByRenavam(Message renavam) {
        try {

            //descriptografar a mensagem
            String decryptedMessage = decryptMessage(renavam);

            //validar a conexão com o servidor de firewall
            validateReplicas();

            //criptografar a mensagem para enviar para o servidor de firewall
            Message renavamEncrypted = encryptFirewallMessage(decryptedMessage);

            //enviar a mensagem para o servidor de firewall
            Message carro = serverFirewallConnection.getCarroByRenavam(renavamEncrypted);

            //descriptografar a resposta
            DecryptedMessage response = decryptFirewallMessage(carro);

            if (response.messageType() == MessageType.ERROR) {
                throw new IllegalArgumentException(response.message());
            }

            //transformar a mensagem em um objeto carro
            Carro carroObject = Carro.fromString(response.message());

            //criptografar a resposta
            return encryptMessage(carroObject.toString(), MessageType.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public User login(String email, String senha) {
        try {

            //validar a conexão com o servidor de firewall
            validateReplicas();

            //criptografar a mensagem para enviar para o servidor de firewall
            Message emailEncryptedFirewall = encryptFirewallMessage(email);
            Message senhaEncryptedFirewall = encryptFirewallMessage(senha);

            //enviar a mensagem para o servidor de firewall
            Message user = serverFirewallConnection.login(emailEncryptedFirewall, senhaEncryptedFirewall);

            //descriptografar a resposta
            DecryptedMessage response = decryptFirewallMessage(user);

            if (response.messageType() == MessageType.ERROR) {
                throw new IllegalArgumentException(response.message());
            }

            //transformar a mensagem em um objeto user
            User userObject = User.fromString(response.message());

            return userObject;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

        this.connectFirewall(ports);

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
            this.serverFirewallSalt = serverFirewallSalts.get(currentConection);
            this.secretKeyFirewall = secretKeysFirewall.get(currentConection);
            this.publicKeyServerFirewall = publicKeysFirewall.get(currentConection);
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

        //limpar as listas
        this.serverFirewallSalts.clear();
        this.secretKeysFirewall.clear();
        this.publicKeysFirewall.clear();
        this.replicFirewallConnected.clear();
        this.replicFirewallTotal.clear();

        for (IpPort port : ports){
            try {
                Registry registryDB = LocateRegistry.getRegistry(port.ip(), port.port());
                ServerFirewallInterface serverFirewall = (ServerFirewallInterface) registryDB.lookup("Firewall");
                replicFirewallTotal.add(serverFirewall);

                //fazer login no servidor de firewall
                String serverSalt = serverFirewall.getServerSalt();
                PublicKey publicKey = serverFirewall.getServerPublicKey();
                serverFirewall.setClientSalt(this.mySalt);
                serverFirewall.setClientPublicKey(this.myPublicKey);

                System.out.println("Salt do servidor de firewall: " + serverSalt);
                System.out.println("Chave publica do servidor de firewall: " + publicKey);

                SecretKey secretKey = AES.getKeyFromPassword(password, mySalt);

                Message responseEncrypted = serverFirewall.serverLogin(encryptFirewallMessage("login", mySalt, secretKey, serverSalt));

                DecryptedMessage response = decryptFirewallMessage(responseEncrypted, mySalt, secretKey, serverSalt, publicKey);

                if (response.messageType() == MessageType.SUCCESS) {
                    this.serverFirewallSalts.add(serverSalt);
                    this.secretKeysFirewall.add(secretKey);
                    this.publicKeysFirewall.add(publicKey);
                } else {
                    System.out.println("Erro ao conectar com o servidor de firewall "+ port.ip() + " na porta " + port.port());
                }

                System.out.println("Conexão com o servidor de firewall " + port.ip() + " feita na porta " + port.port());
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Erro ao conectar com o servidor de firewall "+ port.ip() + " na porta " + port.port());
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
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
        this.clientSalt = clientSalt;
        System.out.println("Salt do cliente: " + clientSalt);
    }

    @Override
    public String getServerSalt() throws Exception {
        return serverSalt;
    }

    @Override
    public void setClientPublicKey(PublicKey clientPublicKey) throws Exception {
        this.publicKeyClient = clientPublicKey;
        System.out.println("Public key do cliente: " + publicKeyClient.toString());
    }

    @Override
    public PublicKey getServerPublicKey() throws Exception {
        return rsa.getPublicKey();
    }

    @Override
    public Message serverLogin(Message message) throws Exception {
        //gerar a chave secreta para ser usada no AES para criptografar e descriptografar
        try {
            this.secretKey = AES.getKeyFromPassword(password, clientSalt);

            String decryptedMessage = AES.decryptPasswordBased(message.message(), secretKey);
            String decryptedVernam = CifraVernam.decrypt(decryptedMessage, clientSalt);

            String hmac = message.hmac();

            String generatedHmac = Hmac.generateHmac(decryptedVernam, serverSalt);

            //verificar a assinatura do hmac
            if (!rsa.verify(message.hmac(), message.signature(), publicKeyClient)) {
                return encryptMessage("Assinatura inválida", MessageType.ERROR);
            }

            if (hmac.equals(generatedHmac)) {
                System.out.println("HMAC válido");
            } else {
                System.out.println("HMAC inválido");
                return encryptMessage("HMAC inválido", MessageType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.isLogged = true;

        return encryptMessage("login successful", MessageType.SUCCESS);
    }

    private Message encryptMessage(String message, MessageType type) {

        String formattedMessage = message + " : " + type.toString();

        String encryptedVernan = CifraVernam.encrypt(formattedMessage, clientSalt);
        String encryptedMessage = AES.encryptPasswordBased(encryptedVernan, secretKey);

        String hmacValue = Hmac.generateHmac(formattedMessage, serverSalt);

        //assinar o hmac
        String signature = rsa.sign(hmacValue);

        Message messageObject = new Message(encryptedMessage, hmacValue, signature);

        System.out.println();
        System.out.println("Mensagem descriptografada enviada: " + formattedMessage);
        System.out.println("Mensagem criptografada enviada: " + messageObject);
        System.out.println();

        return messageObject;
    }

    private Message encryptFirewallMessage(String message) {
        String encryptedVernan = CifraVernam.encrypt(message, mySalt);
        String encryptedMessage = AES.encryptPasswordBased(encryptedVernan, secretKeyFirewall);
        String hmacValue = Hmac.generateHmac(message, serverFirewallSalt);

        //assinar o hmac
        String signature = rsaFirewall.sign(hmacValue);

        Message messageObject = new Message(encryptedMessage, hmacValue, signature);

        System.out.println();
        System.out.println("Mensagem descriptografada enviada: " + message);
        System.out.println("Mensagem criptografada enviada: " + messageObject);
        System.out.println();

        return messageObject;
    }

    private Message encryptFirewallMessage(String message, String salt, SecretKey secretKey, String serverSalt) {
        String encryptedVernan = CifraVernam.encrypt(message, salt);
        String encryptedMessage = AES.encryptPasswordBased(encryptedVernan, secretKey);
        String hmacValue = Hmac.generateHmac(message, serverSalt);

        //assinar o hmac
        String signature = rsaFirewall.sign(hmacValue);

        Message messageObject = new Message(encryptedMessage, hmacValue, signature);

        System.out.println();
        System.out.println("Mensagem descriptografada enviada: " + message);
        System.out.println("Mensagem criptografada enviada: " + messageObject);
        System.out.println();

        return messageObject;
    }

    private DecryptedMessage decryptFirewallMessage(Message message) {
        String decryptedMessage = AES.decryptPasswordBased(message.message(), secretKeyFirewall);
        String decryptedVernam = CifraVernam.decrypt(decryptedMessage, mySalt);

        String generatedHmac = Hmac.generateHmac(decryptedVernam, serverFirewallSalt);

        //verificar a assinatura do hmac
        if (!rsaFirewall.verify(message.hmac(), message.signature(), publicKeyServerFirewall)) {
            return new DecryptedMessage("Assinatura inválida", MessageType.ERROR);
        }

        //verificar se o HMAC da mensagem recebida é igual ao HMAC gerado
        if (!generatedHmac.equals(message.hmac())) {
            return new DecryptedMessage("Mensagem corrompida", MessageType.ERROR);
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

    private DecryptedMessage decryptFirewallMessage(Message message, String salt, SecretKey secretKey, String serverSalt, PublicKey publicKeyServerGetaway) {
        String decryptedMessage = AES.decryptPasswordBased(message.message(), secretKey);
        String decryptedVernam = CifraVernam.decrypt(decryptedMessage, salt);

        String generatedHmac = Hmac.generateHmac(decryptedVernam, serverSalt);

        //verificar a assinatura do hmac
        if (!rsaFirewall.verify(message.hmac(), message.signature(), publicKeyServerGetaway)) {
            return new DecryptedMessage("Assinatura inválida", MessageType.ERROR);
        }

        //verificar se o HMAC da mensagem recebida é igual ao HMAC gerado
        if (!generatedHmac.equals(message.hmac())) {
            return new DecryptedMessage("Mensagem corrompida", MessageType.ERROR);
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

    private Message encryptMessage(String message) {
        String encryptedVernan = CifraVernam.encrypt(message, clientSalt);
        String encryptedMessage = AES.encryptPasswordBased(encryptedVernan, secretKey);
        String hmacValue = Hmac.generateHmac(message, serverSalt);

        //assinar o hmac
        String signature = rsa.sign(hmacValue);

        Message messageObject = new Message(encryptedMessage, hmacValue, signature);

        System.out.println();
        System.out.println("Mensagem descriptografada enviada: " + message);
        System.out.println("Mensagem criptografada enviada: " + messageObject);
        System.out.println();

        return messageObject;
    }

    private String decryptMessage(Message message) {
        String decryptedMessage = AES.decryptPasswordBased(message.message(), secretKey);
        String decryptedVernam = CifraVernam.decrypt(decryptedMessage, clientSalt);

        String generatedHmac = Hmac.generateHmac(decryptedVernam, serverSalt);

        //verificar a assinatura do hmac
        if (!rsa.verify(message.hmac(), message.signature(), publicKeyClient)) {
            return "Assinatura inválida";
        }

        //verificar se o HMAC da mensagem recebida é igual ao HMAC gerado
        if (!generatedHmac.equals(message.hmac())) {
            return "Mensagem corrompida";
        }

        System.out.println();
        System.out.println("Mensagem criptografada recebida: " + message);
        System.out.println("Mensagem descriptografada recebida: " + decryptedVernam);
        System.out.println();

        return decryptedVernam;
    }
}
