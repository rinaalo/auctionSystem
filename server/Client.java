public class Client {
    private String clientId;
    private String email;
    private String password;
    private ClientType type;

    public Client(String clientId, String email, String password, ClientType type) {
        this.clientId = clientId;
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }
}
