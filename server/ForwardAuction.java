import java.util.Comparator;
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
    public String addItemToAuction(AuctionItem item, int clientId) {
        //TODO: SHOULD CHECK IF THE ID THAT CREATED THE AUCTION IS THE ONE ADDING THE ITEM
        if (auctionItem != null) {
            return "This auction can only contain one item.\n";
        }
        auctionItem = item;
        auctionItem.setSeller(clientId);
        return "Item " + item.getItemId() + " has been added to auction " + getAuctionId() + " by seller " + clientId + ".\n";
    }

    public Bid getHighestBid() {
        if(auctionBids.isEmpty()) {
            return null;
        }
        /* 
        Bid maxBid = auctionBids.get(0);
        int highestOffer = maxBid.getOffer();
        for (Bid bid : auctionBids) {
            if (highestOffer < bid.getOffer()) {
                highestOffer = bid.getOffer();
                maxBid = bid;
            }
        }
        return maxBid;*/
        auctionBids.sort(Comparator.comparing(Bid::getOffer));
        return auctionBids.get(auctionBids.size() - 1);

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
    public String closeAuction() {
        setOngoing(false);
        if (auctionItem == null) {
            return "Auction is closed.\n";
        }
        auctionItem.setInAuction(false);
        Bid bid = getHighestBid();
        int offer = bid.getOffer();
        Client client = bid.getClient();

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
        // if item is sold
        auctionItem.setWinner(client.getClientId());
        auctionItem.setSoldPrice(offer);
        return "Auction is closed." +
        "\nItem ID: " + auctionItem.getItemId() +
        "\nWinner: " + name +
        "\nBid: " + offer;
    }

    @Override
    public String getWinnerDetails(Map<Integer, Client> clients) {
        Client buyer = clients.get(auctionItem.getWinner());
        Client seller = clients.get(auctionItem.getSeller());
        String ret = "This Auction is closed!\n Winner details:";
        ret += "Item: " + auctionItem.getItemTitle() + 
                "\nItem ID: " + auctionItem.getItemId() +
                "\nBuyer: " + buyer.getName() +
                "\nSold Price " + auctionItem.getSoldPrice() +
                "\nSeller: " + seller.getName();
        return ret;
    }
}