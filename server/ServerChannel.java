import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

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
        System.out.println("Clients: " + server.state.getClients().keySet());
        System.out.println("Auctions: " + server.state.getAuctions().keySet());
        System.out.println("Items: " + server.state.getItems().keySet());
    }

    public void getState(OutputStream output) throws Exception {
        synchronized(server.state) {
            Util.objectToStream(server.state, new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {
        ServerState new_state = (ServerState) Util.objectFromStream(new DataInputStream(input));
        synchronized(server.state) {
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
