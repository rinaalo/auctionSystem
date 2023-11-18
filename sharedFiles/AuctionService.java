import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuctionService extends Remote{
    public AuctionItem getSpec(int itemId, int clientId) throws RemoteException; 
    public void addItem(AuctionItem item) throws RemoteException; 
    public int generateItemId(int clientId) throws RemoteException; 
    public int addClient(String name, String email, ClientType type) throws RemoteException; 
    public String createForwardAuction() throws RemoteException;
    public String createReverseAuction() throws RemoteException;
    public String createDoubleAuction() throws RemoteException;
    public String closeAuction(int auctionId) throws RemoteException;
    public String bid(int clientId, int auctionId, int bid) throws RemoteException;
    public String itemDetails(int itemId, int clientId) throws RemoteException;
    public String getAuctions(int clientId) throws RemoteException;
    public String addItemToAuction(int itemId, int auctionId, int reservedPrice, int startingPrice, int clientId) throws RemoteException;
    public String getItemsInAuction(int auctionId, int clientId) throws RemoteException;
}