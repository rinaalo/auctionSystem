public class Bid {
    private int clientId;
    private int offer;
    public Bid(int clientId, int bid) {
        this.clientId = clientId;
        this.offer = bid;
    }
    public int getClientId() {
        return this.clientId;
    }
    public int getOffer() {
        return this.offer;
    }
}
