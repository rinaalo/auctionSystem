import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.List;

import org.jgroups.JChannel;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;


public class ServerReplica implements AuctionService {
    
    private DataState state = new DataState();

    private JChannel channel;
    private RpcDispatcher dispatcher;

    public ServerReplica() {
        super();
        try {
            channel = new JChannel();
            channel.connect("AuctionCluster");
            dispatcher = new RpcDispatcher(channel, this);
            DataState s = dispatcher.callRemoteMethod(
                channel.getView().getCoord(),
                new MethodCall("getState", new Object[]{}, new Class[]{}),
                new RequestOptions(ResponseMode.GET_FIRST, 5000, true)
                );
            if (s != null) {
                setState(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setState(DataState state) {
        System.out.println("clients: " + state.getClients().keySet());
        System.out.println("items: " + state.getItems().keySet());
        System.out.println("auctions: " + state.getAuctions().keySet());
        this.state = state;
    }

    public DataState getState() {
        System.out.println("clients: " + state.getClients().keySet());
        System.out.println("items: " + state.getItems().keySet());
        System.out.println("auctions: " + state.getAuctions().keySet());
        return this.state;
    }

    @Override
    public Boolean addClient(String name, String email, String password) throws RemoteException {
        if (state.getClients().containsKey(name)) {
            return false;
        }
        ClientAccount client = new ClientAccount(name, email, password);
        state.addClients(name, client);
        System.out.println("Client " + name + " has been added to the system.");
        return true;
    }

    @Override
    public PublicKey verifyClient(String name, String password) throws RemoteException {
        if (!state.getClients().containsKey(name)) {
            return null;
        } else if (!state.getClients().get(name).getPassword().equals(password)) {
            return null;
        } else {
            return state.getKeyPair().getPublic();
        }
    }

    @Override
    public ServerResponse putItem(String itemTitle, Boolean used, String description, String clientId) throws RemoteException {
        String itemId = generateItemId();
        if (itemId == null) {
            return new ServerResponse("Can not create item.\nItem ID: " + itemId + "\n", state.getKeyPair().getPrivate());
        }
        AuctionItem newItem = new AuctionItem(itemId, itemTitle, used, description);
        state.addItems(itemId, newItem);
        state.getClients().get(clientId).addItem(newItem);
        newItem.setSeller(clientId);
        System.out.println("Item " + itemId + " has been added to the system.");
        return new ServerResponse("Item has been added to the system. Item details:\n" + itemDetails(itemId, clientId), state.getKeyPair().getPrivate());
    }
    
    public String generateItemId() {
        String itemId = Integer.toString(state.getItemIdCounter());
        return itemId;

        /*Boolean unique = false;
        while (!unique) {
            String str = java.util.UUID.randomUUID().toString();
            String itemId = str.substring(0, 4);
            if (state.getItems().isEmpty()) {
                return itemId;
            }
            if (state.getItems().containsKey(itemId)) {
                continue;
            } else {
                return itemId;
            }
        }
        System.err.println("Encountered error while creating itemID.");
        return null;*/
    }

    @Override
    public ServerResponse showClientsBelongings(String clientId) throws RemoteException {   
        return new ServerResponse(showClientsItems(clientId) + "\n" + showClientsAuctions(clientId), state.getKeyPair().getPrivate());
    }

    public String showClientsItems(String clientId) {
        List<AuctionItem> clientsItems = state.getClients().get(clientId).getItems();
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
        List<Auction> clientsAuctions = state.getClients().get(clientId).getAuctions();
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
            return new ServerResponse("Can not create action.\nAuction ID: " + auctionId + "\n", state.getKeyPair().getPrivate());
        }
        switch (auctionType) {
            case FORWARD:
                Auction forwardAuction = new ForwardAuction(auctionId, clientId, title);
                state.addAuctions(auctionId, forwardAuction);
                state.getClients().get(clientId).addAuction(forwardAuction);
                break;
            case REVERSE:
                Auction reverseAuction = new ReverseAuction(auctionId, clientId, title);
                state.addAuctions(auctionId, reverseAuction);
                state.getClients().get(clientId).addAuction(reverseAuction);
                break;
            case DOUBLE:
                Auction doubleAuction = new ReverseAuction(auctionId, clientId, title);
                state.addAuctions(auctionId, doubleAuction);
                state.getClients().get(clientId).addAuction(doubleAuction);
                break;
            default:
                return new ServerResponse("Can not create action.\nAuction ID: " + auctionId + "\n", state.getKeyPair().getPrivate());
        }
        System.out.println(auctionType.toString() + " Auction " + auctionId + " has been created by " + clientId);
        return new ServerResponse(auctionType.toString() + " Auction has been created.\nAuction ID: " + auctionId + "\n", state.getKeyPair().getPrivate());
    }

    public String generateAuctionId() {
        String auctionId = Integer.toString(state.getAuctionIdCounter());
        return auctionId;
        /*Boolean unique = false;
        while (!unique) {
            String str = java.util.UUID.randomUUID().toString();
            String auctionId = str.substring(0, 4);
            if (state.getAuctions().isEmpty()) {
                return auctionId;
            }
            if (state.getAuctions().containsKey(auctionId)) {
                continue;
            } else {
                return auctionId;
            }
        }
        System.err.println("Encountered error while creating itemID.");
        return null;*/
    }

    @Override
    public ServerResponse closeAuction(String auctionId, String clientId) throws RemoteException {
        String ret;
        if (!state.getAuctions().containsKey(auctionId)) {
            ret = "Auction does not exist.\n";
            return new ServerResponse(ret, state.getKeyPair().getPrivate());
        }
        Auction auction = state.getAuctions().get(auctionId);
        if (auction.getOngoing() == false) {
            ret = "This auction is already closed.\n";
        }
        else if (!auction.getCreatorId().equals(clientId)) {
            ret = "This auction does not belong to you.\n";
        }
        else ret = auction.closeAuction();
        return new ServerResponse(ret, state.getKeyPair().getPrivate());
    }

    public String itemDetails(String itemId, String clientId) {
        if (!state.getItems().containsKey(itemId)) {
            return "Item " + itemId + " does not exist.\n";
        }
        AuctionItem item = state.getItems().get(itemId);
        return item.printItemDetails();
    }

    @Override
    public ServerResponse bid(String clientId, String auctionId, int offer) throws RemoteException {
        String ret;
        if (!state.getAuctions().containsKey(auctionId)) {
            ret = "Auction " + auctionId + " does not exist.\n";
            return new ServerResponse(ret, state.getKeyPair().getPrivate());
        }
        Auction auction = state.getAuctions().get(auctionId);
        if (auction.noItemsInAuction()) {
            ret = "No available items in auction " + auctionId + ".\n";
        }
        else if (auction.getOngoing() == false) {
            ret = "Auction " + auctionId + " is closed.\n";
        }
        else ret = auction.bid(offer, state.getClients().get(clientId));
        return new ServerResponse(ret, state.getKeyPair().getPrivate());
    }

    @Override
    public ServerResponse addItemToAuction(String itemId, String auctionId, int reservedPrice, int startingPrice, String clientId) throws RemoteException {
        // FAILED
        String ret;
        if (!state.getItems().containsKey(itemId)) {
            ret = "Item " + itemId + " does not exist\n";
            return new ServerResponse(ret, state.getKeyPair().getPrivate());
        }
        if (!state.getAuctions().containsKey(auctionId)) {
            ret = "Auction " + auctionId + " does not exist\n";
            return new ServerResponse(ret, state.getKeyPair().getPrivate());
        }
        Auction auction = state.getAuctions().get(auctionId);
        AuctionItem item = state.getItems().get(itemId);
        if (state.getAuctions().get(auctionId).getOngoing() == false) {
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
        // SUCCESS
        else {
            System.out.println("Item " + itemId + " is added to auction " + auctionId);
            item.setReservedPrice(reservedPrice);
            item.setStartingPrice(startingPrice);
            item.setInAuction(true);
            ret = auction.addItemToAuction(item, clientId);
        }
        return new ServerResponse(ret, state.getKeyPair().getPrivate());
    }

    @Override
    public ServerResponse getAuctions() throws RemoteException {
        if (state.getAuctions().isEmpty()) {
            return new ServerResponse("No available auctions.\n", state.getKeyPair().getPrivate());
        }
        String ret = "\nAll auctions:\n\n";
        for (String auctionId : state.getAuctions().keySet()) {
            Auction auction = state.getAuctions().get(auctionId);
            ret += auction.printAuction();
        }
        return new ServerResponse(ret, state.getKeyPair().getPrivate());
    }

    @Override
    public ServerResponse getItemsInAuction(String auctionId) throws RemoteException {
        if (!state.getAuctions().containsKey(auctionId)) {
            return new ServerResponse("Auction " + auctionId + " does not exist.\n", state.getKeyPair().getPrivate()); 
        }
        String ret = "";
        Auction auction = state.getAuctions().get(auctionId);
        if (auction.noItemsInAuction()) {
            ret = "No items in auction " + auctionId + ".\n";
        }
        if (auction.getIsSuccess()) {
            ret = auction.getWinnerDetails();
        }
        else ret = auction.printItemsInAuction();
        return new ServerResponse(ret, state.getKeyPair().getPrivate());
    }

    public static void main(String[] args) {
        new ServerReplica();
    }
}
