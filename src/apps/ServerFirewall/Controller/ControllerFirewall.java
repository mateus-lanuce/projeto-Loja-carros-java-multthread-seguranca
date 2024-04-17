package apps.ServerFirewall.Controller;

import apps.Categoria;
import apps.Interfaces.ServerFirewall.ServerFirewallInterface;
import apps.MessageType;
import apps.Records.*;
import apps.ServerFirewall.Model.ModelFirewall;
import apps.Utils.*;

import javax.crypto.SecretKey;
import javax.security.auth.login.LoginException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

public class ControllerFirewall extends UnicastRemoteObject implements ServerFirewallInterface {
    private final ModelFirewall model;

    //segurança
    private RSA rsa = new RSA();
    private String clientSalt;
    private String serverSalt;
    private PublicKey publicKeyClient;
    private SecretKey secretKey;
    private String password = "senha";
    private boolean isLogged = false;

    ArrayList<String> ipsPermitidos;

    public ControllerFirewall(ArrayList<IpPort> portsDB, int idPreferenciaDB, IpPort AuthServer, ArrayList<String> ipsPermitidos) throws RemoteException {
        super();
        this.ipsPermitidos = ipsPermitidos;
        this.serverSalt = SaltValue.getSaltString(10);
        this.model = new ModelFirewall(portsDB, idPreferenciaDB, AuthServer);
    }

   @Override
    public Message adicionar(Message carro) throws IllegalArgumentException, RemoteException {

        if(verifyPermission("adicionar um carro")) {
            return encryptMessage("IP não permitido", MessageType.ERROR);
        }

        if (!isLogged) {
            return encryptMessage("Usuário não logado", MessageType.ERROR);
        }

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(carro);

        //verificar se a mensagem é de erro
        if (decryptedMessage.contains("Assinatura inválida") || decryptedMessage.contains("Mensagem corrompida")) {
            return encryptMessage("Mensagem corrompida", MessageType.ERROR);
        }

        //transformar a mensagem em objeto
        Carro carroObj = Carro.fromString(decryptedMessage);

        //enviar para o model
        Carro carroRetorno = model.adicionar(carroObj);

        //criar a mensagem de retorno
        return encryptMessage(carroRetorno.toString(), MessageType.SUCCESS);

//        return model.adicionar(carro);
    }

   @Override
    public Message remover(Message renavam) throws IllegalArgumentException, RemoteException {

        if (verifyPermission("remover um carro")) {
            return encryptMessage("IP não permitido", MessageType.ERROR);
        }

        if (!isLogged) {
            return encryptMessage("Usuário não logado", MessageType.ERROR);
        }

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(renavam);

        //verificar se a mensagem é de erro
        if (decryptedMessage.contains("Assinatura inválida") || decryptedMessage.contains("Mensagem corrompida")) {
            return encryptMessage("Mensagem corrompida", MessageType.ERROR);
        }

        //enviar para o model
        Carro carroRetorno = model.remover(decryptedMessage);

        //criar a mensagem de retorno
        return encryptMessage(carroRetorno.toString(), MessageType.SUCCESS);

        //return model.remover(renavam);
    }

   @Override
    public LinkedList<Message> removerPorNome(Message nome) throws RemoteException {

        if (verifyPermission("remover carros por nome")) {
            LinkedList<Message> list = new LinkedList<>();
            list.add(encryptMessage("IP não permitido", MessageType.ERROR));
            return list;
        }

        if (!isLogged) {
            LinkedList<Message> list = new LinkedList<>();
            list.add(encryptMessage("Usuário não logado", MessageType.ERROR));
            return list;
        }

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(nome);

        //verificar se a mensagem é de erro
        if (decryptedMessage.contains("Assinatura inválida") || decryptedMessage.contains("Mensagem corrompida")) {
            LinkedList<Message> list = new LinkedList<>();
            list.add(encryptMessage("Mensagem corrompida", MessageType.ERROR));
            return list;
        }

        //enviar para o model
        LinkedList<Carro> carrosRetorno = model.removerPorNome(decryptedMessage);

        //criar a mensagem de retorno
        LinkedList<Message> list = new LinkedList<>();
        for (Carro carro : carrosRetorno) {
            list.add(encryptMessage(carro.toString(), MessageType.SUCCESS));
        }

        return list;
        //return model.removerPorNome(nome);
    }

   @Override
    public LinkedList<Message> getCarros(Message categoria) throws RemoteException {

        if (verifyPermission("obter carros")) {
            LinkedList<Message> list = new LinkedList<>();
            list.add(encryptMessage("IP não permitido", MessageType.ERROR));
            return list;
        }

        if (!isLogged) {
            LinkedList<Message> list = new LinkedList<>();
            list.add(encryptMessage("Usuário não logado", MessageType.ERROR));
            return list;
        }

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(categoria);

        //verificar se a mensagem é de erro
        if (decryptedMessage.contains("Assinatura inválida") || decryptedMessage.contains("Mensagem corrompida")) {
            LinkedList<Message> list = new LinkedList<>();
            list.add(encryptMessage("Mensagem corrompida", MessageType.ERROR));
            return list;
        }

        LinkedList<Carro> carrosRetorno;

        if (decryptedMessage.equals("TODOS")) {
            carrosRetorno = model.getCarros(null);
        } else {
            carrosRetorno = model.getCarros(Categoria.valueOf(decryptedMessage));
        }

        //criar a mensagem de retorno
        LinkedList<Message> list = new LinkedList<>();
        for (Carro carro : carrosRetorno) {
            list.add(encryptMessage(carro.toString(), MessageType.SUCCESS));
        }

        return list;
        //return model.getCarros(categoria);
    }

