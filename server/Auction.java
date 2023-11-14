import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Auction {
    private Boolean ongoing;
    private String auctionType; // this might be an enum
    // item ids, list of bids
    private Map<Integer, List<Bid>> itemBids;
    public Auction(String auctionType) {
        this.auctionType = auctionType;
        itemBids = new Hashtable<>();
        ongoing = true;
    }
    public Boolean getOngoing() {
        return this.ongoing;
    }
    public String getAuctionType() {
        return this.auctionType;
    }
    public Map<Integer, List<Bid>> getItemBids() {
        return this.itemBids;
    }
    public void setOngoing(Boolean status) {
        this.ongoing = status;
    }
    public void setAuctionType(String type) {
        this.auctionType = type;
    }
}
