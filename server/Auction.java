import java.util.Map;

public abstract class Auction {
    private int auctionId;
    private Boolean ongoing;

    public Auction(int auctionId) {
        this.auctionId = auctionId;
        this.ongoing = true;
    }

    public Boolean getOngoing() {
        return this.ongoing;
    }

    public int getAuctionId() {
        return this.auctionId;
    }

    public void setOngoing(Boolean status) {
        this.ongoing = status;
    }

    public abstract AuctionType getAuctionType();

    public abstract Boolean noItemsInAuction();

    public abstract Boolean noBidsInAuction();

    public abstract String printItemsInAuction();

    public abstract String printAuction();

    public abstract String addItemToAuction(AuctionItem item, int clientId);

    public abstract String bid(int offer, Client client);

    public abstract String closeAuction();

    public abstract String getWinnerDetails(Map<Integer, Client> clients);
}
