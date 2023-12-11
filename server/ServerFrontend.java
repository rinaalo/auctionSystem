import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;

import java.util.Map.Entry;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

public class ServerFrontend implements AuctionService {

    private JChannel channel;
    private RpcDispatcher dispatcher;

    public ServerFrontend() {
        try {
            channel = new JChannel();
            channel.connect("AuctionCluster");
            channel.setDiscardOwnMessages(true);
            dispatcher = new RpcDispatcher(channel, this);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataState getState() {
        DataState s = null;
        try {
            s = dispatcher.callRemoteMethod(
                    channel.getView().getMembers().get(1),
                    new MethodCall("getState", new Object[]{}, new Class[]{}),
                    new RequestOptions(ResponseMode.GET_FIRST, 5000, true)
                    );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }


    @Override
    public Boolean addClient(String clientId, String email, String password) throws RemoteException {
        RspList<Boolean> list = null;
        try {
            list = dispatcher.callRemoteMethods(
                null,
                new MethodCall("addClient",
                    new Object[]{clientId, email, password},
                    new Class[]{clientId.getClass(), email.getClass(), password.getClass()}),
                new RequestOptions(ResponseMode.GET_ALL, 5000, false)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) throw new RemoteException();
        if (list.isEmpty()) throw new RemoteException();
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
            list = dispatcher.callRemoteMethods(
                null,
                new MethodCall("verifyClient",
                    new Object[]{name, password},
                    new Class[]{name.getClass(), password.getClass()}),
                new RequestOptions(ResponseMode.GET_ALL, 5000, false)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) throw new RemoteException();
        if (list.isEmpty()) throw new RemoteException();
        PublicKey ret = list.getFirst();
        for (Entry<Address, Rsp<PublicKey>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) throw new RemoteException();
        }
        return ret;
    }

    @Override
    public ServerResponse putItem(String itemTitle, Boolean used, String description, String clientId) throws RemoteException {
        RspList<ServerResponse> list = null;
        try {
            list = dispatcher.callRemoteMethods(
                null,
                new MethodCall("putItem",
                    new Object[]{itemTitle, used, description, clientId},
                    new Class[]{itemTitle.getClass(), used.getClass(), description.getClass(), clientId.getClass()}),
                new RequestOptions(ResponseMode.GET_ALL, 5000, false)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) throw new RemoteException();
        if (list.isEmpty()) throw new RemoteException();
        ServerResponse ret = list.getFirst();
        for (Entry<Address, Rsp<ServerResponse>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) { 
                System.out.println("NOT EQUAL!");
                throw new RemoteException();
            }
        }
        return ret;
    }
    @Override
    public ServerResponse showClientsBelongings(String clientId) throws RemoteException {
        RspList<ServerResponse> list = null;
        try {
            list = dispatcher.callRemoteMethods(
                null,
                new MethodCall("showClientsBelongings",
                    new Object[]{clientId},
                    new Class[]{clientId.getClass()}),
                new RequestOptions(ResponseMode.GET_ALL, 5000, false)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) throw new RemoteException();
        if (list.isEmpty()) throw new RemoteException();
        ServerResponse ret = list.getFirst();
        for (Entry<Address, Rsp<ServerResponse>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) throw new RemoteException();
        }
        return ret;
    }
    @Override
    public ServerResponse createAuction(String clientId, String title, AuctionType auctionType) throws RemoteException {
        RspList<ServerResponse> list = null;
        try {
            list = dispatcher.callRemoteMethods(
                null,
                new MethodCall("createAuction",
                    new Object[]{clientId, title, auctionType}, 
                    new Class[]{clientId.getClass(), title.getClass(), auctionType.getClass()}), 
                new RequestOptions(ResponseMode.GET_ALL, 5000, false)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) throw new RemoteException();
        if (list.isEmpty()) throw new RemoteException();
        ServerResponse ret = list.getFirst();
        for (Entry<Address, Rsp<ServerResponse>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) throw new RemoteException();
        }
        return ret;
    }
    @Override
    public ServerResponse closeAuction(String auctionId, String clientId) throws RemoteException {
        RspList<ServerResponse> list = null;
        try {
            list = dispatcher.callRemoteMethods(
                null,
                new MethodCall("closeAuction",
                    new Object[]{auctionId, clientId},
                    new Class[]{auctionId.getClass(), clientId.getClass()}), 
                new RequestOptions(ResponseMode.GET_ALL, 5000, false)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) throw new RemoteException();
        if (list.isEmpty()) throw new RemoteException();
        ServerResponse ret = list.getFirst();
        for (Entry<Address, Rsp<ServerResponse>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) throw new RemoteException();
        }
        return ret;
    }
    @Override
    public ServerResponse bid(String clientId, String auctionId, int bid) throws RemoteException {
        RspList<ServerResponse> list = null;
        try {
            list = dispatcher.callRemoteMethods(
                null,
                new MethodCall("bid",
                    new Object[]{clientId, auctionId, bid}, 
                    new Class[]{clientId.getClass(), auctionId.getClass(), int.class}), 
                new RequestOptions(ResponseMode.GET_ALL, 5000, false)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) throw new RemoteException();
        if (list.isEmpty()) throw new RemoteException();
        ServerResponse ret = list.getFirst();
        for (Entry<Address, Rsp<ServerResponse>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) throw new RemoteException();
        }
        return ret;
    }
    @Override
    public ServerResponse getAuctions() throws RemoteException {
        RspList<ServerResponse> list = null;
        try {
            list = dispatcher.callRemoteMethods(
                null,
                new MethodCall("getAuctions",
                    new Object[]{}, 
                    new Class[]{}), 
                new RequestOptions(ResponseMode.GET_ALL, 5000, false)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) throw new RemoteException();
        if (list.isEmpty()) throw new RemoteException();
        ServerResponse ret = list.getFirst();
        for (Entry<Address, Rsp<ServerResponse>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) throw new RemoteException();
        }
        return ret;
    }
    @Override
    public ServerResponse addItemToAuction(String itemId, String auctionId, int reservedPrice, int startingPrice, String clientId) throws RemoteException {
        RspList<ServerResponse> list = null;
        try {
            list = dispatcher.callRemoteMethods(
                null,
                new MethodCall("addItemToAuction",
                    new Object[]{itemId, auctionId, reservedPrice, startingPrice, clientId}, 
                    new Class[]{itemId.getClass(), auctionId.getClass(), int.class, int.class, clientId.getClass()}), 
                new RequestOptions(ResponseMode.GET_ALL, 5000, false)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) throw new RemoteException();
        if (list.isEmpty()) throw new RemoteException();
        ServerResponse ret = list.getFirst();
        for (Entry<Address, Rsp<ServerResponse>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) throw new RemoteException();
        }
        return ret;
    }
    @Override
    public ServerResponse getItemsInAuction(String auctionId) throws RemoteException {
        RspList<ServerResponse> list = null;
        try {
            list = dispatcher.callRemoteMethods(
                null,
                new MethodCall("getItemsInAuction",
                    new Object[]{auctionId}, 
                    new Class[]{auctionId.getClass()}), 
                new RequestOptions(ResponseMode.GET_ALL, 5000, false)
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) throw new RemoteException();
        if (list.isEmpty()) throw new RemoteException();
        ServerResponse ret = list.getFirst();
        for (Entry<Address, Rsp<ServerResponse>> entry : list.entrySet()) {
            if (!ret.equals(entry.getValue().getValue())) throw new RemoteException();
        }
        return ret;
    }

    public static void main(String[] args) {
        try {
            ServerFrontend frontEnd = new ServerFrontend();
            String name = "myserver";
            Remote stub = (AuctionService) UnicastRemoteObject.exportObject(frontEnd, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, (AuctionService) stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
