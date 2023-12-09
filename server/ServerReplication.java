import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;


public class ServerReplication extends ReceiverAdapter {
    
    JChannel channel;
    ServerState state;

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        //String line = msg.getSrc() + ": " + msg.getObject();
        //System.out.println(line);
        synchronized(state) {
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
        while(true) {
            try {
                //line="[" + user_name + "] " + line;
                //Message msg=new Message(null, line);
                //channel.send(msg);
            }
            catch(Exception e) {
            }
        }
    }

    public void start(Server server) throws Exception {
        state = server.state;
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("ServerCluster");
        channel.getState(null, 10000);
        eventLoop();
        channel.close();
    }
}
