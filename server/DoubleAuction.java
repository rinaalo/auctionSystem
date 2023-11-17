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
        auctions.get(auctionId).getAuctionItems().add(itemId);
        return "Item has been added to auction.\n";
    }

    @Override
    public String determineWinner(int auctionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'determineWinner'");
    }
    
}
