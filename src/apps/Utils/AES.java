package apps.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

//classe para utilizar o algoritmo de criptografia AES
public class AES {
    //método para criptografar uma string com uma chave secreta AES baseada em senha
    public static String encryptPasswordBased(String strToEncrypt, SecretKey secret) {
        try {
            //cria um objeto de criptografia AES
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            //inicializa o objeto de criptografia com o modo de criptografia e a chave secreta
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            //retorna a string criptografada

            String encryptedString = Arrays.toString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));

            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
    //método para descriptografar uma string com uma chave secreta AES baseada em senha
    public static String decryptPasswordBased(String strToDecrypt, SecretKey secret) {
        try {
            //cria um objeto de criptografia AES
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            //inicializa o objeto de criptografia com o modo de descriptografia e a chave secreta
            cipher.init(Cipher.DECRYPT_MODE, secret);
            //retorna a string descriptografada
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
    /**
     * Gera uma chave secreta a partir de uma senha e um salt
     * Explicacao: a chave secreta AES pode ser derivada de uma determinada senha usando
     * uma função de derivação de chave baseada em senha, como PBKDF2.
     * Também precisamos de um valor salt para transformar uma senha em uma chave secreta.
     * O salt também é um valor aleatório.
     * @param password senha
     * @param salt salt é um valor aleatório que é utilizado para aumentar a segurança da chave
     * @return chave secreta
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        // PBKDF2 com SHA-256 como função de hash
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        // Gera a chave secreta de 256 bits
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);

        // Converte a chave secreta em um objeto SecretKey para ser utilizada
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

}
