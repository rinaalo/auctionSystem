import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReverseAuction extends Auction {

    private List<AuctionItem> auctionItems;
    private Bid auctionBid;

    public ReverseAuction(int auctionId) {
        super(auctionId);
        this.auctionItems = new LinkedList<>();
        auctionBid = null;
    }

    public List<AuctionItem> getAuctionItems() {
        return this.auctionItems;
    }

    @Override
    public AuctionType getAuctionType() {
        return AuctionType.REVERSE;
    }

    public Bid getAuctionBid() {
        return auctionBid;
    }

    @Override
    public Boolean noItemsInAuction() {
        if (auctionItems.isEmpty()) return true;
        return false;
    }

    @Override
    public Boolean noBidsInAuction() {
        if (auctionBid == null) return true;
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
        String highestBid = "";
        if (noBidsInAuction()) {
            highestBid = "No bid has been made yet.";
        } else {
            highestBid += auctionBid.getOffer();
        }
        String ret = "auction ID: " + getAuctionId() +
                "\nhighest bid: " + highestBid +
                "\ntype: " + getAuctionType() +
                "\nongoing: " + getOngoing() + "\n\n";
        return ret;
    }

    @Override
    public String addItemToAuction(AuctionItem item, int auctionId, Map<Integer, Auction> auctions) {
        auctionItems.add(item);
        return "Item has been added to auction.\n";
    }

    public AuctionItem getCheapestItem() {
        if(auctionItems.isEmpty()) {
            return null;
        }
        AuctionItem cheapestItem = auctionItems.get(0);
        int cheapestPrice = cheapestItem.getReservedPrice();
        for (AuctionItem item : auctionItems) {
            if(cheapestPrice > item.getReservedPrice()) {
                cheapestPrice = item.getReservedPrice();
                cheapestItem = item;
            }
        }
        return cheapestItem;
    }

    @Override
    public String bid(int offer, Client client) {
        // TODO: MAKE SURE ONLY THE PERSON WHO CREATED THE AUCTION CAN BID
        AuctionItem cheapestItem = getCheapestItem();
        if (offer < cheapestItem.getReservedPrice()) {
            setOngoing(false);
            for (AuctionItem auctionItem : auctionItems) {
                auctionItem.setInAuction(false);
            }
            return "Bid unsuccessful, the reserve has not been reached.\nAuction is closed.\n";
        }
        cheapestItem.setWinner(client.getClientId());
        auctionBid = new Bid(client, cheapestItem.getReservedPrice());
        int cheapestPrice = cheapestItem.getReservedPrice();
        String ret = "Lowest priced item in auction is " + cheapestItem.getItemId() + " with the price of " + cheapestPrice + ".\n" +
                    "You have bid the amount of " + cheapestItem.getReservedPrice() + " in auction " + getAuctionId() + "\n\n" +
                    determineWinner();
        return ret;
    }

    @Override
    public String determineWinner() {
        setOngoing(false);
        for (AuctionItem auctionItem : auctionItems) {
            auctionItem.setInAuction(false);
        }
        AuctionItem cheapestItem = getCheapestItem();
        String ret = "The item details: \n" + cheapestItem.printItemDetails();
        return ret;
    }
}
