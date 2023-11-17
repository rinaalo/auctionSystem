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
        if (auctions.get(auctionId).getAuctionItems().size() >= 1) {
            return "This auction can only contain one item.\n";
        }
        auctions.get(auctionId).getAuctionItems().add(itemId);
        return "Item has been added to auction.\n";
    }

    @Override
    public String determineWinner(int auctionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'determineWinner'");
    }
}
