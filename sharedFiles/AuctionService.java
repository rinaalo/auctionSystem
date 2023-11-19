import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuctionService extends Remote{
    public AuctionItem getSpec(int itemId, String clientId) throws RemoteException; 
    public void addItem(AuctionItem item) throws RemoteException; 
    public int generateItemId(String clientId) throws RemoteException; 
    public Boolean addClient(String clientId, String email, String password, ClientType type) throws RemoteException; 
    public String createForwardAuction(String clientId) throws RemoteException;
    public String createReverseAuction(String clientId) throws RemoteException;
    public String createDoubleAuction(String clientId) throws RemoteException;
    public String closeAuction(int auctionId, String clientId) throws RemoteException;
    public String bid(String clientId, int auctionId, int bid) throws RemoteException;
    public String itemDetails(int itemId, String clientId) throws RemoteException;
    public String getAuctions(String clientId) throws RemoteException;
    public String addItemToAuction(int itemId, int auctionId, int reservedPrice, int startingPrice, String clientId) throws RemoteException;
    public String getItemsInAuction(int auctionId, String clientId) throws RemoteException;
}