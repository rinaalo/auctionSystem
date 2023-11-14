import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client{

    private static int clientId = 0;

    public static void sellerMenu() {
        System.out.println("""

        You are a seller.
        Available prompts:

        add [item id] [item title] [item condition]
            adds an item to the system.
            - the item id must be an integer.
            - the item title can only consist of one word with no spaces in between.
            - the item condition can be set to true (used), anything else will be considered false (new).
            EXAMPLE USAGE: add 102 paint true

        addDesc [item id] [description]
            adds a description for specified item.
            - the item id belongs to the item you want to add a description to.
            - description must be a sentence describing the item
            EXAMPLE USAGE: addDescription 102 can be used for creating drawings

        itemDetails [item id]
            shows the details of specified item.
            - item id must belong to an existing item.
            EXAMPLE USAGE: itemDetails 102

        createAuction [auction type]
            starts an auction.
            - auction type will determine the behaviour of the auction.
                - available types: reverse, double
            EXAMPLE USAGE: createAuction reverse

        addItemToAuction [item id] [reserved price] [auction id]
            adds an item to an auction
            - item id belongs to the item that will be added to the auction
            - reserved price is the minimum bid required for the item
            - auction id belongs to the auction the item will be added to
            EXAMPLE USAGE: add 102 500 238

        closeAuction [auction id]
            ends the specified auction
            - auction id must be an ongoing auction's id.
            EXAMPLE USAGE: closeAuction 238

        """);
    }

    public static void sellerPrompts(Scanner option, AuctionService server) {
        Boolean inProcess = true;
        while (inProcess) {
            String[] tokens = option.nextLine().split(" ");
            System.out.println();
            switch (tokens[0]) {
                case "add":
                    if (tokens.length < 4) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        AuctionItem newItem = new AuctionItem(Integer.parseInt(tokens[1]), tokens[2], "No available description", Boolean.valueOf(tokens[3]));
                        server.addItem(newItem);
                        System.out.println(server.itemDetails(Integer.parseInt(tokens[1]), clientId));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID");
                        continue;
                    } catch (RemoteException e) {
                        System.err.println("Exception: ");
                        e.printStackTrace();
                    }
                    break;
                case "addDesc":
                    if (tokens.length < 3) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        String description = "";
                        for (int i = 2; i < tokens.length; i++) {
                            description += (tokens[i] + " ");
                        }
                        server.addDescription(Integer.parseInt(tokens[1]), clientId, description);
                        System.out.println(server.itemDetails(Integer.parseInt(tokens[1]), clientId));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID");
                        e.printStackTrace();
                        continue;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case "itemDetails":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        System.out.println(server.itemDetails(Integer.parseInt(tokens[1]), clientId));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID");
                        e.printStackTrace();
                        continue;
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case "createAuction":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        server.createAuction(tokens[1]);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case "addItemToAuction":
                    if (tokens.length < 3) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        server.addItemToAuction(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), clientId);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID");
                        e.printStackTrace();
                        continue;
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case "closeAuction":
                    if (tokens.length < 2) {
                        System.err.println("Not enough arguments");
                        continue;
                    }
                    try {
                        server.closeAuction(Integer.parseInt(tokens[1]));
                        // TODO should determine the winner
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

    public static void buyerMenu() {
        System.out.println("""
            
        You are a buyer.
        Available prompts:

        browse
            - shows all the available auctions
        show [auction id]
            - shows a list of items in an auction
        bid [auction id] [item id] [bid]
                
        """);
    }

    public static void buyerPrompts(Scanner option, AuctionService server) {
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
        // browse active auctions with their current highest bid
        // bid for an item by entering the buyer’s details: name and email address. 
    }

    public static void main(String[] args) {
        System.out.println("=============================");
        System.out.println("-----------WELCOME-----------");
        System.out.println("=============================");
        Scanner option = new Scanner(System.in);
        System.out.println("\nAre you a (s)eller or a (B)uyer?");  
        
        try {
            String name = "myserver";
            Registry registry = LocateRegistry.getRegistry("localhost");
            AuctionService server = (AuctionService) registry.lookup(name);

            Boolean invalidInput = true;
            while(invalidInput) {
                String position = option.nextLine();
                if (position.equalsIgnoreCase("s") || position.equalsIgnoreCase("seller")) {
                    invalidInput = false;
                    sellerMenu();
                    sellerPrompts(option, server);
                } else if (position.equalsIgnoreCase("b") || position.equalsIgnoreCase("buyer") || position.equals("")) {
                    invalidInput = false; 
                    buyerMenu();
                    buyerPrompts(option, server);
                } else {
                    System.out.println("Please enter a valid position: (s)eller or (b)buyer");
                }
            }
        }
        catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
        option.close();
    }
}
