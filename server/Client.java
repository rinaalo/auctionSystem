public class Client {
    private String name;
    private String email;
    private int clientId;
    private ClientType type;
    public Client(String name, String email, int clientId, ClientType type) {
        this.name = name;
        this.email = email;
        this.clientId = clientId;
        this.type = type;
    }
    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
}
