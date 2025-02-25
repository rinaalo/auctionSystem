import java.io.Serializable;

public abstract class Auction implements Serializable{
    private String auctionId;
    private Boolean ongoing;
    private Boolean isSuccess;
    private String creatorId;
    private String title;

    public Auction(String auctionId, String creatorId, String title) {
        this.auctionId = auctionId;
        this.ongoing = true;
        this.isSuccess = false;
        this.creatorId = creatorId;
        this.title = title;
    }

    public Boolean getOngoing() {
        return this.ongoing;
    }

    public Boolean getIsSuccess() {
        return this.isSuccess;
    }

    public String getAuctionId() {
        return this.auctionId;
    }

    public String getCreatorId() {
        return this.creatorId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setOngoing(Boolean status) {
        this.ongoing = status;
    }

    public void setIsSuccess(Boolean success) {
        this.isSuccess = success;
    }

    public abstract AuctionType getAuctionType();

    public abstract Boolean noItemsInAuction();

    public abstract Boolean noBidsInAuction();

    public abstract String printItemsInAuction();

    public abstract String printAuction();

    public abstract String addItemToAuction(AuctionItem item, String clientId);

    public abstract String bid(int offer, ClientAccount client);

    public abstract String closeAuction();

    public abstract String getWinnerDetails();
}
