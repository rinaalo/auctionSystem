import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuctionService extends Remote{
    public AuctionItem getSpec(int itemId, int clientId) throws RemoteException; 
    public void addItem(AuctionItem item) throws RemoteException; 
    public int generateItemId(int clientId) throws RemoteException; 
    public int addClient(String name, String email, ClientType type) throws RemoteException; 
    public int createAuction(String auctionType) throws RemoteException;
    public void closeAuction(int auctionId) throws RemoteException;
    public void bid(int clientId, int auctionId, int itemId, int bid) throws RemoteException;
    public String itemDetails(int itemId, int clientId) throws RemoteException;
    public String getAuctions(int clientId) throws RemoteException;
    public void addItemToAuction(int itemId, int reservedPrice, int auctionId, int clientId) throws RemoteException;
    public String getItemsInAuction(int auctionId, int clientId) throws RemoteException;
}