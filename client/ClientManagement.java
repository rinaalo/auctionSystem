import java.util.Scanner;

public abstract class ClientManagement {
    public abstract void register(Scanner option, AuctionService server);
    public abstract void showMenu();
    public abstract void prompts(Scanner option, AuctionService server);
}
