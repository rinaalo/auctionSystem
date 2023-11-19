public class Bid {
    private RegisteredClient client;
    private int offer;
    public Bid(RegisteredClient client, int bid) {
        this.client = client;
        this.offer = bid;
    }
    public RegisteredClient getClient() {
        return this.client;
    }
    public int getOffer() {
        return this.offer;
    }
}
