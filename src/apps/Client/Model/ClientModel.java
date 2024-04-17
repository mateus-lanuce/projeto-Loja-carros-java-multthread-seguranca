package apps.Client.Model;

import apps.Categoria;
import apps.Interfaces.ServerGetawayInterface;
import apps.MessageType;
import apps.Records.*;
import apps.Utils.*;

import javax.crypto.SecretKey;
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

    /**
     * salt para criptografia enviado para o servidor na hora do login
     * será usado no AES para critografar e descriptografar baseado na senha da conta e no salt recebido
     */
    String mySalt;
    private PublicKey myPublicKey;
    /**
     * salt recebido do servidor na hora do login para garantir a autenticidade das mensagens
     */
    String serverGerawaySalt;
    SecretKey secretKeyGetaway;
    private RSA rsa = new RSA();

    private PublicKey publicKeyServerGetaway;

    LinkedList<String> serverGetawaySalts;
    LinkedList<SecretKey> secretKeysGetaway;
    LinkedList<PublicKey> publicKeysGetaway;

    String password = "senha";

    ArrayList<IpPort> ports;

    public ClientModel(ArrayList<IpPort> ports, int idPreferencia) {
        this.replicGetawayConnected = new LinkedList<>();
        this.replicGetawayTotal = new LinkedList<>();
        this.serverGetawaySalts = new LinkedList<>();
        this.secretKeysGetaway = new LinkedList<>();
        this.publicKeysGetaway = new LinkedList<>();

        this.mySalt = SaltValue.getSaltString(10);
        this.myPublicKey = rsa.getPublicKey();
        this.ports = ports;

        this.idPreferencia = idPreferencia;
        this.validateReplicas();
    }

    private void connectGetaway(ArrayList<IpPort> ports) {

        //limpar as conexões
        replicGetawayTotal.clear();
        replicGetawayConnected.clear();
        serverGetawaySalts.clear();
        secretKeysGetaway.clear();
        publicKeysGetaway.clear();

        for (IpPort port : ports){
            try {
                Registry registryDB = LocateRegistry.getRegistry(port.ip(), port.port());
                ServerGetawayInterface serverGetaway = (ServerGetawayInterface) registryDB.lookup("Getaway");
                replicGetawayTotal.add(serverGetaway);

                //fazer login no servidor de gateway
                String serverSalt = serverGetaway.getServerSalt();
                PublicKey publicKey = serverGetaway.getServerPublicKey();
                serverGetaway.setClientSalt(this.mySalt);
                serverGetaway.setClientPublicKey(this.myPublicKey);

                System.out.println("Salt do servidor de getaway: " + serverSalt);
                System.out.println("Chave publica do servidor de getaway: " + publicKey);

                SecretKey secretKey = AES.getKeyFromPassword(password, mySalt);

                Message responseEncrypted = serverGetaway.serverLogin(encryptGetawayMessage("login", mySalt, secretKey, serverSalt));

                DecryptedMessage response = decryptGetawayMessage(responseEncrypted, mySalt, secretKey, serverSalt, publicKey);

                if (response.messageType() == MessageType.SUCCESS) {
                    this.serverGetawaySalts.add(serverSalt);
                    this.secretKeysGetaway.add(secretKey);
                    this.publicKeysGetaway.add(publicKey);
                } else {
                    System.out.println("Erro ao conectar com o servidor de getaway "+ port.ip() + " na porta " + port.port());
                }

                System.out.println("Conexão com o servidor de getaway " + port.ip() + " feita na porta " + port.port());
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Erro ao conectar com o servidor de getaway "+ port.ip() + " na porta " + port.port());
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void validateReplicas() {

        this.connectGetaway(ports);

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
            this.serverGerawaySalt = serverGetawaySalts.get(currentConection);
            this.secretKeyGetaway = secretKeysGetaway.get(currentConection);
            this.publicKeyServerGetaway = publicKeysGetaway.get(currentConection);
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

    public User autenticar(String login, String senha) {
        try {
            return serverGetaway.login(login, senha);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Carro adicionar(Carro carro) {
        try {

            //criar a mensagem a ser enviada
            Message message = encryptGetawayMessage(carro.toString());

            //descriptografar a mensagem recebida
            DecryptedMessage response = decryptGetawayMessage(serverGetaway.adicionar(message));

            if (response.messageType() == MessageType.SUCCESS) {
                return carro;
            } else {
                throw new RuntimeException("Erro ao adicionar o carro");
            }

            //return serverGetaway.adicionar(message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<Carro> apagarPorNome(String nome) {
        try {

            //criar a mensagem a ser enviada
            Message message = encryptGetawayMessage(nome);

            //descriptografar a mensagem recebida
            LinkedList<Carro> response = new LinkedList<>();

            for (Message m : serverGetaway.removerPorNome(message)) {
                DecryptedMessage decryptedMessage = decryptGetawayMessage(m);
                if (decryptedMessage.messageType() == MessageType.SUCCESS) {
                    response.add(Carro.fromString(decryptedMessage.message()));
                } else {
                    throw new RuntimeException("Erro ao remover o carro");
                }
            }

            return response;

//            return serverGetaway.removerPorNome(nome);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Carro apagarPorRenavam(String renavam) {
        try {

            //criar a mensagem a ser enviada
            Message message = encryptGetawayMessage(renavam);

            //descriptografar a mensagem recebida
            DecryptedMessage response = decryptGetawayMessage(serverGetaway.remover(message));

            if (response.messageType() == MessageType.SUCCESS) {
                return getCarroPorRenavam(renavam);
            } else {
                throw new RuntimeException("Erro ao remover o carro");
            }

            //return serverGetaway.remover(renavam);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<Carro> getCarros(Categoria categoria) {
        try {

            Message message;

            if (categoria == null) {
                message = encryptGetawayMessage("TODOS");
            } else {
                message = encryptGetawayMessage(categoria.toString());
            }

            //descriptografar a mensagem recebida
            LinkedList<Carro> response = new LinkedList<>();

            for (Message m : serverGetaway.getCarros(message)) {
                DecryptedMessage decryptedMessage = decryptGetawayMessage(m);
                if (decryptedMessage.messageType() == MessageType.SUCCESS) {
                    response.add(Carro.fromString(decryptedMessage.message()));
                } else {
                    throw new RuntimeException("Erro ao obter os carros");
                }
            }

            return response;
//            LinkedList<Carro> carros = serverGetaway.getCarros(categoria);
//            return serverGetaway.getCarros(categoria);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Carro getCarroPorRenavam(String renavam) {
        try {

            //criar a mensagem a ser enviada
            Message message = encryptGetawayMessage(renavam);

            //descriptografar a mensagem recebida
            DecryptedMessage response = decryptGetawayMessage(serverGetaway.getCarroByRenavam(message));

            if (response.messageType() == MessageType.SUCCESS) {
                return Carro.fromString(response.message());
            } else {
                throw new RuntimeException("Erro ao obter o carro");
            }

            //return serverGetaway.getCarroByRenavam(renavam);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<Carro> getCarrosPorNome(String nome) {
        try {

            //criar a mensagem a ser enviada
            Message message = encryptGetawayMessage(nome);

            //descriptografar a mensagem recebida
            LinkedList<Carro> response = new LinkedList<>();

            for (Message m : serverGetaway.getCarrosByNome(message)) {
                DecryptedMessage decryptedMessage = decryptGetawayMessage(m);
                if (decryptedMessage.messageType() == MessageType.SUCCESS) {
                    response.add(Carro.fromString(decryptedMessage.message()));
                } else {
                    throw new RuntimeException("Erro ao obter os carros");
                }
            }

            return response;

//            return serverGetaway.getCarrosByNome(nome);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Carro alterar(String renavam, Carro carro) {
        try {

            //criar a mensagem a ser enviada
            Message message = encryptGetawayMessage(carro.toString());
            Message renavamMessage = encryptGetawayMessage(renavam);

            //descriptografar a mensagem recebida
            DecryptedMessage response = decryptGetawayMessage(serverGetaway.alterar(renavamMessage, message));

            if (response.messageType() == MessageType.SUCCESS) {
                return carro;
            } else {
                throw new RuntimeException("Erro ao alterar o carro");
            }

            //return serverGetaway.alterar(renavam, carro);
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

    /**
     * criptografar a mensagem a ser enviada
     * não tem um type na mensagem enviada ao servidor de autenticação
     * @param message
     * @return
     */
    private Message encryptGetawayMessage(String message){
        String encryptedVernan = CifraVernam.encrypt(message, mySalt);
        String encryptedMessage = AES.encryptPasswordBased(encryptedVernan, secretKeyGetaway);
        String hmacValue = Hmac.generateHmac(message, serverGerawaySalt);

        //assinar o hmac
        String signature = rsa.sign(hmacValue);

        Message messageObject = new Message(encryptedMessage, hmacValue, signature);

        System.out.println();
        System.out.println("Mensagem descriptografada enviada: " + message);
        System.out.println("Mensagem criptografada enviada: " + messageObject);
        System.out.println();

        return messageObject;
    }

    private Message encryptGetawayMessage(String message, String salt, SecretKey secretKey, String serverSalt) {
        String encryptedVernan = CifraVernam.encrypt(message, salt);
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


    /**
     * descriptografar a mensagem recebida
     * as mensagens que vem do servidor tem um campo Type que indica o tipo da mensagem
     * @param message
     * @return
     */
    private DecryptedMessage decryptGetawayMessage(Message message) {
        String decryptedMessage = AES.decryptPasswordBased(message.message(), secretKeyGetaway);
        String decryptedVernam = CifraVernam.decrypt(decryptedMessage, mySalt);

        String generatedHmac = Hmac.generateHmac(decryptedVernam, serverGerawaySalt);

        //verificar a assinatura do hmac
        if (!rsa.verify(message.hmac(), message.signature(), publicKeyServerGetaway)) {
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

    private DecryptedMessage decryptGetawayMessage(Message message, String salt, SecretKey secretKey, String serverSalt, PublicKey publicKeyServerGetaway) {
        String decryptedMessage = AES.decryptPasswordBased(message.message(), secretKey);
        String decryptedVernam = CifraVernam.decrypt(decryptedMessage, salt);

        String generatedHmac = Hmac.generateHmac(decryptedVernam, serverSalt);

        //verificar a assinatura do hmac
        if (!rsa.verify(message.hmac(), message.signature(), publicKeyServerGetaway)) {
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
