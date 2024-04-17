package apps.Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

// classe para gerar o HMAC de uma mensagem
public class Hmac {

    public Hmac() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Gera o HMAC de uma mensagem.
     * @param message a mensagem a ser gerado o HMAC.
     * @param key a chave para gerar o HMAC.
     * @return o HMAC da mensagem.
     */
    public static String generateHmac(String message, String key) {
        try {
            //cria um objeto de criptografia HMAC
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            //cria uma chave secreta a partir da chave
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            //inicializa o objeto de criptografia com a chave secreta
            sha256_HMAC.init(secret_key);
            //retorna o HMAC da mensagem
            return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while generating HMAC: " + e.toString());
        }
        return null;
    }
}
