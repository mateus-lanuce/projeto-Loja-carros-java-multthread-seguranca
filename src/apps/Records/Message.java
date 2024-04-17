package apps.Records;

import java.io.Serializable;

/**
 * Represents a message with its message, hmac information.
 */
public record Message(String message, String hmac, String signature) implements Serializable {
    public Message {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        if (hmac == null || hmac.isBlank()) {
            throw new IllegalArgumentException("HMAC cannot be null or empty");
        }
        if (signature == null || signature.isBlank()) {
            throw new IllegalArgumentException("Signature cannot be null or empty");
        }
    }
}
