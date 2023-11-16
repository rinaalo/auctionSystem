import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.LinkedList;
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
        if (items.get(itemId) == null) {
            return null;
        }
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
        if(!auctions.containsKey(auctionId)) {
            return "Auction does not exist.\n";
        }
        // if auction has been previously closed.
        if (auctions.get(auctionId).getOngoing() == false) {
            return "This auction is already closed\n";
        }
        Bid bid = getHighestBid(auctionId);
        int offer = bid.getOffer();
        int clientId = bid.getClientId();
        int itemId = bid.getItemId();
        auctions.get(auctionId).setOngoing(false);

        // if no one bid.
        if (!clients.containsKey(clientId)) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }
        String name = clients.get(clientId).getName();
        String email = clients.get(clientId).getEmail();

        // if offer is less than reserved price.
        if (offer < items.get(itemId).getReservedPrice()) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }
        return "Auction is closed." + "\nWinner: " + name + "\nEmail: " + email + "\nItem ID: " + itemId +"\nBid: " + offer;
    }
    
    @Override
    public String itemDetails(int itemId, int clientId) throws RemoteException {
        if (!items.containsKey(itemId)) {
            return "Item does not exist.\n";
        }
        return (
        "Item id: " + getSpec(itemId, clientId).getItemId() + "\n" + 
        "Item title: " + getSpec(itemId, clientId).getItemTitle() + "\n" + 
        "Used: " + getSpec(itemId, clientId).getCondition() + "\n" +
        "Item description: " + getSpec(itemId, clientId).getItemDescription() + "\n"
        );
    }

    @Override
    public String bid(int clientId, int auctionId, int itemId, int bid) throws RemoteException {
        if(!items.containsKey(itemId)) {
            return "Item does not exist\n";
        }
        if(!auctions.containsKey(auctionId)) {
            return "Auction does not exist\n";
        }
        if(auctions.get(auctionId).getOngoing() == false) {
            return "This auction is closed.\n";
        }
        auctions.get(auctionId).getItemBids().get(itemId).add(new Bid(clientId, itemId, bid));
        return "You have bid the amount of " + bid + " for item " + itemId + " in auction " + auctionId + "\n";
    }
    
    public Bid getHighestBid(int auctionId) {
        int highestOffer = 0;
        Bid maxBid = new Bid(-1, -1, 0);
        for (Integer itemId : auctions.get(auctionId).getItemBids().keySet()) {
            for (Bid bid : auctions.get(auctionId).getItemBids().get(itemId)) {
                if(highestOffer < bid.getOffer()) {
                    highestOffer = bid.getOffer();
                    maxBid = bid;
                }
            }
        }
        return maxBid;
    }
    
    @Override
    public String getAuctions(int clientId) throws RemoteException {
        String ret = "";
        for (Integer auctionId : auctions.keySet()) {
            ret += "auction ID: " + auctionId + 
                    "\nhighest bid: " + getHighestBid(auctionId).getOffer() + 
                    "\ntype: " + auctions.get(auctionId).getAuctionType() + 
                    "\nongoing: " + auctions.get(auctionId).getOngoing() + "\n";
        }
        return ret;
    }

    @Override
    public String addItemToAuction(int itemId, int reservedPrice, int auctionId, int clientId) throws RemoteException {
        if(!items.containsKey(itemId)) {
            return "Item does not exist\n";
        }
        if(!auctions.containsKey(auctionId)) {
            return "Auction does not exist\n";
        }
        if(auctions.get(auctionId).getOngoing() == false) {
            return "This auction has been closed.\n";
        }
        items.get(itemId).setReservedPrice(reservedPrice);
        return auctions.get(auctionId).addItemToAuction(itemId, auctionId, auctions);
    }
    
    @Override
    public String getItemsInAuction(int auctionId, int clientId) throws RemoteException {
        if (auctions.get(auctionId).getItemBids().isEmpty()) {
            return "No available items in auction.";
        }
        String ret = "------------------------------------\n";
        ret += "Available items in the auction: \n\n";
        for (Integer itemId : auctions.get(auctionId).getItemBids().keySet()) {
            ret += itemDetails(itemId, clientId) + "\n";
        }
        ret += "------------------------------------";
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
