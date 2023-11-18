import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Auction {
    private int auctionId;
    private Boolean ongoing;
    private List<Bid> auctionBids;

    public Auction(int auctionId) {
        this.auctionId = auctionId;
        this.ongoing = true;
        this.auctionBids = new LinkedList<>();
    }

    public Boolean getOngoing() {
        return this.ongoing;
    }

    public List<Bid> getAuctionBids() {
        return this.auctionBids;
    }

    public int getAuctionId() {
        return this.auctionId;
    }
    
    public void setOngoing(Boolean status) {
        this.ongoing = status;
    }

    public Bid getHighestBid() {
        if(auctionBids.isEmpty()) {
            return null;
        }
        int highestOffer = 0;
        Bid maxBid = auctionBids.get(0);
        for (Bid bid : auctionBids) {
            if (highestOffer < bid.getOffer()) {
                highestOffer = bid.getOffer();
                maxBid = bid;
            }
        }
        return maxBid;
    }

    public abstract AuctionType getAuctionType();
    
    public abstract Boolean noItemsInAuction();
    
    public abstract String printItemsInAuction();

    public abstract String addItemToAuction(AuctionItem item, int auctionId, Map<Integer, Auction> auctions);

    public abstract String bid();
    
    public abstract String determineWinner(int auctionId, Map<Integer, Auction> auctions);
}
