import java.rmi.RemoteException;
import java.util.Scanner;

public class Seller extends ClientManagement {

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
            EXAMPLE USAGE: show 2313
        =========================================
        add [item title] [item condition] [item description]
        - - - - - - - - - - - - - - - - - - - - -
            adds an item to the system.
            item title can only consist of one word with no spaces in between.
            item condition can be set to true (used),
            anything else will be considered false (new).
            description must be a sentence describing the item
            EXAMPLE USAGE: add paint true paints stuff
        =========================================
        create [auction type]
        - - - - - - - - - - - - - - - - - - - - -
            starts an auction.
            available types: (f)orward, (d)ouble
            EXAMPLE USAGE: createAuction f
        =========================================
        addToAuction [item id] [auction id] [reserved price] [starting price]
        - - - - - - - - - - - - - - - - - - - - -
            adds an item to an auction
            EXAMPLE USAGE: add 1234 5678 100 50
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
                case "mine":
                    clear();
                    try {
                        System.out.println(server.showClientsBelongings(getClientId()));
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        e.printStackTrace();
                    }
                    break;
                case "browse":
                    try {
                        clear();
                        System.out.println(server.getAuctions(getClientId()));
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
                        System.out.println(server.getItemsInAuction(tokens[1], getClientId()));
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "add":
                    if (tokens.length < 4) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        String itemId = server.generateItemId();
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
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        String auctionType = tokens[1];
                        switch (auctionType.toLowerCase()) {
                            case "f":
                                clear();
                                System.out.println(server.createForwardAuction(getClientId()));
                                break;
                            case "d":
                                clear();
                                System.out.println(server.createDoubleAuction(getClientId()));
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
                        System.out.println(server.addItemToAuction(itemId, auctionId, reservedPrice, startingPrice, getClientId()));
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
                        System.out.println(server.closeAuction(auctionId, getClientId()));
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
