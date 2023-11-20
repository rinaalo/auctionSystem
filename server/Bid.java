public class Bid {
    private ClientAccount client;
    private int offer;
    public Bid(ClientAccount client, int bid) {
        this.client = client;
        this.offer = bid;
    }
    public ClientAccount getClient() {
        return this.client;
    }
    public int getOffer() {
        return this.offer;
    }
}
