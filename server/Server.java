import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Server implements AuctionService {
    // auction id, auction
    private Map<String, Auction> auctions;
    // item id, item
    private Map<String, AuctionItem> items;
    // client id, client
    private Map<String, ClientAccount> clients;

    public Server() {
        super();
        auctions = new Hashtable<>();
        items = new Hashtable<>();
        clients = new Hashtable<>();
    }

    /*@Override
    public AuctionItem getSpec(String itemId, String clientId) throws RemoteException {
        return items.get(itemId);
    }*/

    @Override
    public String addItem(AuctionItem item, String clientId) throws RemoteException {
        items.put(item.getItemId(), item);
        clients.get(clientId).addItem(item);
        System.err.println("item has been added.");
        return "Item has been added. Item details:\n" + itemDetails(item.getItemId(), clientId);
    }
    
    @Override
    public String generateItemId() throws RemoteException {
        Boolean unique = false;
        while (!unique) {
            String str = java.util.UUID.randomUUID().toString();
            String itemId = str.substring(0, 4);
            if (items.isEmpty()) {
                return itemId;
            }
            if (items.containsKey(itemId)) {
                continue;
            } else {
                return itemId;
            }
        }
        System.err.println("Encountered error while creating itemID.");
        return "Encountered error while creating itemID.\n";
    }

    @Override
    public String showClientsBelongings(String clientId) throws RemoteException {   
        return showClientsItems(clientId) + "\n" + showClientsAuctions(clientId);
    }

    public String showClientsItems(String clientId) {
        List<AuctionItem> clientsItems = clients.get(clientId).getItems();
        if (clientsItems.isEmpty()) {
            return "You have no items yet.\n";
        }
        String ret = "List of your items:\n\n";
        for (AuctionItem item : clientsItems) {
            ret += itemDetails(item.getItemId(), clientId);
        }
        return ret;
    }

    public String showClientsAuctions(String clientId) {
        List<Auction> clientsAuctions = clients.get(clientId).getAuctions();
        if (clientsAuctions.isEmpty()) {
            return "You have no auctions yet.\n";
        }
        String ret = "List of your auctions:\n\n";
        for (Auction auction : clientsAuctions) {
            ret += auction.printAuction();
        }
        return ret;
    }

    @Override
    public Boolean addClient(String name, String email, String password) throws RemoteException {
        if (clients.containsKey(name)) {
            return false;
        }
        ClientAccount client = new ClientAccount(name, email, password);
        clients.put(name, client);
        System.err.println("client " + name + " has been added to the system.");
        return true;
    }

    @Override
    public String createForwardAuction(String clientId) throws RemoteException {
        String auctionId = generateAuctionId();
        Auction newAuction = new ForwardAuction(auctionId, clientId);
        auctions.put(auctionId, newAuction);
        clients.get(clientId).addAuction(newAuction);
        System.err.println("Forward Auction has been created.");
        return "Forward Auction has been created.\nAuction ID: " + auctionId + "\n";
    }

    @Override
    public String createReverseAuction(String clientId) throws RemoteException {
        String auctionId = generateAuctionId();
        Auction newAuction = new ReverseAuction(auctionId, clientId);
        auctions.put(auctionId, newAuction);
        clients.get(clientId).addAuction(newAuction);
        System.err.println("Reverse Auction has been created.");
        return "Reverse Auction has been created.\nAuction ID: " + auctionId + "\n";
    }

    @Override
    public String createDoubleAuction(String clientId) throws RemoteException {
        String auctionId = generateAuctionId();
        Auction newAuction = new DoubleAuction(auctionId, clientId);
        auctions.put(auctionId, newAuction);
        clients.get(clientId).addAuction(newAuction);
        System.err.println("Double Auction has been created.");
        return "Double Auction has been created.\nAuction ID: " + auctionId + "\n";
    }

    public String generateAuctionId() {
        Boolean unique = false;
        while (!unique) {
            String str = java.util.UUID.randomUUID().toString();
            String auctionId = str.substring(0, 4);
            if (auctions.isEmpty()) {
                return auctionId;
            }
            if (auctions.containsKey(auctionId)) {
                continue;
            } else {
                return auctionId;
            }
        }
        System.err.println("Encountered error while creating itemID.");
        return "Encountered error while creating itemID.\n";
    }

    @Override
    public String closeAuction(String auctionId, String clientId) throws RemoteException {
        // FAIL
        if (!auctions.containsKey(auctionId)) {
            return "Auction does not exist.\n";
        }
        Auction auction = auctions.get(auctionId);
        if (auction.getOngoing() == false) {
            return "This auction is already closed.\n";
        }
        if (!auction.getCreatorId().equals(clientId)) {
            return "This auction does not belong to you.\n";
        }
        // SUCCESS
        return auction.closeAuction();
    }

    public String itemDetails(String itemId, String clientId) {
        if (!items.containsKey(itemId)) {
            return "Item " + itemId + " does not exist.\n";
        }
        AuctionItem item = items.get(itemId);
        return item.printItemDetails();
    }

    @Override
    public String bid(String clientId, String auctionId, int offer) throws RemoteException {
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
    public String addItemToAuction(String itemId, String auctionId, int reservedPrice, int startingPrice, String clientId) throws RemoteException {
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
        for (String auctionId : auctions.keySet()) {
            Auction auction = auctions.get(auctionId);
            ret += auction.printAuction();
        }
        return ret;
    }

    @Override
    public String getItemsInAuction(String auctionId, String clientId) throws RemoteException {
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
