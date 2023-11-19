import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ForwardAuction extends Auction {

    private AuctionItem auctionItem;
    private List<Bid> auctionBids;

    
    public ForwardAuction(int auctionId) {
        super(auctionId);
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
                "\nhighest bid: " + highestBid +
                "\ntype: " + getAuctionType() +
                "\nongoing: " + getOngoing() + "\n\n";
        return ret;
    }

    @Override
    public String addItemToAuction(AuctionItem item, int auctionId, Map<Integer, Auction> auctions) {
        //TODO: SHOULD CHECK IF THE ID THAT CREATED THE AUCTION IS THE ONE ADDING THE ITEM
        if (auctionItem != null) {
            return "This auction can only contain one item.\n";
        }
        auctionItem = item;
        return "Item " + item.getItemId() + " has been added to auction " + auctionId + ".\n";
    }

    public Bid getHighestBid() {
        if(auctionBids.isEmpty()) {
            return null;
        }
        Bid maxBid = auctionBids.get(0);
        int highestOffer = maxBid.getOffer();
        for (Bid bid : auctionBids) {
            if (highestOffer < bid.getOffer()) {
                highestOffer = bid.getOffer();
                maxBid = bid;
            }
        }
        return maxBid;
    }

    @Override
    public String bid(int offer, Client client) {
        if (getHighestBid() == null) {
        }
        else if (offer <= getHighestBid().getOffer()) {
            return "Offer has to be higher than the current bid.\n";
        }
        getAuctionBids().add(new Bid(client, offer));
        return "You have bid the amount of " + offer + " in auction " + getAuctionId() + "\n";
    }

    @Override
    public String determineWinner() {
        setOngoing(false);
        auctionItem.setInAuction(false);
        Bid bid = getHighestBid();
        int offer = bid.getOffer();
        Client client = bid.getClient();

        auctionItem.setWinner(client.getClientId());

        String name = client.getName();
        String email = client.getEmail();
        // if there is no bidders
        if (getAuctionBids().isEmpty()) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }
        // if offer is less than reserved price.
        if (offer < auctionItem.getReservedPrice()) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }
        return "Auction is closed." +
        "\nWinner: " + name +
        "\nEmail: " + email +
        "\nItem ID: " + auctionItem.getItemId() +
        "\nBid: " + offer;
    }
}