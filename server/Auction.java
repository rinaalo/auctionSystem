import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Auction {
    private Boolean ongoing;
    private AuctionType type;
    // item ids, list of bids
    private Map<Integer, List<Bid>> itemBids;
    public Auction(AuctionType type) {
        this.type = type;
        itemBids = new Hashtable<>();
        ongoing = true;
    }
    public Boolean getOngoing() {
        return this.ongoing;
    }
    public AuctionType getAuctionType() {
        return this.type;
    }
    public Map<Integer, List<Bid>> getItemBids() {
        return this.itemBids;
    }
    public void setOngoing(Boolean status) {
        this.ongoing = status;
    }
}
