import java.util.LinkedList;
import java.util.Map;

public class ReverseAuction extends Auction {

    public ReverseAuction(int auctionId) {
        super(auctionId);
        //TODO Auto-generated constructor stub
    }

    @Override
    public AuctionType getAuctionType() {
        return AuctionType.REVERSE;
    }

    @Override
    public String addItemToAuction(int itemId, int auctionId, Map<Integer, Auction> auctions) {
        //TODO: add condition
        auctions.get(auctionId).getItemBids().put(itemId, new LinkedList<>());
        return "Item has been added to auction.\n";
    }
    
}
