import java.util.LinkedList;
import java.util.Map;

public class ForwardAuction extends Auction {
    
    public ForwardAuction(int auctionId) {
        super(auctionId);
        //TODO Auto-generated constructor stub
    }

    @Override
    public AuctionType getAuctionType() {
        return AuctionType.FORWARD;
    }

    @Override
    public String addItemToAuction(int itemId, int auctionId, Map<Integer, Auction> auctions) {
        if (auctions.get(auctionId).getItemBids().size() >= 1) {
            return "This auction can only contain one item.\n";
        }
        auctions.get(auctionId).getItemBids().put(itemId, new LinkedList<>());
        return "Item has been added to auction.\n";
    }
}
