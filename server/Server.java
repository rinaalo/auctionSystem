import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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
    // keys
    private KeyPair kp;

    public Server() {
        super();
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

    @Override
    public ServerResponse addItem(AuctionItem item, String clientId) throws RemoteException {
        items.put(item.getItemId(), item);
        clients.get(clientId).addItem(item);
        System.err.println("item has been added.");
        return new ServerResponse("Item has been added. Item details:\n" + itemDetails(item.getItemId(), clientId), kp.getPrivate());
    }
    
    @Override
    public ServerResponse generateItemId() throws RemoteException {
        Boolean unique = false;
        while (!unique) {
            String str = java.util.UUID.randomUUID().toString();
            String itemId = str.substring(0, 4);
            if (items.isEmpty()) {
                return new ServerResponse(itemId, kp.getPrivate());
            }
            if (items.containsKey(itemId)) {
                continue;
            } else {
                return new ServerResponse(itemId, kp.getPrivate());
            }
        }
        System.err.println("Encountered error while creating itemID.");
        return new ServerResponse("Encountered error while creating itemID.\n", kp.getPrivate());
    }

    @Override
    public ServerResponse showClientsBelongings(String clientId) throws RemoteException {   
        return new ServerResponse(showClientsItems(clientId) + "\n" + showClientsAuctions(clientId), kp.getPrivate());
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
    public PublicKey verifyClient(String name, String password) throws RemoteException {
        String ret;
        if (!clients.containsKey(name)) {
            ret = "\nThis username does not exist.\n";
            return null;
        } else if (!clients.get(name).getPassword().equals(password)) {
            ret = "\nIncorrect password.\n";
            return null;
        } else {
            ret = "SUCCESS";
            return kp.getPublic();
        }
    }

    @Override
    public ServerResponse createForwardAuction(String clientId, String title) throws RemoteException {
        String auctionId = generateAuctionId();
        Auction newAuction = new ForwardAuction(auctionId, clientId, title);
        auctions.put(auctionId, newAuction);
        clients.get(clientId).addAuction(newAuction);
        System.err.println("Forward Auction has been created.");
        return new ServerResponse("Forward Auction has been created.\nAuction ID: " + auctionId + "\n", kp.getPrivate());
    }

    @Override
    public ServerResponse createReverseAuction(String clientId, String title) throws RemoteException {
        String auctionId = generateAuctionId();
        Auction newAuction = new ReverseAuction(auctionId, clientId, title);
        auctions.put(auctionId, newAuction);
        clients.get(clientId).addAuction(newAuction);
        System.err.println("Reverse Auction has been created.");
        return new ServerResponse("Reverse Auction has been created.\nAuction ID: " + auctionId + "\n", kp.getPrivate());
    }

    @Override
    public ServerResponse createDoubleAuction(String clientId, String title) throws RemoteException {
        String auctionId = generateAuctionId();
        Auction newAuction = new DoubleAuction(auctionId, clientId, title);
        auctions.put(auctionId, newAuction);
        clients.get(clientId).addAuction(newAuction);
        System.err.println("Double Auction has been created.");
        return new ServerResponse("Double Auction has been created.\nAuction ID: " + auctionId + "\n", kp.getPrivate());
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
    public ServerResponse closeAuction(String auctionId, String clientId) throws RemoteException {
        String ret;
        if (!auctions.containsKey(auctionId)) {
            ret = "Auction does not exist.\n";
            return new ServerResponse(ret, kp.getPrivate());
        }
        Auction auction = auctions.get(auctionId);
        if (auction.getOngoing() == false) {
            ret = "This auction is already closed.\n";
        }
        else if (!auction.getCreatorId().equals(clientId)) {
            ret = "This auction does not belong to you.\n";
        }
        else ret = auction.closeAuction();
        return new ServerResponse(ret, kp.getPrivate());
    }

    public String itemDetails(String itemId, String clientId) {
        if (!items.containsKey(itemId)) {
            return "Item " + itemId + " does not exist.\n";
        }
        AuctionItem item = items.get(itemId);
        return item.printItemDetails();
    }

    @Override
    public ServerResponse bid(String clientId, String auctionId, int offer) throws RemoteException {
        String ret;
        if (!auctions.containsKey(auctionId)) {
            ret = "Auction " + auctionId + " does not exist.\n";
            return new ServerResponse(ret, kp.getPrivate());
        }
        Auction auction = auctions.get(auctionId);
        if (auction.noItemsInAuction()) {
            ret = "No available items in auction " + auctionId + ".\n";
        }
        else if (auction.getOngoing() == false) {
            ret = "Auction " + auctionId + " is closed.\n";
        }
        else ret = auction.bid(offer, clients.get(clientId));
        return new ServerResponse(ret, kp.getPrivate());
    }

    @Override
    public ServerResponse addItemToAuction(String itemId, String auctionId, int reservedPrice, int startingPrice, String clientId) throws RemoteException {
        String ret;
        if (!items.containsKey(itemId)) {
            ret = "Item " + itemId + " does not exist\n";
            return new ServerResponse(ret, kp.getPrivate());
        }
        if (!auctions.containsKey(auctionId)) {
            ret = "Auction " + auctionId + " does not exist\n";
            return new ServerResponse(ret, kp.getPrivate());
        }
        Auction auction = auctions.get(auctionId);
        AuctionItem item = items.get(itemId);
        if (auctions.get(auctionId).getOngoing() == false) {
            ret = "Auction " + auctionId + " has been closed.\n";
        }
        else if (item.getInAuction()) {
            ret = "Item " + itemId + " is already in an auction.\n";
        }
        else if (startingPrice < reservedPrice) {
            ret = "Starting price has to be greater than the reserved price.\n";
        } 
        else {
            item.setReservedPrice(reservedPrice);
            item.setStartingPrice(startingPrice);
            item.setInAuction(true);
            ret = auction.addItemToAuction(item, clientId);
        }
        return new ServerResponse(ret, kp.getPrivate());
    }

    @Override
    public ServerResponse getAuctions(String clientId) throws RemoteException {
        if (auctions.isEmpty()) {
            return new ServerResponse("No available auctions.\n", kp.getPrivate());
        }
        String ret = "\nAll auctions:\n\n";
        for (String auctionId : auctions.keySet()) {
            Auction auction = auctions.get(auctionId);
            ret += auction.printAuction();
        }
        return new ServerResponse(ret, kp.getPrivate());
    }

    @Override
    public ServerResponse getItemsInAuction(String auctionId, String clientId) throws RemoteException {
        if (!auctions.containsKey(auctionId)) {
            return new ServerResponse("Auction " + auctionId + " does not exist.\n", kp.getPrivate()); 
        }
        String ret = "";
        Auction auction = auctions.get(auctionId);
        if (auction.noItemsInAuction()) {
            ret = "No items in auction " + auctionId + ".\n";
        }
        if (!auction.getOngoing()) {
            ret = auction.getWinnerDetails();
        }
        else ret = auction.printItemsInAuction();
        return new ServerResponse(ret, kp.getPrivate());
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
