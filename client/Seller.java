import java.rmi.RemoteException;
import java.util.Scanner;

public class Seller extends ClientManager {

    @Override
    public void showMenu() {
        System.out.println("""

        You are a seller.
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
        mine
        - - - - - - - - - - - - - - - - - - - - -
            shows all of your items and auctions
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
        add [item title] [item condition] [item description]
        - - - - - - - - - - - - - - - - - - - - -
            adds an item to the system.
            item title must only be one word.
            item condition can be true (used),
            anything else will be considered false (new).
            description can be a sentence.
            EXAMPLE: add paint true paints stuff
        =========================================
        create [auction title] [auction type]
        - - - - - - - - - - - - - - - - - - - - -
            starts an auction.
            auction title must only be one word.
            available types: (f)orward, (d)ouble
            EXAMPLE: create jars f
        =========================================
        addToAuction [item id] [auction id] [reserved price] [starting price]
        - - - - - - - - - - - - - - - - - - - - -
            adds an item to an auction
            EXAMPLE: addToAuction 1234 5678 100 50
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
                case "mine":
                    clear();
                    try {
                        ServerResponse response = server.showClientsBelongings(getClientId());
                        SignatureVerifier.getInstance();
                        if (SignatureVerifier.verify(getServerPublicKey(), response)) {
                            System.out.println(response.getMessage());
                        }
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        e.printStackTrace();
                    }
                    break;
                case "browse":
                    try {
                        clear();
                        ServerResponse response = server.getAuctions(getClientId());
                        SignatureVerifier.getInstance();
                        if (SignatureVerifier.verify(getServerPublicKey(), response)) {
                            System.out.println(response.getMessage());
                        }
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
                        ServerResponse response = server.getItemsInAuction(tokens[1], getClientId());
                        SignatureVerifier.getInstance();
                        if (SignatureVerifier.verify(getServerPublicKey(), response)) {
                            System.out.println(response.getMessage());
                        }
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "add":
                //TODO: FIX
                    if (tokens.length < 4) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        ServerResponse resultItemId = server.generateItemId();
                        String itemId = resultItemId.getMessage();
                        String itemTitle = tokens[1];
                        Boolean used = Boolean.valueOf(tokens[2]);
                        String description = "";
                        for (int i = 3; i < tokens.length; i++) {
                            description += (tokens[i] + " ");
                        }
                        AuctionItem newItem = new AuctionItem(itemId, itemTitle, used, description);
                        clear();
                        System.out.println(server.addItem(newItem, getClientId())); 
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
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
                            case "f":
                                clear();
                                ServerResponse responseForward = server.createForwardAuction(getClientId(), auctionTitle);
                                SignatureVerifier.getInstance();
                                if (SignatureVerifier.verify(getServerPublicKey(), responseForward)) {
                                    System.out.println(responseForward.getMessage());
                                }
                                break;
                            case "d":
                                clear();
                                ServerResponse responseDouble = server.createDoubleAuction(getClientId(), auctionTitle);
                                SignatureVerifier.getInstance();
                                if (SignatureVerifier.verify(getServerPublicKey(), responseDouble)) {
                                    System.out.println(responseDouble.getMessage());
                                }
                                break;
                            default:
                                System.err.println("Please try again with a valid auction type f or d");
                                break;
                        }
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "addToAuction":
                    if (tokens.length < 5) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        String itemId = tokens[1];
                        String auctionId = tokens[2];
                        int reservedPrice = Integer.parseInt(tokens[3]);
                        int startingPrice = Integer.parseInt(tokens[4]);
                        clear();
                        ServerResponse response = server.addItemToAuction(itemId, auctionId, reservedPrice, startingPrice, getClientId());
                        SignatureVerifier.getInstance();
                        if (SignatureVerifier.verify(getServerPublicKey(), response)) {
                            System.out.println(response.getMessage());
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid price");
                        continue;
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "close":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        String auctionId = tokens[1];
                        clear();
                        ServerResponse response = server.closeAuction(auctionId, getClientId());
                        SignatureVerifier.getInstance();
                        if (SignatureVerifier.verify(getServerPublicKey(), response)) {
                            System.out.println(response.getMessage());
                        }
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
