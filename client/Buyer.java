import java.rmi.RemoteException;
import java.util.Scanner;

public class Buyer {

    private int clientId = -1;

    public void register(Scanner option, AuctionService server) {
        System.out.println("Register as a buyer by entering your name and email address.");
        System.out.print("Name: ");
        String name = option.nextLine();
        System.out.print("Email: ");
        String email = option.nextLine();
        try {
            clientId = server.addClient(name, email, ClientType.BUYER);
        } catch (RemoteException e) {
            System.err.println("Request could not be handled due to network problems.");
            e.printStackTrace();
        }
    }

    public void buyerMenu() {
        System.out.println("""
            
        You are a buyer.
        Available prompts:

        > help
            shows all available prompts

        > browse
            shows all the available auctions

        > show [auction id]
            shows a list of items in an auction along with their details
            EXAMPLE USAGE: show 2313
            
        > bid [auction id] [item id] [bid]
            allows you to bid for an item in a specified auction
            EXAMPLE USAGE: bid 1234 3421 500
                
        """);
    }

    public void buyerPrompts(Scanner option, AuctionService server) {
        // TODO:
        //int clientId = server.
        Boolean inProcess = true;
        while (inProcess) {
            String[] tokens = option.nextLine().split(" ");
            System.out.println();
            switch (tokens[0]) {
                case "help":
                    buyerMenu();
                    break;
                case "browse":
                    try {
                        System.out.println(server.getAuctions(clientId));
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        e.printStackTrace();
                        continue;
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
                        System.err.println("Request could not be handled due to network problems.");
                        e.printStackTrace();
                        continue;
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
                        System.err.println("Request could not be handled due to network problems.");
                        e.printStackTrace();
                        continue;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
