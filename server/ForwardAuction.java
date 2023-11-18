import java.util.Map;

public class ForwardAuction extends Auction {

    private AuctionItem auctionItem;
    
    public ForwardAuction(int auctionId) {
        super(auctionId);
        this.auctionItem = null;
        //TODO Auto-generated constructor stub
    }

    public AuctionItem getAuctionItem() {
        return this.auctionItem;
    }

    @Override
    public AuctionType getAuctionType() {
        return AuctionType.FORWARD;
    }

    @Override
    public Boolean noItemsInAuction() {
        if (auctionItem == null) return true;
        return false;
    }

    @Override
    public String printItemsInAuction() {
        String ret = "\nItem in auction " + getAuctionId() + ":\n\n";
        ret += auctionItem.printItemDetails() + "\n";
        return ret;
    }

    @Override
    public String addItemToAuction(AuctionItem item, int auctionId, Map<Integer, Auction> auctions) {
        if (auctionItem != null) {
            return "This auction can only contain one item.\n";
        }
        auctionItem = item;
        return "Item " + item.getItemId() + " has been added to auction " + auctionId + ".\n";
    }

    @Override
    public String bid() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bid'");
    }

    @Override
    public String determineWinner(int auctionId, Map<Integer, Auction> auctions) {
        Auction auction = auctions.get(auctionId);
        Bid bid = auction.getHighestBid();
        int offer = bid.getOffer();
        int clientId = bid.getClientId();
        // if offer is less than reserved price.
        if (offer < auctionItem.getReservedPrice()) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }
        return "";
    }
}