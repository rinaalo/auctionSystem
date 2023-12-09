import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Map;

public class ServerState implements Serializable{
    // auction id, auction
    private Map<String, Auction> auctions;
    // item id, item
    private Map<String, AuctionItem> items;
    // client id, client
    private Map<String, ClientAccount> clients;
    // keys
    private KeyPair kp;

    public ServerState() {
        auctions = new Hashtable<>();
        items = new Hashtable<>();
        clients = new Hashtable<>();
        try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            kp = kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			System.exit(-1);
			e.printStackTrace();
		}
    }

    public Map<String, Auction> getAuctions() {
        return auctions;
    }

    public Map<String, AuctionItem> getItems() {
        return items;
    }

    public Map<String, ClientAccount> getClients() {
        return clients;
    }

    public KeyPair getKp() {
        return kp;
    }

    public void addAuctions(String auctionId, Auction auction) {
        this.auctions.put(auctionId, auction);
    }

    public void addItems(String itemId, AuctionItem item) {
        this.items.put(itemId, item);
    }

    public void addClients(String clientId, ClientAccount client) {
        this.clients.put(clientId, client);
    }
}
