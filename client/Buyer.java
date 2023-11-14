import java.rmi.RemoteException;
import java.util.Scanner;

public class Buyer {

    private int clientId = 0;

    public void buyerMenu() {
        System.out.println("""
            
        You are a buyer.
        Available prompts:

        browse
            - shows all the available auctions
        show [auction id]
            - shows a list of items in an auction along with their details
        bid [auction id] [item id] [bid]
                
        """);
        //TODO: IN BID BUYERS NAME AND EMAIL
    }

    public void buyerPrompts(Scanner option, AuctionService server) {
        Boolean inProcess = true;
        while (inProcess) {
            String[] tokens = option.nextLine().split(" ");
            System.out.println();
            switch (tokens[0]) {
                case "browse":
                    try {
                        System.out.println(server.getAuctions(clientId));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case "show":
                    try {
                        System.out.println(server.getItemsInAuction(Integer.parseInt(tokens[1]), clientId));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID");
                        e.printStackTrace();
                        continue;
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case "bid":
                    if (tokens.length < 3) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        server.bid(clientId, Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID");
                        e.printStackTrace();
                        continue;
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
