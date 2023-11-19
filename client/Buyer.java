import java.rmi.RemoteException;
import java.util.Scanner;

public class Buyer extends ClientManagement {

    private String clientId;

    public Buyer() {
        clientId = null;
    }

    @Override
    public void register(Scanner option, AuctionService server) {
        Boolean invalidInput = true;
        while(invalidInput) {
            System.out.println("------ Register ------");
            System.out.print("Username: ");
            String name = option.nextLine();
            System.out.print("Email: ");
            String email = option.nextLine();
            System.out.print("Password: ");
            String password = option.nextLine();

            try {
                if(server.addClient(name, email, password, ClientType.BUYER)) {
                    clientId = name;
                    System.out.println("Registry Successful.\n");
                    invalidInput = false;
                } else{
                    System.out.println("\nUsername taken. Try again.\n");
                    continue;
                };
            } catch (RemoteException e) {
                System.err.println("Request could not be handled due to network problems.\n");
            }
        }
    }

    @Override
    public void login(Scanner option, AuctionService server) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
    
    @Override
    public void showMenu() {
        System.out.println("""
        
        You are a buyer.
        Available prompts:
        =========================================
        help
        - - - - - - - - - - - - - - - - - - - - -
            shows all available prompts
        =========================================
        browse
        - - - - - - - - - - - - - - - - - - - - -
            shows all the available auctions
        =========================================
        show [auction id]
        - - - - - - - - - - - - - - - - - - - - -
            shows a list of items in an auction along with their details
            EXAMPLE USAGE: show 2313
        =========================================
        bid [auction id] [bid]
        - - - - - - - - - - - - - - - - - - - - -
            allows you to bid for a specified auction
            EXAMPLE USAGE: bid 1234 500
        =========================================
        create [auction type]
        - - - - - - - - - - - - - - - - - - - - -
            starts an auction.
            available types: (r)everse, (d)ouble
            EXAMPLE USAGE: createAuction r
        =========================================
        close [auction id]
        - - - - - - - - - - - - - - - - - - - - -
            ends the specified auction
            EXAMPLE USAGE: closeAuction 238
        =========================================
        """);
    }

    @Override
    public void prompts(Scanner option, AuctionService server) {
        Boolean inProcess = true;
        while (inProcess) {
            String[] tokens = option.nextLine().split(" ");
            System.out.println();
            switch (tokens[0]) {
                case "help":
                    clear();
                    break;
                case "browse":
                    try {
                        clear();
                        System.out.println(server.getAuctions(clientId));
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "show":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        clear();
                        System.out.println(server.getItemsInAuction(Integer.parseInt(tokens[1]), clientId));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID");
                        continue;
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "bid":
                    if (tokens.length < 3) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        clear();
                        System.out.println(server.bid(clientId, Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID or bid");
                        continue;
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "create":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        String auctionType = tokens[1];
                        switch (auctionType.toLowerCase()) {
                            case "r":
                                clear();
                                System.out.println(server.createReverseAuction(clientId));
                                break;
                            case "d":
                                clear();
                                System.out.println(server.createDoubleAuction(clientId));
                                break;
                            default:
                                System.err.println("Please try again with a valid auction type r or d");
                                break;
                        }
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "close":
                    if (tokens.length < 2) {
                        continue;
                    }
                    try {
                        int auctionId = Integer.parseInt(tokens[1]);
                        clear();
                        System.out.println(server.closeAuction(auctionId, clientId));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID");
                        continue;
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
