package apps.Records;

public record IpPort(String ip, int port) {
    public IpPort {
        if (ip == null || ip.isBlank()) {
            throw new IllegalArgumentException("IP não pode ser nulo ou vazio");
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Porta inválida");
        }
    }
}
