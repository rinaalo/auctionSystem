import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuctionService extends Remote{
    public Boolean addClient(String clientId, String email, String password) throws RemoteException; 
    //public AuctionItem getSpec(String itemId, String clientId) throws RemoteException; 
    public ServerResponse addItem(AuctionItem item, String clientId) throws RemoteException; 
    public ServerResponse generateItemId() throws RemoteException; 
    public ServerResponse showClientsBelongings(String clientId) throws RemoteException;
    public ServerResponse createForwardAuction(String clientId) throws RemoteException;
    public ServerResponse createReverseAuction(String clientId) throws RemoteException;
    public ServerResponse createDoubleAuction(String clientId) throws RemoteException;
    public ServerResponse closeAuction(String auctionId, String clientId) throws RemoteException;
    public ServerResponse bid(String clientId, String auctionId, int bid) throws RemoteException;
    public ServerResponse getAuctions(String clientId) throws RemoteException;
    public ServerResponse addItemToAuction(String itemId, String auctionId, int reservedPrice, int startingPrice, String clientId) throws RemoteException;
    public ServerResponse getItemsInAuction(String auctionId, String clientId) throws RemoteException;
}