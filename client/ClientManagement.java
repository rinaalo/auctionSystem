import java.rmi.RemoteException;
import java.util.Scanner;

public abstract class ClientManagement {

    private String clientId;

    public ClientManagement() {
        clientId = null;
    }

    public String getClientId() {
        return clientId;
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
                    System.out.println("Registry Successful.\n");
                    invalidInput = false;
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

    }

    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        showMenu();
    }
    
    public abstract void showMenu();
    public abstract void prompts(Scanner option, AuctionService server);
}
