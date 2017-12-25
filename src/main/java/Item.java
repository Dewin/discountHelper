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
    private Condition condition;

    public Item(String name, String info, String imageLink, double price, int amount, Unit unit) {
        this.name = name;
        this.info = info;
        this.imageLink = imageLink;
        this.price = price;
        this.amount = amount;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
