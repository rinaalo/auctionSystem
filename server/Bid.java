public class Bid {
    private int clientId;
    private int itemId;
    private int offer;
    public Bid(int clientId, int itemId, int bid) {
        this.clientId = clientId;
        this.itemId = itemId;
        this.offer = bid;
    }
    public int getClientId() {
        return this.clientId;
    }
    public int getItemId() {
        return this.itemId;
    }
    public int getOffer() {
        return this.offer;
    }
}
