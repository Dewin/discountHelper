package location;

import com.maxmind.geoip.Location;

/**
 * Created by Simon on 2017-06-24.
 */

public class GeoLocation {

    private String countryCode;
    private String countryName;
    private String postalCode;
    private String city;
    private String region;
    private int areaCode;
    private int dmaCode;
    private int metroCode;
    private float latitude;
    private float longitude;

    public GeoLocation(String countryCode, String countryName, String postalCode, String city, String region,
                       int areaCode, int dmaCode, int metroCode, float latitude, float longitude) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.postalCode = postalCode;
        this.city = city;
        this.region = region;
        this.areaCode = areaCode;
        this.dmaCode = dmaCode;
        this.metroCode = metroCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public int getAreaCode() {
        return areaCode;
    }

    public int getDmaCode() {
        return dmaCode;
    }

    public int getMetroCode() {
        return metroCode;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public static GeoLocation map(Location loc) {
        return new GeoLocation(loc.countryCode, loc.countryName, loc.postalCode, loc.city, loc.region,
                loc.area_code, loc.dma_code, loc.metro_code, loc.latitude, loc.longitude);
    }

    public double distance(float latitude, float longitude) {
        float lat1 = this.latitude;
        float lon1 = this.longitude;
        float lat2 = latitude;
        float lon2 = longitude;
        lat1 = (float)((double)lat1 * 0.017453292500000002D);
        lat2 = (float)((double)lat2 * 0.017453292500000002D);
        double delta_lat = (double)(lat2 - lat1);
        double delta_lon = (double)(lon2 - lon1) * 0.017453292500000002D;
        double temp = Math.pow(Math.sin(delta_lat / 2.0D), 2.0D) + Math.cos((double)lat1) * Math.cos((double)lat2) * Math.pow(Math.sin(delta_lon / 2.0D), 2.0D);
        return 12756.4D * Math.atan2(Math.sqrt(temp), Math.sqrt(1.0D - temp));
    }

    @Override
    public String toString() {
        return "location.GeoLocation{" +
                "countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", areaCode=" + areaCode +
                ", dmaCode=" + dmaCode +
                ", metroCode=" + metroCode +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}