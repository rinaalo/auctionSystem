import java.util.LinkedList;
import java.util.Map;

public class DoubleAuction extends Auction {

    public DoubleAuction(int auctionId) {
        super(auctionId);
        //TODO Auto-generated constructor stub
    }

    @Override
    public AuctionType getAuctionType() {
        return AuctionType.DOUBLE;
    }

    @Override
    public String addItemToAuction(int itemId, int auctionId, Map<Integer, Auction> auctions) {
        //TODO: add condition
        auctions.get(auctionId).getItemBids().put(itemId, new LinkedList<>());
        return "Item has been added to auction.\n";
    }
    
}
