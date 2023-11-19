import java.util.LinkedList;
import java.util.List;

public class RegisteredClient {
    private String clientId;
    private String email;
    private String password;
    private ClientType type;
    private List<AuctionItem> items;
    private List<Auction> auctions;

    public RegisteredClient(String clientId, String email, String password, ClientType type) {
        this.clientId = clientId;
        this.email = email;
        this.password = password;
        this.type = type;
        this.items = new LinkedList<>();
        this.auctions = new LinkedList<>();
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

    public List<AuctionItem> getItems() {
        return items;
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    public void addItem(AuctionItem item) {
        items.add(item);
    }

    public void addAuction(Auction auction) {
        auctions.add(auction);
    }
}
