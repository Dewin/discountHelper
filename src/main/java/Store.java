/**
 * Created by Simon on 2017-06-24.
 */
public class Store implements Comparable<Store> {
    private float latitude;
    private float longitude;
    private double distanceToUser;
    private String name;
    private String address;

    public Store(float latitude, float longitude, double distanceToUser, String name, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceToUser = distanceToUser;
        this.name = name;
        this.address = address;
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

    public int compareTo(Store o) {
        return (this.distanceToUser < o.distanceToUser) ? -1 : 1;
    }
}
