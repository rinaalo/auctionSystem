import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ClientAccount implements Serializable {
    private String clientId;
    private String email;
    private String password;
    private List<AuctionItem> items;
    private List<Auction> auctions;

    public ClientAccount(String clientId, String email, String password) {
        this.clientId = clientId;
        this.email = email;
        this.password = password;
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
