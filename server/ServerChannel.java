import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private ServerState state;
    private String name;
    private AuctionService stub;
    private Registry registry;
    private Address currentAddress;

    public ServerChannel(Server server, String name, AuctionService stub, Registry registry) {
        this.server = server;
        this.state = server.state;
        this.name = name;
        this.stub = stub;
        this.registry = registry;
    }

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        // getting updates
        synchronized(state) {
            try {
                channel.getState(null, 10000);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //state.add(line);
        }
    }

    public void getState(OutputStream output) throws Exception {
        synchronized(state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {
        ServerState new_state = (ServerState) Util.objectFromStream(new DataInputStream(input));
        synchronized(state) {
            state = new_state;
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
            if (currentAddress.equals(leader)) {
                try {
                    registry.rebind(name, stub);
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
            channel.getState(null, 10000);
            currentAddress = channel.getAddress();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        eventLoop();
        channel.close();
    }
}
