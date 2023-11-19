import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DoubleAuction extends Auction {

    private List<AuctionItem> auctionItems;
    private List<Bid> auctionBids;

    public DoubleAuction(int auctionId) {
        super(auctionId);
        this.auctionItems = new LinkedList<>();
        this.auctionBids = new LinkedList<>();
    }

    public List<AuctionItem> getAuctionItems() {
        return this.auctionItems;
    }

    @Override
    public AuctionType getAuctionType() {
        return AuctionType.DOUBLE;
    }

    public List<Bid> getAuctionBids() {
        return this.auctionBids;
    }

    @Override
    public Boolean noItemsInAuction() {
        if (auctionItems.isEmpty())
            return true;
        return false;
    }

    @Override
    public Boolean noBidsInAuction() {
        if(auctionBids.isEmpty()) return true;
        return false;
    }

    @Override
    public String printItemsInAuction() {
        String ret = "\nAll items in auction " + getAuctionId() + ":\n\n";
        for (AuctionItem item : auctionItems) {
            ret += item.printItemDetails() + "\n";
        }
        return ret;
    }

    @Override
    public String printAuction() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'printAuction'");
    }

    @Override
    public String addItemToAuction(AuctionItem item, int auctionId, Map<Integer, Auction> auctions) {
        auctionItems.add(item);
        return "Item has been added to auction.\n";
    }

    @Override
    public String bid(int offer, Client client) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bid'");
    }

    @Override
    public String determineWinner() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'determineWinner'");
    }
}
