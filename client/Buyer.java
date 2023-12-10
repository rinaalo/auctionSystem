import java.rmi.RemoteException;
import java.util.Scanner;

public class Buyer extends ClientManager {

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
        quit
        - - - - - - - - - - - - - - - - - - - - -
            quits program
        =========================================
        browse
        - - - - - - - - - - - - - - - - - - - - -
            shows all the available auctions
        =========================================
        show [auction id]
        - - - - - - - - - - - - - - - - - - - - -
            shows a list of items in an auction
            EXAMPLE: show 2313
        =========================================
        bid [auction id] [bid]
        - - - - - - - - - - - - - - - - - - - - -
            bid for a specified auction
            EXAMPLE: bid 1234 500
        =========================================
        create [auction title] [auction type]
        - - - - - - - - - - - - - - - - - - - - -
            starts an auction.
            auction title must only be one word.
            available types: (r)everse, (d)ouble
            EXAMPLE: create jar r
        =========================================
        close [auction id]
        - - - - - - - - - - - - - - - - - - - - -
            ends the specified auction
            EXAMPLE: close 238
        =========================================
        """);
    }

    @Override
    public void prompts(Scanner option, AuctionService server) {
        Boolean inProcess = true;
        while (inProcess) {
            String[] tokens = option.nextLine().split(" ");
            System.out.println();
            try {
                switch (tokens[0]) {
                    case "help":
                        clear();
                        break;
                    case "quit":
                        System.out.println("Quitting");
                        System.exit(-1);
                        break;
                    case "browse":
                        clear();
                        ServerResponse response = server.getAuctions();
                        System.out.println(verifySignature(response));
                        break;
                    case "show":
                        if (tokens.length < 2) {
                            System.err.println("Not enough arguments");
                            continue;
                        }
                        clear();
                        response = server.getItemsInAuction(tokens[1]);
                        System.out.println(verifySignature(response));
                        break;
                    case "bid":
                        if (tokens.length < 3) {
                            System.err.println("Not enough arguments");
                            continue;
                        }
                        try {
                            clear();
                            String auctionId = tokens[1];
                            int offer = Integer.parseInt(tokens[2]);
                            response = server.bid(getClientId(), auctionId, offer);
                            System.out.println(verifySignature(response));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid bid");
                            continue;
                        }
                        break;
                    case "create":
                        if (tokens.length < 3) {
                            System.err.println("Not enough arguments");
                            continue;
                        }
                        String auctionTitle = tokens[1];
                        String auctionType = tokens[2];
                        switch (auctionType.toLowerCase()) {
                            case "r":
                                clear();
                                ServerResponse responseReverse = server.createAuction(getClientId(), auctionTitle, AuctionType.REVERSE);
                                System.out.println(verifySignature(responseReverse));
                                break;
                            case "d":
                                clear();
                                ServerResponse responseDouble = server.createAuction(getClientId(), auctionTitle, AuctionType.DOUBLE);
                                System.out.println(verifySignature(responseDouble));
                                break;
                            default:
                                System.err.println("Please try again with a valid auction type r or d");
                                break;
                        }
                        break;
                    case "close":
                        if (tokens.length < 2) {
                            continue;
                        }
                        try {
                            clear();
                            String auctionId = tokens[1];
                            response = server.closeAuction(auctionId, getClientId());
                            System.out.println(verifySignature(response));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid ID");
                            continue;
                        }
                        break;
                    default:
                        System.err.println("\nPlease enter a valid prompt.\n");
                        break;
                } 
            } catch (RemoteException e) {
                System.err.println("Request could not be handled due to network problems.\n");
            }
        }
    }
}
