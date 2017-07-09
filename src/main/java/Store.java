import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 2017-06-24.
 */
public class Store implements Comparable<Store> {
    private float latitude;
    private float longitude;
    private double distanceToUser;
    private String name;
    private String address;
    private String discountLink;
    private StoreType type;
    private List<Item> itemList;


    public Store(float latitude, float longitude, double distanceToUser, String name, String address, StoreType type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceToUser = distanceToUser;
        this.name = name;
        this.address = address;
        this.discountLink = "";
        this.type = type;
        this.itemList = new ArrayList<Item>();
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public double getDistanceToUser() {
        return distanceToUser;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public StoreType getType() {
        return type;
    }

    public String getDiscountLink() {
        return discountLink;
    }

    public void setDiscountLink(String discountLink) {
        this.discountLink = discountLink;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void addItem(Item item) {
        this.itemList.add(item);
    }

    public int compareTo(Store o) {
        return (this.distanceToUser < o.distanceToUser) ? -1 : 1;
    }
}
