
public abstract class Auction {
    private int auctionId;
    private Boolean ongoing;
    private String creatorId;

    public Auction(int auctionId, String creatorId) {
        this.auctionId = auctionId;
        this.ongoing = true;
        this.creatorId = creatorId;
    }

    public Boolean getOngoing() {
        return this.ongoing;
    }

    public int getAuctionId() {
        return this.auctionId;
    }

    public String getCreatorId() {
        return this.creatorId;
    }

    public void setOngoing(Boolean status) {
        this.ongoing = status;
    }

    public abstract AuctionType getAuctionType();

    public abstract Boolean noItemsInAuction();

    public abstract Boolean noBidsInAuction();

    public abstract String printItemsInAuction();

    public abstract String printAuction();

    public abstract String addItemToAuction(AuctionItem item, String clientId);

    public abstract String bid(int offer, RegisteredClient client);

    public abstract String closeAuction();

    public abstract String getWinnerDetails();
}
