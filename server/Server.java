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
    public Boolean addClient(String name, String email, String password) throws RemoteException {
        if (clients.containsKey(name)) {
            return false;
        }
        ClientAccount client = new ClientAccount(name, email, password);
        clients.put(name, client);
        System.out.println("Client " + name + " has been added to the system.");
        return true;
    }

    @Override
    public PublicKey verifyClient(String name, String password) throws RemoteException {
        if (!clients.containsKey(name)) {
            return null;
        } else if (!clients.get(name).getPassword().equals(password)) {
            return null;
        } else {
            return kp.getPublic();
        }
    }

    @Override
    public ServerResponse putItem(String itemTitle, Boolean used, String description, String clientId) throws RemoteException {
        String itemId = generateItemId();
        if (itemId == null) {
            return new ServerResponse("Can not create item.\nItem ID: " + itemId + "\n", kp.getPrivate());
        }
        AuctionItem newItem = new AuctionItem(itemId, itemTitle, used, description);
        items.put(itemId, newItem);
        clients.get(clientId).addItem(newItem);
        newItem.setSeller(clientId);
        System.out.println("Item " + itemId + " has been added to the system.");
        return new ServerResponse("Item has been added to the system. Item details:\n" + itemDetails(itemId, clientId), kp.getPrivate());
    }
    
    public String generateItemId() {
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
        return null;
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
    public ServerResponse createAuction(String clientId, String title, AuctionType auctionType) {
        String auctionId = generateAuctionId();
        if (auctionId == null) {
            System.err.println("Can not create action.\nAuction ID: " + auctionId);
            return new ServerResponse("Can not create action.\nAuction ID: " + auctionId + "\n", kp.getPrivate());
        }
        switch (auctionType) {
            case FORWARD:
                Auction forwardAuction = new ForwardAuction(auctionId, clientId, title);
                auctions.put(auctionId, forwardAuction);
                clients.get(clientId).addAuction(forwardAuction);
                break;
            case REVERSE:
                Auction reverseAuction = new ReverseAuction(auctionId, clientId, title);
                auctions.put(auctionId, reverseAuction);
                clients.get(clientId).addAuction(reverseAuction);
                break;
            case DOUBLE:
                Auction doubleAuction = new ReverseAuction(auctionId, clientId, title);
                auctions.put(auctionId, doubleAuction);
                clients.get(clientId).addAuction(doubleAuction);
                break;
            default:
                return new ServerResponse("Can not create action.\nAuction ID: " + auctionId + "\n", kp.getPrivate());
        }
        System.out.println(auctionType.toString() + " Auction " + auctionId + " has been created by " + clientId);
        return new ServerResponse(auctionType.toString() + " Auction has been created.\nAuction ID: " + auctionId + "\n", kp.getPrivate());
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
        return null;
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
        // FAILED
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
        else if (item.getIsSold()) {
            ret = "Item " + itemId + " has already been sold.\n";
        }
        else if(!item.getSeller().equals(clientId)) {
            ret = "Item " + itemId + " is not yours.\n";
        }
        else if(!item.getItemTitle().equals(auction.getTitle())) {
            ret = "Item " + item.getItemTitle() + " is not in the same category as auction " + auction.getTitle() + "\n";
        }
        else if (startingPrice < reservedPrice) {
            ret = "Starting price has to be greater than the reserved price.\n";
        } 
        else {
            // SUCCESS
            System.out.println("Item " + itemId + " is added to auction " + auctionId);
            item.setReservedPrice(reservedPrice);
            item.setStartingPrice(startingPrice);
            item.setInAuction(true);
            ret = auction.addItemToAuction(item, clientId);
        }
        return new ServerResponse(ret, kp.getPrivate());
    }

    @Override
    public ServerResponse getAuctions() throws RemoteException {
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
    public ServerResponse getItemsInAuction(String auctionId) throws RemoteException {
        if (!auctions.containsKey(auctionId)) {
            return new ServerResponse("Auction " + auctionId + " does not exist.\n", kp.getPrivate()); 
        }
        String ret = "";
        Auction auction = auctions.get(auctionId);
        if (auction.noItemsInAuction()) {
            ret = "No items in auction " + auctionId + ".\n";
        }
        if (auction.getIsSuccess()) {
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
