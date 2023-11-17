import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Auction {
    private int auctionId;
    private Boolean ongoing;
    private List<Bid> auctionBids;
    private List<Integer> auctionItems;

    public Auction(int auctionId) {
        this.auctionId = auctionId;
        this.ongoing = true;
        this.auctionBids = new LinkedList<>();
        this.auctionItems = new LinkedList<>();
    }

    public Boolean getOngoing() {
        return this.ongoing;
    }

    public List<Bid> getAuctionBids() {
        return this.auctionBids;
    }

    public List<Integer> getAuctionItems() {
        return this.auctionItems;
    }

    public int getAuctionId() {
        return this.auctionId;
    }
    
    public void setOngoing(Boolean status) {
        this.ongoing = status;
    }

    public abstract AuctionType getAuctionType();

    public abstract String addItemToAuction(int itemId, int auctionId, Map<Integer, Auction> auctions);
    
    public abstract String determineWinner(int auctionId);
}
