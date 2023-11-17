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
    public int createForwardAuction() throws RemoteException {
        auctionId = generateAuctionId();
        Auction newAuction = new ForwardAuction(auctionId);
        auctions.put(auctionId, newAuction);
        System.err.println("Forward Auction has been created.");
        return auctionId;
    }

    @Override
    public int createReverseAuction() throws RemoteException {
        auctionId = generateAuctionId();
        Auction newAuction = new ReverseAuction(auctionId);
        auctions.put(auctionId, newAuction);
        System.err.println("Reverse Auction has been created.");
        return auctionId;
    }

    @Override
    public int createDoubleAuction() throws RemoteException {
        auctionId = generateAuctionId();
        Auction newAuction = new DoubleAuction(auctionId);
        auctions.put(auctionId, newAuction);
        System.err.println("Double Auction has been created.");
        return auctionId;
    }

    public int generateAuctionId() {
        return ++auctionId;
    }

    @Override
    public String closeAuction(int auctionId) throws RemoteException {
        if (!auctions.containsKey(auctionId)) {
            return "Auction does not exist.\n";
        }
        Auction auction = auctions.get(auctionId);
        // if auction has been previously closed.
        if (auction.getOngoing() == false) {
            return "This auction is already closed\n";
        }
        /*Bid bid = getHighestBid(auctionId);
        int offer = bid.getOffer();
        int clientId = bid.getClientId();
        int itemId = bid.getItemId();*/
        auction.setOngoing(false);

        // if no one bid.
        if (auction.getAuctionBids().isEmpty()) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }

        auction.determineWinner(auctionId);
        /*String name = clients.get(clientId).getName();
        String email = clients.get(clientId).getEmail();

        // if offer is less than reserved price.
        if (offer < items.get(itemId).getReservedPrice()) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }
        return "Auction is closed." +
                "\nWinner: " + name +
                "\nEmail: " + email +
                "\nItem ID: " + itemId +
                "\nBid: " + offer;*/
        return null;
    }

    @Override
    public String itemDetails(int itemId, int clientId) throws RemoteException {
        if (!items.containsKey(itemId)) {
            return "Item does not exist.\n";
        }
        AuctionItem item = items.get(itemId);
        String startingPrice;
        if (!item.getInAuction()) {
            startingPrice = "";
        } else
            startingPrice = "Starting price: " + Integer.toString(item.getStartingPrice()) + "\n";

        return ("Item id: " + item.getItemId() + "\n" +
                "Item title: " + item.getItemTitle() + "\n" +
                "Used: " + item.getCondition() + "\n" +
                "Item description: " + item.getItemDescription() + "\n" +
                startingPrice);
    }

    @Override
    public String bid(int clientId, int auctionId, int bid) throws RemoteException {
        if (!auctions.containsKey(auctionId)) {
            return "Auction does not exist.\n";
        }
        Auction auction = auctions.get(auctionId);
        if (auction.getAuctionItems().isEmpty()) {
            return "No available items in the auction.\n";
        }
        if (auction.getOngoing() == false) {
            return "This auction is closed.\n";
        }
        //TODO: check if it is less than the MINIMUM starting price out of all items in the auction
        /*if (bid < items.get(itemId).getStartingPrice()) {
            return "Your bid has to be greater than the starting price.\n";
        }*/
        auction.getAuctionBids().add(new Bid(clientId, bid));
        return "You have bid the amount of " + bid + " in auction " + auctionId + "\n";
    }

    public Bid getHighestBid(int auctionId) {
        int highestOffer = 0;
        Bid maxBid = new Bid(-1, 0);
        for (Bid bid : auctions.get(auctionId).getAuctionBids()) {
            if (highestOffer < bid.getOffer()) {
                highestOffer = bid.getOffer();
                maxBid = bid;
            }
        }
        return maxBid;
    }

    @Override
    public String getAuctions(int clientId) throws RemoteException {
        if (auctions.isEmpty()) {
            return "No available auctions.\n";
        }
        String ret = "";
        for (Integer auctionId : auctions.keySet()) {
            ret += "\nauction ID: " + auctionId +
                    "\nhighest bid: " + getHighestBid(auctionId).getOffer() +
                    "\ntype: " + auctions.get(auctionId).getAuctionType() +
                    "\nongoing: " + auctions.get(auctionId).getOngoing() + "\n";
        }
        return ret;
    }

    @Override
    public String addItemToAuction(int itemId, int auctionId, int reservedPrice, int startingPrice, int clientId) throws RemoteException {
        if (!items.containsKey(itemId)) {
            return "Item does not exist\n";
        }
        if (!auctions.containsKey(auctionId)) {
            return "Auction does not exist\n";
        }
        Auction auction = auctions.get(auctionId);
        AuctionItem item = items.get(itemId);
        if (auctions.get(auctionId).getOngoing() == false) {
            return "This auction has been closed.\n";
        }
        if (item.getInAuction()) {
            return "This item is already in an auction.\n";
        }
        item.setReservedPrice(reservedPrice);
        item.setStartingPrice(startingPrice);
        item.setInAuction(true);
        return auction.addItemToAuction(itemId, auctionId, auctions);
    }

    @Override
    public String getItemsInAuction(int auctionId, int clientId) throws RemoteException {
        if (!auctions.containsKey(auctionId)) {
            return "Auction does not exist\n";
        }
        Auction auction = auctions.get(auctionId);
        if (auction.getAuctionItems().isEmpty()) {
            return "No available items in auction.\n";
        }
        String ret = "\nAvailable items in the auction: \n\n";
        for (Integer itemId : auction.getAuctionItems()) {
            ret += itemDetails(itemId, clientId) + "\n";
        }
        return ret;
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
