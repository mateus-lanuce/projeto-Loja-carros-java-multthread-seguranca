package apps.Utils;

import apps.Records.PublicKey;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * A classe RSA implementa um algoritmo de criptografia assimétrica RSA.
 * Ele gera chaves públicas e privadas, assina mensagens e verifica assinaturas.
 */
public class RSA {

    private static final int BIT_LENGTH = 1024;
    private static final BigInteger ONE = BigInteger.ONE;

    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;

    /**
     * Construtor da classe RSA.
     * Gera chaves públicas e privadas.
     */
    public RSA() {
        generateKeys();
    }

    /**
     * Gera chaves públicas e privadas.
     * Utiliza o algoritmo RSA para gerar as chaves.
     */
    private void generateKeys() {
        BigInteger p = generatePrime();
        BigInteger q = generatePrime();

        modulus = p.multiply(q);

        BigInteger phi = p.subtract(ONE).multiply(q.subtract(ONE));

        publicKey = BigInteger.valueOf(65537); // Valor comum para 'e'

        privateKey = publicKey.modInverse(phi);
    }

    /**
     * Gera um número primo aleatório.
     * Utiliza o algoritmo de geração de números primos do BigInteger.
     * 
     * @return O número primo gerado.
     */
    private BigInteger generatePrime() {
        return BigInteger.probablePrime(BIT_LENGTH, new SecureRandom());
    }

    /**
     * Assina uma mensagem utilizando a chave privada.
     * 
     * @param message A mensagem a ser assinada.
     * @return A assinatura da mensagem.
     */
    public String sign(String message) {
        BigInteger m = new BigInteger(message.getBytes());
        BigInteger signature = m.modPow(privateKey, modulus);
        return Base64.getEncoder().encodeToString(signature.toByteArray());
    }

    /**
     * Obtém a chave pública.
     * 
     * @return A chave pública.
     */
    public PublicKey getPublicKey() {
        return new PublicKey(publicKey, modulus);
    }

    /**
     * Verifica a assinatura de uma mensagem utilizando a chave pública.
     * 
     * @param message A mensagem a ser verificada.
     * @param signature A assinatura da mensagem.
     * @param publicKey A chave pública utilizada para verificar a assinatura.
     * @return true se a assinatura é válida, false caso contrário.
     */
    public boolean verify(String message, String signature, PublicKey publicKey) {
        BigInteger m = new BigInteger(message.getBytes());
        BigInteger s = new BigInteger(Base64.getDecoder().decode(signature));
        BigInteger originalMessage = s.modPow(publicKey.publicKey(), publicKey.modulus());
        return m.equals(originalMessage);
    }
}

class RSATest {
    public static void main(String[] args) {
        RSA rsa = new RSA();
        String message = "Mensagem a ser assinada";
        String signature = rsa.sign(message);
        System.out.println("Assinatura: " + signature);

        // Obtém a chave pública
        PublicKey publicKey = rsa.getPublicKey();

        // Verificação da assinatura usando outra instância
        RSA rsaTest = new RSA();
        boolean verified = rsaTest.verify(message, signature, publicKey);
        System.out.println("Assinatura verificada: " + verified);
    }
}
