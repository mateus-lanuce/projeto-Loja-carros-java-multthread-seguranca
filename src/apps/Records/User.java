package apps.Records;

import java.io.Serializable;

public record User(String email, String password, boolean client) implements Serializable {
    public User {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }
}
