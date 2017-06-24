package location;

import com.maxmind.geoip.LookupService;

import java.io.IOException;

/**
 * Created by Simon on 2017-06-24.
 */

public class GeoIPv4 {

    private static LookupService lookUp;

    static {
        try {
            lookUp = new LookupService(
                    GeoIPv4.class.getResource("GeoLiteCity.dat").getFile(),
                    LookupService.GEOIP_MEMORY_CACHE);

            System.out.println("GeoIP Database loaded: " + lookUp.getDatabaseInfo());
        } catch (IOException e) {
            System.out.println("Could not load geo ip database: " + e.getMessage());
        }
    }

    public static GeoLocation getLocation(String ipAddress) {
        return GeoLocation.map(lookUp.getLocation(ipAddress));
    }
}
