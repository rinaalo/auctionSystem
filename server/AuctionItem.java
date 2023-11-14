import java.io.Serializable;

public class AuctionItem implements Serializable{
    private int itemId;
    private String itemTitle;
    private String itemDescription;
    private Boolean used;
    private int reservedPrice;

    public AuctionItem(int itemId, String itemTitle, String itemDescription, Boolean used) {
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
        this.used = used;
    }
    
    // get methods
    public int getItemId() {
        return this.itemId;
    }
    public String getItemTitle() {
        return this.itemTitle;
    }
    public String getItemDescription() {
        return this.itemDescription;
    }
    public Boolean getCondition() {
        return this.used;
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
