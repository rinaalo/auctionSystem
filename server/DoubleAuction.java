import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DoubleAuction extends Auction {

    private List<AuctionItem> auctionItems;
    private List<Bid> auctionBids;
    private Map<AuctionItem, Bid> winners;

    public DoubleAuction(int auctionId) {
        super(auctionId);
        this.auctionItems = new LinkedList<>();
        this.auctionBids = new LinkedList<>();
        this.winners = new Hashtable<>();
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
        String ret = "auction ID: " + getAuctionId() +
                "\ntype: " + getAuctionType() +
                "\nongoing: " + getOngoing() + "\n\n";
        return ret;
    }

    @Override
    public String addItemToAuction(AuctionItem item, int clientId) {
        auctionItems.add(item);
        item.setSeller(clientId);
        return "Item " + item.getItemId() + " has been added to auction " + getAuctionId() + " by seller " + clientId + ".\n";
    }

    @Override
    public String bid(int offer, Client client) {
        getAuctionBids().add(new Bid(client, offer));
        return "You have bid the amount of " + offer + " in auction " + getAuctionId() + "\n";
    }

    @Override
    public String closeAuction() {
        // close auction
        setOngoing(false);
        if(auctionItems.isEmpty()) {
            return "Auction is closed.\n";
        }
        for (AuctionItem auctionItem : auctionItems) {
            auctionItem.setInAuction(false);
        }
        if (auctionBids.isEmpty()) {
            return "Auction is closed.\nThe reserve has not been reached.\n";
        }
        // determine winners
        auctionBids.sort(Comparator.comparing(Bid::getOffer));
        auctionItems.sort(Comparator.comparing(AuctionItem::getReservedPrice).reversed());
        if (auctionItems.size() < auctionBids.size()) {
            for (int i = 0; i < auctionItems.size(); i++) {
                if (auctionBids.get(i).getOffer() < auctionItems.get(i).getReservedPrice()) continue;
                winners.put(auctionItems.get(i), auctionBids.get(i));
            } 
        } else {
            for (int i = 0; i < auctionBids.size(); i++) {
                if (auctionBids.get(i).getOffer() < auctionItems.get(i).getReservedPrice()) continue;
                winners.put(auctionItems.get(i), auctionBids.get(i));
            }
        }
        for (AuctionItem soldItem : winners.keySet()) {
            Bid bid = winners.get(soldItem);
            soldItem.setWinner(bid.getClient().getClientId());
            soldItem.setSoldPrice(bid.getOffer());
        }
        String ret = "Auction is closed.\nSold items:\n " + winnerItems();
        return ret;
    }

    public String winnerItems() {
        String ret = "";
        for (AuctionItem soldItem : winners.keySet()) {
            Bid bid = winners.get(soldItem);
            ret += "Item: " + soldItem.getItemTitle() + 
                "\nItem ID: " + soldItem.getItemId() +
                "\nBuyer ID: " + bid.getClient() +
                "\nSold Price " + soldItem.getSoldPrice() +
                "\nSeller ID: " + soldItem.getSeller();
        }
        return ret;
    }

    @Override
    public String getWinnerDetails(Map<Integer, Client> clients) {
        String ret = "This Auction is closed!\n Winner details:";
        for (AuctionItem soldItem : winners.keySet()) {
            Bid bid = winners.get(soldItem);
            ret += "Item: " + soldItem.getItemTitle() + 
                "\nItem ID: " + soldItem.getItemId() +
                "\nBuyer: " + bid.getClient().getName() +
                "\nSold Price " + soldItem.getSoldPrice() +
                "\nSeller: " + clients.get(soldItem.getSeller()).getName();
        }
        return ret;
    }
}
