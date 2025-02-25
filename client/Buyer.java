import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Buyer extends ClientManager {

    private Client client;

    public Buyer(Client client) {
        super(client);
        this.client = client;
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
            switch (tokens[0]) {
                case "help":
                    clear();
                    break;
                case "quit":
                    System.out.println("Quitting");
                    System.exit(-1);
                    break;
                case "browse":
                    try {
                        clear();
                        ServerResponse response = server.getAuctions();
                        System.out.println(verifySignature(response));
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.\n");
                        System.err.println("Reconnecting Client...\n");
                        try {
                            server = client.connectClient();
                        } catch (RemoteException | NotBoundException e1) {
                            System.out.println("Could not connect.");
                        }
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
                        ServerResponse response = server.getItemsInAuction(tokens[1]);
                        System.out.println(verifySignature(response));
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.\n");
                        System.err.println("Reconnecting Client...\n");
                        try {
                            server = client.connectClient();
                        } catch (RemoteException | NotBoundException e1) {
                            System.out.println("Could not connect.");
                        }
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
                        String auctionId = tokens[1];
                        int offer = Integer.parseInt(tokens[2]);
                        ServerResponse response = server.bid(getClientId(), auctionId, offer);
                        System.out.println(verifySignature(response));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid bid");
                        continue;
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.\n");
                        System.err.println("Reconnecting Client...\n");
                        try {
                            server = client.connectClient();
                        } catch (RemoteException | NotBoundException e1) {
                            System.out.println("Could not connect.");
                        }
                        continue;
                    }
                    break;
                case "create":
                    if (tokens.length < 3) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
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
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.\n");
                        System.err.println("Reconnecting Client...\n");
                        try {
                            server = client.connectClient();
                        } catch (RemoteException | NotBoundException e1) {
                            System.out.println("Could not connect.");
                        }
                        continue;
                    }
                    break;
                case "close":
                    if (tokens.length < 2) {
                        continue;
                    }
                    try {
                        clear();
                        String auctionId = tokens[1];
                        ServerResponse response = server.closeAuction(auctionId, getClientId());
                        System.out.println(verifySignature(response));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID");
                        continue;
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.\n");
                        System.err.println("Reconnecting Client...\n");
                        try {
                            server = client.connectClient();
                        } catch (RemoteException | NotBoundException e1) {
                            System.out.println("Could not connect.");
                        }
                        continue;
                    }
                    break;
                default:
                    System.err.println("\nPlease enter a valid prompt.\n");
                    break;
            }
        }
    }
}
