import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.security.KeyPair;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

public class ServerChannel extends ReceiverAdapter implements Runnable {
    
    private JChannel channel;
    private Server server;
    private String name;
    private AuctionService stub;
    private Registry registry;
    private Address currentAddress;
    private boolean LEADER;

    public ServerChannel(Server server, String name, AuctionService stub, Registry registry) {
        this.server = server;
        this.name = name;
        this.stub = stub;
        this.registry = registry;
        LEADER = false;
    }

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    @Override
    public void receive(Message msg) {
        System.out.println("new message!! new message!!");
        // getting updates
        synchronized(server.state) {
            try {
                InputStream targetStream = new ByteArrayInputStream(msg.getBuffer());
                ServerState new_state = (ServerState) Util.objectFromStream(new DataInputStream(targetStream));
                if (new_state.equals(server.state)) {
                    // if new state is same as old state, do not do anything
                } else {
                    Address leader = channel.getView().getCoord();
                    if (leader.equals(msg.getSrc())) {
                        server.state = new_state;
                        //channel.send(msg);
                    }
                    else {
                        // if message not from leader, do not do anything
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Clients: " + server.state.getClients());
        System.out.println("Auctions: " + server.state.getAuctions());
        System.out.println("Items: " + server.state.getItems());
        //System.out.println("Private key: " + server.state.getKeyPair().getPrivate());
        //System.out.println("Public key: " + server.state.getKeyPair().getPublic());
    }

    public void getState(OutputStream output) throws Exception {
        System.out.println("made it to get state");
        synchronized(server.state) {
            Util.objectToStream(server.state, new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {
        System.out.println("made it to set state");
        ServerState new_state = (ServerState) Util.objectFromStream(new DataInputStream(input));
        synchronized(server.state) {
            /*server.state.getAuctions().clear(); 
            server.state.getAuctions().putAll(new_state.getAuctions()); 

            server.state.getItems().clear(); 
            server.state.getItems().putAll(new_state.getItems()); 

            server.state.getClients().clear(); 
            server.state.getClients().putAll(new_state.getClients()); 

            server.state.setKeyPair(new KeyPair(new_state.getKeyPair().getPublic(), new_state.getKeyPair().getPrivate()));*/
            server.state = new_state;
        }
    }

    public void sendMessage(Message msg) {
        try {
            channel.send(msg);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void eventLoop() {
        Address leader;
        while(true) {
            leader = channel.getView().getCoord();
            if(LEADER) {}
            else if (currentAddress.equals(leader)) {
                try {
                    System.out.println("new leader");
                    registry.rebind(name, stub);
                    LEADER = true;
                } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            channel = new JChannel();
            channel.setReceiver(this);
            channel.connect("AuctionCluster");
            channel.getState(null, 5000);
            currentAddress = channel.getAddress();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        eventLoop();
        channel.close();
    }
}
