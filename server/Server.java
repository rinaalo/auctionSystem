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
    private Map<Integer, Client> clients;

    // TODO temporary ids
    private int auctionId;
    private int itemId;
    private int clientId;

    public Server() {
        super();
        auctions = new Hashtable<>();
        items = new Hashtable<>();
        clients = new Hashtable<>();
        auctionId = 0;
        itemId = 0;
        clientId = 0;
    }

    @Override
    public AuctionItem getSpec(int itemId, int clientId) throws RemoteException {
        return items.get(itemId);
    }

    @Override
    public void addItem(AuctionItem item) throws RemoteException {
        items.put(item.getItemId(), item);
        System.err.println("item has been added.");
    }

    @Override
    public int generateItemId(int clientId) throws RemoteException {
        return ++itemId;
    }

    @Override
    public int addClient(String name, String email, ClientType type) throws RemoteException {
        clientId = generateClientId();
        Client client = new Client(name, email, clientId, type);
        clients.put(clientId, client);
        System.err.println("client has been added to the system.");
        return clientId;
    }

    public int generateClientId() {
        return ++clientId;
    }

    @Override
    public String createForwardAuction() throws RemoteException {
        auctionId = generateAuctionId();
        Auction newAuction = new ForwardAuction(auctionId);
        auctions.put(auctionId, newAuction);
        System.err.println("Forward Auction has been created.");
        return "Forward Auction has been created.\nAuction ID: " + auctionId + "\n";
    }

    @Override
    public String createReverseAuction() throws RemoteException {
        auctionId = generateAuctionId();
        Auction newAuction = new ReverseAuction(auctionId);
        auctions.put(auctionId, newAuction);
        System.err.println("Reverse Auction has been created.");
        return "Reverse Auction has been created.\nAuction ID: " + auctionId + "\n";
    }

    @Override
    public String createDoubleAuction() throws RemoteException {
        auctionId = generateAuctionId();
        Auction newAuction = new DoubleAuction(auctionId);
        auctions.put(auctionId, newAuction);
        System.err.println("Double Auction has been created.");
        return "Double Auction has been created.\nAuction ID: " + auctionId + "\n";
    }

    public int generateAuctionId() {
        return ++auctionId;
    }

    @Override
    public String closeAuction(int auctionId) throws RemoteException {
        // FAIL
        if (!auctions.containsKey(auctionId)) {
            return "Auction does not exist.\n";
        }
        Auction auction = auctions.get(auctionId);
        if (auction.getOngoing() == false) {
            return "This auction is already closed\n";
        }
        // SUCCESS
        auction.setOngoing(false);
        // if no one bid.
        if (auction.getAuctionBids().isEmpty()) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }
        auction.determineWinner(auctionId, auctions);
        /*
         * String name = clients.get(clientId).getName();
         * String email = clients.get(clientId).getEmail();
         * 
         * // if offer is less than reserved price.
         * if (offer < items.get(itemId).getReservedPrice()) {
         * return "Auction is closed.\nThe reserve has not been reached.\n";
         * }
         * return "Auction is closed." +
         * "\nWinner: " + name +
         * "\nEmail: " + email +
         * "\nItem ID: " + itemId +
         * "\nBid: " + offer;
         */
        return null;
    }

    @Override
    public String itemDetails(int itemId, int clientId) throws RemoteException {
        if (!items.containsKey(itemId)) {
            return "Item " + itemId + " does not exist.\n";
        }
        AuctionItem item = items.get(itemId);
        return item.printItemDetails();
    }

    @Override
    public String bid(int clientId, int auctionId, int bid) throws RemoteException {
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
        // TODO: check if it is less than the MINIMUM starting price out of all items in
        // the auction
        /*
         * if (bid < items.get(itemId).getStartingPrice()) {
         * return "Your bid has to be greater than the starting price.\n";
         * }
         */
        auction.getAuctionBids().add(new Bid(clientId, bid));
        return "You have bid the amount of " + bid + " in auction " + auctionId + "\n";
    }

    @Override
    public String addItemToAuction(int itemId, int auctionId, int reservedPrice, int startingPrice, int clientId)
            throws RemoteException {
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
        if (startingPrice >= reservedPrice) {
            return "Starting price has to be less than the reserved price.\n";
        }
        item.setReservedPrice(reservedPrice);
        item.setStartingPrice(startingPrice);
        item.setInAuction(true);
        return auction.addItemToAuction(item, auctionId, auctions);
    }

    @Override
    public String getAuctions(int clientId) throws RemoteException {
        if (auctions.isEmpty()) {
            return "No available auctions.\n";
        }
        String ret = "\nAll auctions:\n\n";
        for (Integer auctionId : auctions.keySet()) {
            Auction auction = auctions.get(auctionId);
            String highestBid = "";
            if (auction.getHighestBid() == null) {
                highestBid = "No bid has been made yet.";
            } else {
                highestBid += auction.getHighestBid().getOffer();
            }
            ret += "auction ID: " + auctionId +
                    "\nhighest bid: " + highestBid +
                    "\ntype: " + auction.getAuctionType() +
                    "\nongoing: " + auction.getOngoing() + "\n\n";
        }
        return ret;
    }

    @Override
    public String getItemsInAuction(int auctionId, int clientId) throws RemoteException {
        if (!auctions.containsKey(auctionId)) {
            return "Auction " + auctionId + " does not exist.\n";
        }
        Auction auction = auctions.get(auctionId);
        if (auction.noItemsInAuction()) {
            return "No items in auction " + auctionId + ".\n";
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
