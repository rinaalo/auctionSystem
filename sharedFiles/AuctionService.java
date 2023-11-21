import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface AuctionService extends Remote {
    
    /**
     * Add a client to the system.
     * @param clientId: username of client.
     * @param email: email address of client.
     * @param password: new password of client.
     * @return returns true if successful, otherwise null
     * @throws RemoteException
     */
    public Boolean addClient(String clientId, String email, String password) throws RemoteException;

    /**
     * Check if a client login is successfull.
     * @param name: username of client
     * @param password: password of their account.
     * @return returns a publicKey if successful, otherwise null
     * @throws RemoteException
     */
    public PublicKey verifyClient(String name, String password) throws RemoteException;

    /**
     * adds an item to the system
     * @param item: item to be added to the system
     * @param clientId: client that added the item to the system
     * @return returns confirmation of item being added
     * @throws RemoteException
     */
    public ServerResponse putItem(String itemTitle, Boolean used, String description, String clientId) throws RemoteException;

    /**
     * shows the belongings of the client
     * @param clientId: username of current client
     * @return returns all items and auctions of client
     * @throws RemoteException
     */
    public ServerResponse showClientsBelongings(String clientId) throws RemoteException;

    /**
     * creates new auction of type forward
     * @param clientId: username of current client
     * @param title: title of auction
     * @return confirmation of creation
     * @throws RemoteException
     */
    public ServerResponse createForwardAuction(String clientId, String title) throws RemoteException;

    /**
     * creates new auction of type reverse
     * @param clientId: username of current client
     * @param title: title of auction
     * @return confirmation of creation
     * @throws RemoteException
     */
    public ServerResponse createReverseAuction(String clientId, String title) throws RemoteException;

    /**
     * creates new auction of type double
     * @param clientId: username of current client
     * @param title: title of auction
     * @return returns confirmation
     * @throws RemoteException
     */
    public ServerResponse createDoubleAuction(String clientId, String title) throws RemoteException;

    /**
     * closes specified auction
     * @param auctionId: id of auction to be closed
     * @param clientId: id of client closing the auction
     * @return returns confirmation
     * @throws RemoteException
     */
    public ServerResponse closeAuction(String auctionId, String clientId) throws RemoteException;

    /**
     * adds a bid to an auction
     * @param clientId: id of client bidding
     * @param auctionId: id of auction getting bid
     * @param bid: value of bid
     * @return returns confimartion
     * @throws RemoteException
     */
    public ServerResponse bid(String clientId, String auctionId, int bid) throws RemoteException;

    /**
     * prints all auctions
     * @return 
     * @throws RemoteException
     */
    public ServerResponse getAuctions() throws RemoteException;

    /**
     * adds an item to a specified auction
     * @param itemId
     * @param auctionId
     * @param reservedPrice: minimum price of the item seller is willing to accept
     * @param startingPrice: price displayed to the buyers
     * @param clientId
     * @return
     * @throws RemoteException
     */
    public ServerResponse addItemToAuction(String itemId, String auctionId, int reservedPrice, int startingPrice,
            String clientId) throws RemoteException;

    /**
     * prints items in an auction
     * @param auctionId
     * @return
     * @throws RemoteException
     */
    public ServerResponse getItemsInAuction(String auctionId) throws RemoteException;
}