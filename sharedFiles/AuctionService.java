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
     * @return confirmation of creation
     * @throws RemoteException
     */
    public ServerResponse createDoubleAuction(String clientId, String title) throws RemoteException;

    /**
     * 
     * @param auctionId: id of auction to be closed
     * @param clientId: id of client closing the auction
     * @return closes auction
     * @throws RemoteException
     */
    public ServerResponse closeAuction(String auctionId, String clientId) throws RemoteException;

    /**
     * 
     * @param clientId
     * @param auctionId
     * @param bid
     * @return
     * @throws RemoteException
     */
    public ServerResponse bid(String clientId, String auctionId, int bid) throws RemoteException;

    /**
     * 
     * @return
     * @throws RemoteException
     */
    public ServerResponse getAuctions() throws RemoteException;

    /**
     * 
     * @param itemId
     * @param auctionId
     * @param reservedPrice
     * @param startingPrice
     * @param clientId
     * @return
     * @throws RemoteException
     */
    public ServerResponse addItemToAuction(String itemId, String auctionId, int reservedPrice, int startingPrice,
            String clientId) throws RemoteException;

    /**
     * 
     * @param auctionId
     * @return
     * @throws RemoteException
     */
    public ServerResponse getItemsInAuction(String auctionId) throws RemoteException;
}