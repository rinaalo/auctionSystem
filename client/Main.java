import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Main{
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
                ClientManagement c;
                if (position.equalsIgnoreCase("s") || position.equalsIgnoreCase("seller")) {
                    invalidInput = false;
                    c = new Seller();
                } else if (position.equalsIgnoreCase("b") || position.equalsIgnoreCase("buyer") || position.equals("")) {
                    invalidInput = false; 
                    c = new Buyer();
                } else {
                    System.out.println("Please enter a valid position: (s)eller or (b)buyer");
                    continue;
                }
                c.register(option, server);
                c.showMenu();
                c.prompts(option, server);
            }
        }
        catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
        option.close();
    }
}
