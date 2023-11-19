public class Bid {
    private Client client;
    private int offer;
    public Bid(Client client, int bid) {
        this.client = client;
        this.offer = bid;
    }
    public Client getClient() {
        return this.client;
    }
    public int getOffer() {
        return this.offer;
    }
}
