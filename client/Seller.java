import java.rmi.RemoteException;
import java.util.Scanner;

public class Seller extends ClientManagement {

    private int clientId = -1;

    @Override
    public void register(Scanner option, AuctionService server) {
        System.out.println("Register as a seller by entering your name and email address.");
        System.out.print("Name: ");
        String name = option.nextLine();
        System.out.print("Email: ");
        String email = option.nextLine();
        try {
            clientId = server.addClient(name, email, ClientType.SELLER);
        } catch (RemoteException e) {
            System.err.println("Request could not be handled due to network problems.");
        }
    }

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
        add [item title] [item condition] [item description]
        - - - - - - - - - - - - - - - - - - - - -
            adds an item to the system.
            the item title can only consist of one word with no spaces in between.
            the item condition can be set to true (used),
            anything else will be considered false (new).
            description must be a sentence describing the item
            EXAMPLE USAGE: add paint true acrylic type
        =========================================
        itemDetails [item id]
        - - - - - - - - - - - - - - - - - - - - -
            shows the details of specified item.
            EXAMPLE USAGE: itemDetails 1234
        =========================================
        createAuction [auction type]
        - - - - - - - - - - - - - - - - - - - - -
            starts an auction.
            available types: (f)orward, (d)ouble
            EXAMPLE USAGE: createAuction f
        =========================================
        addItemToAuction [item id] [auction id] [reserved price] [starting price]
        - - - - - - - - - - - - - - - - - - - - -
            adds an item to an auction
            reserved price is the minimum bid required for the item to be sold.
            starting price is the minimum amount the bidder is allowed to offer.
            EXAMPLE USAGE: add 1234 5678 100 50
        =========================================
        closeAuction [auction id]
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
                case "add":
                    if (tokens.length < 4) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        int itemId = server.generateItemId(clientId);
                        String itemTitle = tokens[1];
                        Boolean used = Boolean.valueOf(tokens[2]);
                        String description = "";
                        for (int i = 3; i < tokens.length; i++) {
                            description += (tokens[i] + " ");
                        }
                        AuctionItem newItem = new AuctionItem(itemId, itemTitle, used, description);
                        server.addItem(newItem);
                        clear();
                        System.out.println(server.itemDetails(itemId, clientId));
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "itemDetails":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        int itemId = Integer.parseInt(tokens[1]);
                        clear();
                        System.out.println(server.itemDetails(itemId, clientId));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID");
                        continue;
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "createAuction":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        String auctionType = tokens[1];
                        switch (auctionType.toLowerCase()) {
                            case "f":
                                clear();
                                System.out.println(server.createForwardAuction());
                                break;
                            case "d":
                                clear();
                                System.out.println(server.createDoubleAuction());
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
                case "addItemToAuction":
                    if (tokens.length < 5) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        int itemId = Integer.parseInt(tokens[1]);
                        int auctionId = Integer.parseInt(tokens[2]);
                        int reservedPrice = Integer.parseInt(tokens[3]);
                        int startingPrice = Integer.parseInt(tokens[4]);
                        clear();
                        System.out.println(server.addItemToAuction(itemId, auctionId, reservedPrice, startingPrice, clientId));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number");
                        continue;
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        continue;
                    }
                    break;
                case "closeAuction":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        int auctionId = Integer.parseInt(tokens[1]);
                        clear();
                        System.out.println(server.closeAuction(auctionId));
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
