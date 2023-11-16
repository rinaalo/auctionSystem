import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public abstract class Auction {
    private int auctionId;
    private Boolean ongoing;
    // item ids, list of bids
    private Map<Integer, List<Bid>> itemBids;

    public Auction(int auctionId) {
        this.auctionId = auctionId;
        this.itemBids = new Hashtable<>();
        this.ongoing = true;
    }

    public Boolean getOngoing() {
        return this.ongoing;
    }

    public Map<Integer, List<Bid>> getItemBids() {
        return this.itemBids;
    }

    public int getAuctionId() {
        return this.auctionId;
    }
    
    public void setOngoing(Boolean status) {
        this.ongoing = status;
    }

    public abstract AuctionType getAuctionType();

    public abstract String addItemToAuction(int itemId, int auctionId, Map<Integer, Auction> auctions);

}
