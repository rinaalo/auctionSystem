import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DoubleAuction extends Auction {

    private List<AuctionItem> auctionItems;
    private List<Bid> auctionBids;
    private Map<AuctionItem, Bid> winners;

    public DoubleAuction(String auctionId, String creatorId, String title) {
        super(auctionId, creatorId, title);
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
        if (auctionItems.isEmpty()) {
            return "\nAuction has no items yet.\n";
        }
        String ret = "\nAll items in auction " + getAuctionId() + ":\n\n";
        for (AuctionItem item : auctionItems) {
            ret += item.printItemDetails() + "\n";
        }
        return ret;
    }

    @Override
    public String printAuction() {
        String ret = "auction ID: " + getAuctionId() +
                "\nauction title: " + getTitle() +
                "\ntype: " + getAuctionType() +
                "\nongoing: " + getOngoing() + "\n\n";
        return ret;
    }

    @Override
    public String addItemToAuction(AuctionItem item, String clientId) {
        auctionItems.add(item);
        return "Item " + item.getItemId() + " has been added to auction " + getAuctionId() + " by seller " + clientId + ".\n";
    }

    @Override
    public String bid(int offer, ClientAccount client) {
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
        auctionBids.sort(Comparator.comparing(Bid::getOffer).reversed());
        auctionItems.sort(Comparator.comparing(AuctionItem::getReservedPrice));
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
        if (winners.isEmpty()) {
            return "Auction is closed.\nNo winners.\n";
        } else {
            setIsSuccess(true);
            for (AuctionItem soldItem : winners.keySet()) {
                Bid bid = winners.get(soldItem);
                soldItem.setWinner(bid.getClient().getClientId());
                soldItem.setSoldPrice(bid.getOffer());
                soldItem.setIsSold(true);
            }
        }
        return getWinnerDetails();
    }

    @Override
    public String getWinnerDetails() {
        String ret = "This Auction is closed!\nWinner details:\n\n";
        for (AuctionItem soldItem : winners.keySet()) {
            Bid bid = winners.get(soldItem);
            ret += "Item: " + soldItem.getItemTitle() + 
                "\nItem ID: " + soldItem.getItemId() +
                "\nSold Price " + soldItem.getSoldPrice() +
                "\nBuyer: " + bid.getClient().getClientId() +
                "\nSeller: " + soldItem.getSeller() + "\n\n";
        }
        return ret;
    }
}
