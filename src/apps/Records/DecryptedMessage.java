package apps.Records;

import apps.MessageType;

public record DecryptedMessage(String message, MessageType messageType) {
    public DecryptedMessage {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (messageType == null) {
            throw new IllegalArgumentException("MessageType cannot be null");
        }
    }
}
