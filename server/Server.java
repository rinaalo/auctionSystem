import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;

public class Server implements AuctionService {
    // auction id, auction
    private Map<Integer, Auction> auctions;
    // item id, item
    private Map<Integer, AuctionItem> items;
    // client id, client
    private Map<String, Client> clients;

    // TODO temporary ids
    private int auctionId;
    private int itemId;

    public Server() {
        super();
        auctions = new Hashtable<>();
        items = new Hashtable<>();
        clients = new Hashtable<>();
        auctionId = 0;
        itemId = 0;
    }

    @Override
    public AuctionItem getSpec(int itemId, String clientId) throws RemoteException {
        return items.get(itemId);
    }

    @Override
    public void addItem(AuctionItem item) throws RemoteException {
        items.put(item.getItemId(), item);
        System.err.println("item has been added.");
    }

    @Override
    public int generateItemId(String clientId) throws RemoteException {
        return ++itemId;
    }

    @Override
    public Boolean addClient(String name, String email, String password, ClientType type) throws RemoteException {
        if (clients.containsKey(name)) {
            return false;
        }
        Client client = new Client(name, email, password, type);
        clients.put(name, client);
        System.err.println("client " + name + " has been added to the system.");
        return true;
    }

    @Override
    public String createForwardAuction(String clientId) throws RemoteException {
        auctionId = generateAuctionId();
        Auction newAuction = new ForwardAuction(auctionId, clientId);
        auctions.put(auctionId, newAuction);
        System.err.println("Forward Auction has been created.");
        return "Forward Auction has been created.\nAuction ID: " + auctionId + "\n";
    }

    @Override
    public String createReverseAuction(String clientId) throws RemoteException {
        auctionId = generateAuctionId();
        Auction newAuction = new ReverseAuction(auctionId, clientId);
        auctions.put(auctionId, newAuction);
        System.err.println("Reverse Auction has been created.");
        return "Reverse Auction has been created.\nAuction ID: " + auctionId + "\n";
    }

    @Override
    public String createDoubleAuction(String clientId) throws RemoteException {
        auctionId = generateAuctionId();
        Auction newAuction = new DoubleAuction(auctionId, clientId);
        auctions.put(auctionId, newAuction);
        System.err.println("Double Auction has been created.");
        return "Double Auction has been created.\nAuction ID: " + auctionId + "\n";
    }

    public int generateAuctionId() {
        return ++auctionId;
    }

    @Override
    public String closeAuction(int auctionId, String clientId) throws RemoteException {
        // FAIL
        if (!auctions.containsKey(auctionId)) {
            return "Auction does not exist.\n";
        }
        Auction auction = auctions.get(auctionId);
        if (auction.getOngoing() == false) {
            return "This auction is already closed\n";
        }
        if (!auction.getCreatorId().equals(clientId)) {
            return "This auction does not belong to you\n";
        }
        // SUCCESS
        return auction.closeAuction();
    }

    @Override
    public String itemDetails(int itemId, String clientId) throws RemoteException {
        if (!items.containsKey(itemId)) {
            return "Item " + itemId + " does not exist.\n";
        }
        AuctionItem item = items.get(itemId);
        return item.printItemDetails();
    }

    @Override
    public String bid(String clientId, int auctionId, int offer) throws RemoteException {
        if (!auctions.containsKey(auctionId)) {
            return "Auction " + auctionId + " does not exist.\n";
        }
        Auction auction = auctions.get(auctionId);
        if (auction.noItemsInAuction()) {
            return "No available items in auction " + auctionId + ".\n";
        }
        if (auction.getOngoing() == false) {
            return "Auction " + auctionId + " is closed.\n";
        }
        return auction.bid(offer, clients.get(clientId));
    }

    @Override
    public String addItemToAuction(int itemId, int auctionId, int reservedPrice, int startingPrice, String clientId) throws RemoteException {
        if (!items.containsKey(itemId)) {
            return "Item " + itemId + " does not exist\n";
        }
        if (!auctions.containsKey(auctionId)) {
            return "Auction " + auctionId + " does not exist\n";
        }
        Auction auction = auctions.get(auctionId);
        AuctionItem item = items.get(itemId);
        if (auctions.get(auctionId).getOngoing() == false) {
            return "Auction " + auctionId + " has been closed.\n";
        }
        if (item.getInAuction()) {
            return "Item " + itemId + " is already in an auction.\n";
        }
        if (startingPrice < reservedPrice) {
            return "Starting price has to be greater than the reserved price.\n";
        }
        item.setReservedPrice(reservedPrice);
        item.setStartingPrice(startingPrice);
        item.setInAuction(true);
        return auction.addItemToAuction(item, clientId);
    }

    @Override
    public String getAuctions(String clientId) throws RemoteException {
        if (auctions.isEmpty()) {
            return "No available auctions.\n";
        }
        String ret = "\nAll auctions:\n\n";
        for (Integer auctionId : auctions.keySet()) {
            Auction auction = auctions.get(auctionId);
            ret += auction.printAuction();
        }
        return ret;
    }

    @Override
    public String getItemsInAuction(int auctionId, String clientId) throws RemoteException {
        if (!auctions.containsKey(auctionId)) {
            return "Auction " + auctionId + " does not exist.\n";
        }
        Auction auction = auctions.get(auctionId);
        if (auction.noItemsInAuction()) {
            return "No items in auction " + auctionId + ".\n";
        }
        if (!auction.getOngoing()) {
            return auction.getWinnerDetails();
        }
        return auction.printItemsInAuction();
    }

    public static void main(String[] args) {
        try {
            Server s = new Server();
            String name = "myserver";
            AuctionService stub = (AuctionService) UnicastRemoteObject.exportObject(s, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }
}
