import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ForwardAuction extends Auction {

    private AuctionItem auctionItem;
    private List<Bid> auctionBids;

    
    public ForwardAuction(String auctionId, String creatorId, String title) {
        super(auctionId, creatorId, title);
        this.auctionItem = null;
        this.auctionBids = new LinkedList<>();
    }

    public AuctionItem getAuctionItem() {
        return this.auctionItem;
    }

    @Override
    public AuctionType getAuctionType() {
        return AuctionType.FORWARD;
    }

    public List<Bid> getAuctionBids() {
        return this.auctionBids;
    }

    @Override
    public Boolean noItemsInAuction() {
        if (auctionItem == null) return true;
        return false;
    }

    @Override
    public Boolean noBidsInAuction() {
        if(auctionBids.isEmpty()) return true;
        return false;
    }

    @Override
    public String printItemsInAuction() {
        if (auctionItem == null) {
            return "\nAuction has no item yet.\n";
        }
        String ret = "\nItem in auction " + getAuctionId() + ":\n\n";
        ret += auctionItem.printItemDetails() + "\n";
        return ret;
    }

    @Override
    public String printAuction() {
        String highestBid = "";
        if (noBidsInAuction()) {
            highestBid = "No bid has been made yet.";
        } else {
            highestBid += getHighestBid().getOffer();
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
        if (!clientId.equals(getCreatorId())) {
            return "Only the creator of this auction is allowed to add items.\n";
        }
        if (auctionItem != null) {
            return "This auction can only contain one item.\n";
        }
        auctionItem = item;
        return "Item " + item.getItemId() + " has been added to auction " + getAuctionId() + " by seller " + clientId + ".\n";
    }

    public Bid getHighestBid() {
        if(auctionBids.isEmpty()) {
            return null;
        }
        auctionBids.sort(Comparator.comparing(Bid::getOffer));
        return auctionBids.get(auctionBids.size() - 1);
    }

    @Override
    public String bid(int offer, ClientAccount client) {
        if (getHighestBid() == null) {
        }
        else if (offer <= getHighestBid().getOffer()) {
            return "Offer has to be higher than the current bid.\n";
        }
        getAuctionBids().add(new Bid(client, offer));
        System.out.println(client.getClientId() + " has bid " + offer + " amount in auction " + getAuctionId());
        return "You have bid the amount of " + offer + " in auction " + getAuctionId() + "\n";
    }

    @Override
    public String closeAuction() {
        setOngoing(false);
        // if there are no items
        if (auctionItem == null) {
            return "Auction is closed.\n";
        }
        auctionItem.setInAuction(false);
        Bid bid = getHighestBid();
        int offer = bid.getOffer();
        ClientAccount client = bid.getClient();
        // if there are no bidders
        if (getAuctionBids().isEmpty()) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }
        // if offer is less than reserved price.
        if (offer < auctionItem.getReservedPrice()) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }
        // if item is sold
        auctionItem.setWinner(client.getClientId());
        auctionItem.setSoldPrice(offer);
        auctionItem.setIsSold(true);
        setIsSuccess(true);
        return getWinnerDetails();
    }

    @Override
    public String getWinnerDetails() {
        String ret = "This Auction is closed!\nWinner details:\n\n";
        ret += "Item: " + auctionItem.getItemTitle() + 
                "\nItem ID: " + auctionItem.getItemId() +
                "\nSold Price " + auctionItem.getSoldPrice() +
                "\nBuyer: " + auctionItem.getWinner() +
                "\nSeller: " + auctionItem.getSeller()  + "\n\n";
        return ret;
    }
}