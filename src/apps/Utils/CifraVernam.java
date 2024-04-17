package apps.Utils;

//classe para utilizar o algoritmo de criptografia de Vernam
public class CifraVernam {

    public CifraVernam() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Gera o texto cifrado de uma mensagem com uma chave secreta.
     * @param message a mensagem a ser cifrada.
     * @param key a chave secreta para cifrar a mensagem.
     * @return o texto cifrado da mensagem.
     */
    public static String encrypt(String message, String key) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char caractere = message.charAt(i);
            // Garante que a chave seja repetida, se necessário
            char chaveChar = key.charAt(i % key.length());
            // XOR para cifrar
            char cifrado = (char) (caractere ^ chaveChar);
            resultado.append(cifrado);
        }
        return resultado.toString();
    }

    /**
     * Gera o texto decifrado de uma mensagem com uma chave secreta.
     * @param message a mensagem a ser decifrada.
     * @param key a chave secreta para decifrar a mensagem.
     * @return o texto decifrado da mensagem.
     */
    public static String decrypt(String message, String key) {
        return encrypt(message, key);
    }

}
