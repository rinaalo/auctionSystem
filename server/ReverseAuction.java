import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReverseAuction extends Auction {
    private List<AuctionItem> auctionItems;

    public ReverseAuction(int auctionId) {
        super(auctionId);
        this.auctionItems = new LinkedList<>();
        //TODO Auto-generated constructor stub
    }

    public List<AuctionItem> getAuctionItems() {
        return this.auctionItems;
    }

    @Override
    public AuctionType getAuctionType() {
        return AuctionType.REVERSE;
    }

    @Override
    public Boolean noItemsInAuction() {
        if (auctionItems.isEmpty()) return true;
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
    public String addItemToAuction(AuctionItem item, int auctionId, Map<Integer, Auction> auctions) {
        // TODO: add condition
        auctionItems.add(item);
        return "Item has been added to auction.\n";
    }

    @Override
    public String bid() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bid'");
    }

    @Override
    public String determineWinner(int auctionId, Map<Integer, Auction> auctions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'determineWinner'");
    }
}
