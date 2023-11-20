import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuctionService extends Remote{
    //public AuctionItem getSpec(String itemId, String clientId) throws RemoteException; 
    public String addItem(AuctionItem item, String clientId) throws RemoteException; 
    public String generateItemId() throws RemoteException; 
    public String showClientsBelongings(String clientId) throws RemoteException;
    public Boolean addClient(String clientId, String email, String password) throws RemoteException; 
    public String createForwardAuction(String clientId) throws RemoteException;
    public String createReverseAuction(String clientId) throws RemoteException;
    public String createDoubleAuction(String clientId) throws RemoteException;
    public String closeAuction(String auctionId, String clientId) throws RemoteException;
    public String bid(String clientId, String auctionId, int bid) throws RemoteException;
    public String getAuctions(String clientId) throws RemoteException;
    public String addItemToAuction(String itemId, String auctionId, int reservedPrice, int startingPrice, String clientId) throws RemoteException;
    public String getItemsInAuction(String auctionId, String clientId) throws RemoteException;
}