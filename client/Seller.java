import java.rmi.RemoteException;
import java.util.Scanner;

public class Seller {

    private int clientId = 0;

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
            e.printStackTrace();
        }
    }

    public void sellerMenu() {
        System.out.println("""

        You are a seller.
        Available prompts:

        > help
            shows all available prompts

        > add [item title] [item condition] [item description]
            adds an item to the system.
            the item title can only consist of one word with no spaces in between.
            the item condition can be set to true (used), anything else will be considered false (new).
            description must be a sentence describing the item
            EXAMPLE USAGE: add paint true acrylic type

        > itemDetails [item id]
            shows the details of specified item.
            item id must belong to an existing item.
            EXAMPLE USAGE: itemDetails 102

        > createAuction [auction type]
            starts an auction.
            auction type will determine the behaviour of the auction.
                available types: (f)orward, (r)everse, (d)ouble
            EXAMPLE USAGE: createAuction f

        > addItemToAuction [item id] [reserved price] [auction id]
            adds an item to an auction
            item id belongs to the item that will be added to the auction
            reserved price is the minimum bid required for the item
            auction id belongs to the auction the item will be added to
            EXAMPLE USAGE: add 102 500 238

        > closeAuction [auction id]
            ends the specified auction
            auction id must be an ongoing auction's id.
            EXAMPLE USAGE: closeAuction 238

        """);
    }

    public void sellerPrompts(Scanner option, AuctionService server) {
        Boolean inProcess = true;
        while (inProcess) {
            String[] tokens = option.nextLine().split(" ");
            System.out.println();
            switch (tokens[0]) {
                case "help":
                    sellerMenu();
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
                        System.out.println(server.itemDetails(itemId, clientId));
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        e.printStackTrace();
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
                        System.out.println(server.itemDetails(itemId, clientId));
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
                case "createAuction":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        String auctionType = tokens[1];
                        switch (auctionType.toLowerCase()) {
                            case "f":
                                server.createForwardAuction();
                                break;
                            case "r":
                                server.createReverseAuction();
                                break;
                            case "d":
                                server.createDoubleAuction();
                                break;
                            default:
                                System.err.println("Please try again with a valid auction type f, r, d");
                                break;
                        }
                    } catch (RemoteException e) {
                        System.err.println("Request could not be handled due to network problems.");
                        e.printStackTrace();
                        continue;
                    }
                    break;
                case "addItemToAuction":
                    if (tokens.length < 3) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        int itemId = Integer.parseInt(tokens[1]);
                        int reservedPrice = Integer.parseInt(tokens[2]);
                        int auctionId = Integer.parseInt(tokens[3]);
                        System.out.println(server.addItemToAuction(itemId, reservedPrice, auctionId, clientId));
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
                case "closeAuction":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        int auctionId = Integer.parseInt(tokens[1]);
                        System.out.println(server.closeAuction(auctionId));
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
