package apps.Interfaces;

import apps.Records.Message;
import apps.Records.PublicKey;

import java.rmi.Remote;

public interface ServerSecurityInterface extends Remote {

    /**
     * @param clientSalt The client salt to be set.
     * @throws Exception
     */
    void setClientSalt(String clientSalt) throws Exception;

    /**
     * @return The server salt.
     * @throws Exception
     */
    String getServerSalt() throws Exception;

    /**
     * set the client public key
     * @param clientPublicKey The client public key to be set.
     */
    void setClientPublicKey(PublicKey clientPublicKey) throws Exception;

    /**
     * @return The server public key.
     * @throws Exception
     */
    PublicKey getServerPublicKey() throws Exception;

    Message serverLogin(Message message) throws Exception;
}
