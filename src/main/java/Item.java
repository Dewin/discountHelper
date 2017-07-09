/**
 * Created by Simon on 2017-07-08. :)
 */
public class Item {

    private String name;
    private String info;
    private String imageLink;
    private double price;
    private int amount;
    private Unit unit;

    public Item(String name, String info, String imageLink, double price, int amount, Unit unit) {
        this.name = name;
        this.info = info;
        this.imageLink = imageLink;
        this.price = price;
        this.amount = amount;
        this.unit = unit;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