   @Override
    public LinkedList<Message> getCarrosByNome(Message nome) throws RemoteException {

        if (verifyPermission("obter carros por nome")) {
            LinkedList<Message> list = new LinkedList<>();
            list.add(encryptMessage("IP não permitido", MessageType.ERROR));
            return list;
        }

        if (!isLogged) {
            LinkedList<Message> list = new LinkedList<>();
            list.add(encryptMessage("Usuário não logado", MessageType.ERROR));
            return list;
        }

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(nome);

        //verificar se a mensagem é de erro
        if (decryptedMessage.contains("Assinatura inválida") || decryptedMessage.contains("Mensagem corrompida")) {
            LinkedList<Message> list = new LinkedList<>();
            list.add(encryptMessage("Mensagem corrompida", MessageType.ERROR));
            return list;
        }

        //enviar para o model
        LinkedList<Carro> carrosRetorno = model.getCarrosByNome(decryptedMessage);

        //criar a mensagem de retorno
        LinkedList<Message> list = new LinkedList<>();

        for (Carro carro : carrosRetorno) {
            list.add(encryptMessage(carro.toString(), MessageType.SUCCESS));
        }

        return list;
        //return model.getCarrosByNome(nome);
    }

   @Override
    public Message getCarroByRenavam(Message renavam) throws RemoteException {

        if (verifyPermission("obter carro por renavam")) {
            return encryptMessage("IP não permitido", MessageType.ERROR);
        }

        if (!isLogged) {
            return encryptMessage("Usuário não logado", MessageType.ERROR);
        }

        //descriptografar a mensagem
        String decryptedMessage = decryptMessage(renavam);

        //verificar se a mensagem é de erro
        if (decryptedMessage.contains("Assinatura inválida") || decryptedMessage.contains("Mensagem corrompida")) {
            return encryptMessage("Mensagem corrompida", MessageType.ERROR);
        }

        //enviar para o model
        Carro carroRetorno = model.getCarroByRenavam(decryptedMessage);

        //criar a mensagem de retorno
        return encryptMessage(carroRetorno.toString(), MessageType.SUCCESS);

        //return model.getCarroByRenavam(renavam);
    }

   @Override
    public Message alterar(Message renavam, Message carro) throws IllegalArgumentException, RemoteException {

        if (verifyPermission("alterar um carro")) {
            return encryptMessage("IP não permitido", MessageType.ERROR);
        }

        if (!isLogged) {
            return encryptMessage("Usuário não logado", MessageType.ERROR);
        }

        //descriptografar a mensagem
        String decryptedRenavam = decryptMessage(renavam);

        //verificar se a mensagem é de erro
        if (decryptedRenavam.contains("Assinatura inválida") || decryptedRenavam.contains("Mensagem corrompida")) {
            return encryptMessage("Mensagem corrompida", MessageType.ERROR);
        }

        String decryptedCarro = decryptMessage(carro);

        //verificar se a mensagem é de erro
        if (decryptedCarro.contains("Assinatura inválida") || decryptedCarro.contains("Mensagem corrompida")) {
            return encryptMessage("Mensagem corrompida", MessageType.ERROR);
        }

        //enviar para o model
        Carro carroRetorno = model.alterar(decryptedRenavam, Carro.fromString(decryptedCarro));

        //criar a mensagem de retorno
        return encryptMessage(carroRetorno.toString(), MessageType.SUCCESS);

        //return model.alterar(renavam, carro);
    }

   
    public int getQuantidade() throws RemoteException {
        return model.getQuantidade();
    }

   
    public boolean isAlive() throws RemoteException {
        return true;
    }

   @Override
    public Message login(Message email, Message password) throws RemoteException {

        if (verifyPermission("logar")) {
            throw new RuntimeException("IP não permitido");
        }

        //descriptografar a mensagem
        String decryptedEmail = decryptMessage(email);

        //verificar se a mensagem é de erro
        if (decryptedEmail.contains("Assinatura inválida") || decryptedEmail.contains("Mensagem corrompida")) {
            return encryptMessage("Mensagem corrompida", MessageType.ERROR);
        }

        String decryptedPassword = decryptMessage(password);

        //verificar se a mensagem é de erro
        if (decryptedPassword.contains("Assinatura inválida") || decryptedPassword.contains("Mensagem corrompida")) {
            return encryptMessage("Mensagem corrompida", MessageType.ERROR);
        }

        //enviar para o model
        try {
            return encryptMessage(model.login(decryptedEmail, decryptedPassword).toString(), MessageType.SUCCESS);
        } catch (LoginException e) {
            return encryptMessage(e.getMessage(), MessageType.ERROR);
        }
    }

   
    public void addUser(User user) throws RemoteException {
        model.addUser(user);
    }

    private boolean verifyPermission(String operation) {
        try {
            String clientHost = RemoteServer.getClientHost();
            System.out.println(clientHost + " solicitou " + operation);

            //verificar se o ip do cliente é permitido
            if (!ipsPermitidos.contains(clientHost)) {
                return true;
            }
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }

        return false;
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
