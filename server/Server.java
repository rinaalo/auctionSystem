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

    // TODO temporary ids 
    private int auctionId;
    private int itemId;

    public Server() {
        super();
        auctions = new Hashtable<>();
        items = new Hashtable<>();
        auctionId = 0;
        itemId = 0;
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
    }

    @Override
    public int generateItemId(int clientId) throws RemoteException {
        return ++itemId;
    }

    @Override
    public int createAuction(String auctionType) throws RemoteException {
        Auction newAuction = new Auction(auctionType);
        auctionId++;
        auctions.put(auctionId, newAuction);
        return auctionId;
    }
    
    @Override
    public void closeAuction(int auctionId) throws RemoteException {
        if (auctions.get(auctionId).getOngoing() == false) {
            System.out.println("This auction is already closed\n");
            return;
        }
        // return winner
        // item that is sold
        // highest bid of winner
        System.out.println("Winner: " + getHighestBid(auctionId).getClientId());
        auctions.get(auctionId).setOngoing(false);
        //auctions.remove(auctionId);
    }
    
    @Override
    public String itemDetails(int itemId, int clientId) throws RemoteException {
        return ("ITEM DETAILS" +
        "\nitem id: " + getSpec(itemId, clientId).getItemId() + " " + 
        "\nitem title: " + getSpec(itemId, clientId).getItemTitle() + " " + 
        "\nused: " + getSpec(itemId, clientId).getCondition() + " " +
        "\nitem description: " + getSpec(itemId, clientId).getItemDescription() + "\n");
    }

    @Override
    public void bid(int clientId, int auctionId, int itemId, int bid) throws RemoteException {
        if(auctions.get(auctionId).getOngoing() == false) {
            System.out.println("This auction has been closed.\n");
            return;
        }
        auctions.get(auctionId).getItemBids().get(itemId).add(new Bid(clientId, bid));
    }
    
    public Bid getHighestBid(int auctionId) {
        int highestOffer = 0;
        Bid maxBid = new Bid(-1, 0);
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
            ret += "auction ID: " + auctionId + " highest bid: " + getHighestBid(auctionId).getOffer() + " ongoing: " + auctions.get(auctionId).getOngoing() + "\n";
        }
        return ret;
    }

    @Override
    public void addItemToAuction(int itemId, int reservedPrice, int auctionId, int clientId) throws RemoteException {
        if(auctions.get(auctionId).getOngoing() == false) {
            System.out.println("This auction has been closed.\n");
            return;
        }
        items.get(itemId).setReservedPrice(reservedPrice);
        auctions.get(auctionId).getItemBids().put(itemId, new LinkedList<>());
        System.out.println(auctions.get(auctionId).getItemBids());
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
