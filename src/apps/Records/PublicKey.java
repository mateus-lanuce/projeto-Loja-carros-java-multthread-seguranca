package apps.Records;

import java.io.Serializable;
import java.math.BigInteger;

public record PublicKey(BigInteger publicKey, BigInteger modulus) implements Serializable {
    public PublicKey {
        if (publicKey == null) {
            throw new IllegalArgumentException("Public key cannot be null");
        }
        if (modulus == null) {
            throw new IllegalArgumentException("Modulus cannot be null");
        }
    }

    @Override
    public String toString() {
        return "PublicKey{" +
                "publicKey=" + publicKey +
                ", modulus=" + modulus +
                '}';
    }
}
