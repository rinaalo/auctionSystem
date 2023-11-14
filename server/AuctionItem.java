import java.io.Serializable;

public class AuctionItem implements Serializable{
    private int itemId;
    private String itemTitle;
    private Boolean used;
    private String itemDescription;
    private int reservedPrice;

    public AuctionItem(int itemId, String itemTitle, Boolean used, String itemDescription) {
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.used = used;
        this.itemDescription = itemDescription;
    }
    
    // get methods
    public int getItemId() {
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

    // set methods
    public void setItemDescription(String newDescription) {
        this.itemDescription = newDescription;
    }
    public void setReservedPrice(int newPrice) {
        this.reservedPrice = newPrice;
    }
}
