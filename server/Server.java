import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Server implements AuctionService {
    private class Auction {
        private String auctionType; // this might be an enum
        // item ids, list of bids
        private Map<Integer, List<Bid>> itemBids;
        private Auction(String auctionType) {
            this.auctionType = auctionType;
            itemBids = new Hashtable<>();
        }
    }
    private class Bid {
        private int clientId;
        private int offer;
        private Bid(int clientId, int bid) {
            this.clientId = clientId;
            this.offer = bid;
        }
    }
    // auction id / auction
    private Map<Integer, Auction> availableAuctions;
    // item id / item
    private Map<Integer, AuctionItem> items;

    // temporary auction id 
    private int tempAuctionId;
    private int tempItemId;

    public Server() {
        super();
        availableAuctions = new Hashtable<>();
        items = new Hashtable<>();
        tempAuctionId = 0;
        tempItemId = 0;
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
        return ++tempItemId;
    }

    @Override
    public int createAuction(String auctionType) throws RemoteException {
        Auction newAuction = new Auction(auctionType);
        tempAuctionId++;
        availableAuctions.put(tempAuctionId, newAuction);
        return tempAuctionId;
    }
    
    @Override
    public void closeAuction(int auctionId) throws RemoteException {
        // return winner
        // item that is sold
        // highest bid of winner
        availableAuctions.remove(auctionId);
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
        //availableAuctions.get(auctionId).bids.add(new Bid(itemId, clientId, bid));
        availableAuctions.get(auctionId).itemBids.get(itemId).add(new Bid(clientId, bid));
    }
    
    public int getHighestBid(int auctionId) {
        int highestBid = 0;
        for (Integer itemId : availableAuctions.get(auctionId).itemBids.keySet()) {
            for (Bid bid : availableAuctions.get(auctionId).itemBids.get(itemId)) {
                if(highestBid < bid.offer) highestBid = bid.offer;
            }
        }
        return highestBid;
    }
    
    @Override
    public String getAuctions(int clientId) throws RemoteException {
        String ret = "";
        for (Integer auctionId : availableAuctions.keySet()) {
            ret += "auction ID: " + auctionId + " highest bid: " + getHighestBid(auctionId) + "\n";
        }
        return ret;
    }

    @Override
    public void addItemToAuction(int itemId, int reservedPrice, int auctionId, int clientId) throws RemoteException {
        items.get(itemId).setReservedPrice(reservedPrice);
        //availableAuctions.get(auctionId).auctionItemIDs.add(itemId);
        availableAuctions.get(auctionId).itemBids.put(itemId, new LinkedList<>());
        System.out.println(availableAuctions.get(auctionId).itemBids);
    }
    
    @Override
    public String getItemsInAuction(int auctionId, int clientId) throws RemoteException {
        if (availableAuctions.get(auctionId).itemBids.isEmpty()) {
            return "No available items in auction.";
        }
        String ret = "------------------------------------\n";
        ret += "Available items in the auction: \n\n";
        for (Integer itemId : availableAuctions.get(auctionId).itemBids.keySet()) {
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
