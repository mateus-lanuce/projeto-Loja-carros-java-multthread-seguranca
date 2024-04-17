package apps.ServerAuth.Entity;

import apps.Interfaces.UsersInterface;
import apps.MessageType;
import apps.Records.Message;
import apps.Records.PublicKey;
import apps.Records.User;
import apps.Utils.*;

import javax.crypto.SecretKey;
import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Users extends UnicastRemoteObject implements UsersInterface {

    @Serial
    private static final long serialVersionUID = 1L;

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

    private final ArrayList<User> users;
    public Users() throws RemoteException {
        super();
        users = new ArrayList<>();
        this.serverSalt = SaltValue.getSaltString(10);
        this.password = "senha";
    }


    @Override
    public Message login(Message userString) throws RemoteException {

        if (!isLogged) {
            return encryptMessage("Usuário não logado no servidor para realizar a operação de login", MessageType.ERROR);
        } else {

            User user = User.fromString(decryptMessage(userString));

            //segurança é tudo kkkkkk
            //não é a melhor forma de fazer isso, mas é o que temos pra hoje
            //não façam isso em casa
            //não façam isso em produção
            //não façam isso em lugar nenhum
            //não façam isso
            //não
            User userFound = users.stream().filter(u -> u.email().equals(user.email())).findFirst().orElse(null);

            if (userFound == null) {
                return encryptMessage("Usuário não encontrado", MessageType.ERROR);
            }

            if (userFound.password().equals(user.password())) {
                return encryptMessage(userFound.toString(), MessageType.SUCCESS);
            } else {
                return encryptMessage("Senha inválida", MessageType.ERROR);
            }

        }
    }

    public void addUser(User user) {
        users.add(user);
        System.out.println("Usuário adicionado: " + user);
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
