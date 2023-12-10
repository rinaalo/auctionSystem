import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client{

    public static void clientPosition(AuctionService server, Scanner scanner) {
        Boolean invalidPositionInput = true;
        System.out.println("\nAre you a (s)eller or a (B)uyer?");  
        while(invalidPositionInput) {
            String position = scanner.nextLine();
            ClientManager c;
            if (position.equalsIgnoreCase("s") || position.equalsIgnoreCase("seller")) {
                invalidPositionInput = false;
                c = new Seller();
            } else if (position.equalsIgnoreCase("b") || position.equalsIgnoreCase("buyer") || position.equals("")) {
                invalidPositionInput = false; 
                c = new Buyer();
            } else {
                System.out.println("Please enter a valid position: (s)eller or (B)buyer");
                continue;
            }
            c.clientAccount(server, scanner);
        }
    }

    public static void main(String[] args) {
        System.out.println("=============================");
        System.out.println("-----------WELCOME-----------");
        System.out.println("=============================");
        Scanner scanner = new Scanner(System.in);
        
        try {
            String name = "myserver";
            Registry registry = LocateRegistry.getRegistry("localhost");
            AuctionService server = (AuctionService) registry.lookup(name);
            clientPosition(server, scanner);
        }
        catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
        scanner.close();
    }
}
