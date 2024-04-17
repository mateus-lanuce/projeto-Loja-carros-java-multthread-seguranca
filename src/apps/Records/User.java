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

    /**
     * return a User object from a string in the format User[email=email, password=password, client=true]
     * @param user
     * @return
     */
    public static User fromString(String user) {
        String[] parts = user.split(",");
        String email = parts[0].split("=")[1];
        String password = parts[1].split("=")[1];
        boolean client = Boolean.parseBoolean(parts[2].split("=")[1].replace("]", ""));
        return new User(email, password, client);
    }
}
