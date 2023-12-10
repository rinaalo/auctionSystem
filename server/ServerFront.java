import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.Map.Entry;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

public class ServerFront implements AuctionService, Receiver{

    private Registry registry;
    private JChannel channel;
    private RpcDispatcher dispatcher;

    public ServerFront() {
        try {
            channel = new JChannel();
            channel.connect("AuctionCluster");
            channel.setDiscardOwnMessages(true);
            dispatcher = new RpcDispatcher(channel, this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ServerFront frontEnd = new ServerFront();
            Remote stub = (AuctionService) UnicastRemoteObject.exportObject(frontEnd, 0);
            frontEnd.registry = LocateRegistry.getRegistry();
            frontEnd.registry.rebind("myserver",(AuctionService) stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'send'");
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }

    @Override
    public Boolean addClient(String clientId, String email, String password) throws RemoteException {
        RspList<Boolean> list = null;
        try {
            list = dispatcher.callRemoteMethods(null, new MethodCall("addClient", new Object[]{clientId, email, password}, 
                    new Class[]{clientId.getClass(), email.getClass(), password.getClass()}), 
                    new RequestOptions(ResponseMode.GET_ALL, 5000, false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(list == null || list.isEmpty()) throw new RemoteException();
        Boolean ret = list.getFirst();
        for (Entry<Address, Rsp<Boolean>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) throw new RemoteException();
        }
        return ret;
    }
    
    @Override
    public PublicKey verifyClient(String name, String password) throws RemoteException {
        RspList<PublicKey> list = null;
        try {
            list = dispatcher.callRemoteMethods(null, new MethodCall("verifyClient", new Object[]{name, password}, 
                    new Class[]{name.getClass(), password.getClass()}), 
                    new RequestOptions(ResponseMode.GET_ALL, 5000, false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(list == null || list.isEmpty()) throw new RemoteException();
        PublicKey ret = list.getFirst();
        for (Entry<Address, Rsp<PublicKey>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) throw new RemoteException();
        }
        return ret;
    }

    @Override
    public ServerResponse putItem(String itemTitle, Boolean used, String description, String clientId)
            throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'putItem'");
    }
    @Override
    public ServerResponse showClientsBelongings(String clientId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showClientsBelongings'");
    }
    @Override
    public ServerResponse createAuction(String clientId, String title, AuctionType auctionType) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAuction'");
    }
    @Override
    public ServerResponse closeAuction(String auctionId, String clientId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'closeAuction'");
    }
    @Override
    public ServerResponse bid(String clientId, String auctionId, int bid) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bid'");
    }
    @Override
    public ServerResponse getAuctions() throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuctions'");
    }
    @Override
    public ServerResponse addItemToAuction(String itemId, String auctionId, int reservedPrice, int startingPrice,
            String clientId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addItemToAuction'");
    }
    @Override
    public ServerResponse getItemsInAuction(String auctionId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getItemsInAuction'");
    }
}
