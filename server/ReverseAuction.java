import java.util.LinkedList;
import java.util.List;

public class ReverseAuction extends Auction {

    private List<AuctionItem> auctionItems;
    private AuctionItem soldItem;
    private Bid auctionBid;

    public ReverseAuction(String auctionId, String creatorId, String title) {
        super(auctionId, creatorId, title);
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
                "\nauction title: " + getTitle() +
                "\nhighest bid: " + highestBid +
                "\ntype: " + getAuctionType() +
                "\nongoing: " + getOngoing() + "\n\n";
        return ret;
    }

    @Override
    public String addItemToAuction(AuctionItem item, String clientId) {
        auctionItems.add(item);
        item.setSeller(clientId);
        return "Item " + item.getItemId() + " has been added to auction " + getAuctionId() + " by seller " + clientId + ".\n";    
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
    public String bid(int offer, ClientAccount client) {
        // BID UNSUCCESSFULL
        if (!client.getClientId().equals(getCreatorId())) {
            return "Only the creator of this auction can bid.\n";
        }
        AuctionItem cheapestItem = getCheapestItem();
        if (offer < cheapestItem.getReservedPrice()) {
            setOngoing(false);
            for (AuctionItem auctionItem : auctionItems) {
                auctionItem.setInAuction(false);
            }
            return "Bid unsuccessful, the reserve has not been reached.\nAuction is closed.\n";
        }
        // BID SUCCESSFULL
        soldItem = cheapestItem;
        soldItem.setWinner(client.getClientId());
        int cheapestPrice = soldItem.getReservedPrice();
        soldItem.setSoldPrice(cheapestPrice);
        auctionBid = new Bid(client, cheapestItem.getReservedPrice());
        String ret = "Lowest priced item in auction is " + soldItem.getItemId() + " with the price of " + cheapestPrice + ".\n" +
                    "You have bought this item with the amount of " + soldItem.getReservedPrice() + " in auction " + getAuctionId() + "\n\n" +
                    closeAuction();
        return ret;
    }

    @Override
    public String closeAuction() {
        setOngoing(false);
        if(auctionItems.isEmpty()) {
            return "Auction is closed.\n";
        }
        for (AuctionItem auctionItem : auctionItems) {
            auctionItem.setInAuction(false);
        }
        return getWinnerDetails();
    }

    @Override
    public String getWinnerDetails() {
        String ret = "This Auction is closed!\nWinner details:\n\n";
        ret += "Item: " + soldItem.getItemTitle() + 
                "\nItem ID: " + soldItem.getItemId() +
                "\nSold Price " + soldItem.getSoldPrice() +
                "\nBuyer: " + soldItem.getWinner() +
                "\nSeller: " + soldItem.getSeller()  + "\n\n";
        return ret;
    }
}
