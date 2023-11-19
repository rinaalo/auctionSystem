import java.io.Serializable;

public class AuctionItem implements Serializable {
    private String itemId;
    private String itemTitle;
    private Boolean used;
    private String itemDescription;
    private int reservedPrice;
    private int startingPrice;
    private Boolean inAuction;
    private String winnerId;
    private String sellerId;
    private int soldPrice;

    public AuctionItem(String itemId, String itemTitle, Boolean used, String itemDescription) {
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.used = used;
        this.itemDescription = itemDescription;
        inAuction = false;
    }

    // get methods
    public String getItemId() {
        return this.itemId;
    }

    public String getItemTitle() {
        return this.itemTitle;
    }

    public Boolean getCondition() {
        return this.used;
    }

    public String getItemDescription() {
        return this.itemDescription;
    }

    public int getReservedPrice() {
        return reservedPrice;
    }

    public int getStartingPrice() {
        return startingPrice;
    }

    public Boolean getInAuction() {
        return inAuction;
    }

    public String getWinner() {
        return winnerId;
    }

    public String getSeller() {
        return sellerId;
    }

    public int getSoldPrice() {
        return soldPrice;
    }

    // set methods
    public void setReservedPrice(int reservedPrice) {
        this.reservedPrice = reservedPrice;
    }
    
    public void setStartingPrice(int startingPrice) {
        this.startingPrice = startingPrice;
    }
    
    public void setInAuction(Boolean inAuction) {
        this.inAuction = inAuction;
    }

    public void setWinner(String winner) {
        this.winnerId = winner;
    }

    public void setSeller(String seller) {
        this.sellerId = seller;
    }

    public void setSoldPrice(int soldPrice) {
        this.soldPrice = soldPrice;
    }

    public String printItemDetails() {
        String price;
        if (!inAuction) {
            price = "";
        } else {
            price = "Starting price: " + startingPrice + "\n";
        }
        return ("Item id: " + itemId + "\n" +
                "Item title: " + itemTitle + "\n" +
                "Used: " + used + "\n" +
                "Item description: " + itemDescription + "\n" +
                price);
    }
}
