import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.Scanner;

public abstract class ClientManager {

    private String clientId;
    private PublicKey publicKey;

    public ClientManager() {
        clientId = null;
        publicKey = null;
    }

    public String getClientId() {
        return clientId;
    }

    public PublicKey getServerPublicKey() {
        return publicKey;
    }

    public void clientAccount(AuctionService server, Scanner scanner) {
        Boolean invalidEntryInput = true;
        System.out.println("\nWould you like to (R)egister or (l)ogin?");  
        while(invalidEntryInput) {
            String entry = scanner.nextLine();
            if (entry.equalsIgnoreCase("l") || entry.equalsIgnoreCase("login")) {
                invalidEntryInput = false;
                login(scanner, server);
            } else if (entry.equalsIgnoreCase("r") || entry.equalsIgnoreCase("register") || entry.equals("")) {
                invalidEntryInput = false; 
                register(scanner, server);
            } else {
                System.out.println("Please enter a valid option: (R)egister or (l)ogin");
                continue;
            }
        }
    }

    public void register(Scanner option, AuctionService server) {
        Boolean invalidInput = true;
        while(invalidInput) {
            System.out.println("\n------ Register ------");
            System.out.print("Username: ");
            String name = option.nextLine();
            System.out.print("Email: ");
            String email = option.nextLine();
            System.out.print("Password: ");
            String password = option.nextLine();
            
            try {
                if(server.addClient(name, email, password)) {
                    clientId = name;
                    System.out.println("\nRegistry Successful.\n");
                    invalidInput = false;
                    login(option, server);
                } else{
                    System.out.println("\nUsername taken. Try again.\n");
                    continue;
                };
            } catch (RemoteException e) {
                System.err.println("Request could not be handled due to network problems.\n");
            }
        }
    }

    public void login(Scanner option, AuctionService server) {
        Boolean invalidInput = true;
        while(invalidInput) {
            System.out.println("\n------ Login ------");
            System.out.print("Username: ");
            String name = option.nextLine();
            System.out.print("Password: ");
            String password = option.nextLine();

            try {
                publicKey = server.verifyClient(name, password);
                if(publicKey != null) {
                    System.out.println("\nLogin Successful.\n");
                    invalidInput = false;
                    showMenu();
                    prompts(option, server);
                } else {
                    System.out.println("\nLogin Failed.\n"); // TODO fix
                    continue;
                }
            } catch (RemoteException e) {
                System.err.println("Request could not be handled due to network problems.\n");
            }
        }
    }

    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        showMenu();
    }
    
    public abstract void showMenu();
    public abstract void prompts(Scanner option, AuctionService server);
}
