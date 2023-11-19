import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        System.out.println("=============================");
        System.out.println("-----------WELCOME-----------");
        System.out.println("=============================");
        Scanner option = new Scanner(System.in);
        
        try {
            String name = "myserver";
            Registry registry = LocateRegistry.getRegistry("localhost");
            AuctionService server = (AuctionService) registry.lookup(name);
            
            Boolean invalidPositionInput = true;
            System.out.println("\nAre you a (s)eller or a (B)uyer?");  
            while(invalidPositionInput) {
                String position = option.nextLine();
                ClientManagement c;
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

                Boolean invalidEntryInput = true;
                System.out.println("\nWould you like to (R)egister or (l)ogin?");  
                while(invalidEntryInput) {
                    String entry = option.nextLine();
                    if (entry.equalsIgnoreCase("l") || entry.equalsIgnoreCase("login")) {
                        invalidEntryInput = false;
                        c.login(option, server);
                    } else if (entry.equalsIgnoreCase("r") || entry.equalsIgnoreCase("register") || entry.equals("")) {
                        invalidEntryInput = false; 
                        c.register(option, server);
                    } else {
                        System.out.println("Please enter a valid option: (R)egister or (l)ogin");
                        continue;
                    }
                }
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
