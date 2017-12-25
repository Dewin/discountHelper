import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.simmetrics.StringDistance;
import org.simmetrics.metrics.StringDistances;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by Simon on 2017-07-02. :)
 */
public abstract class HttpRequestHandler {

    private static IcaHttpRequestHandler ica;
    private static Double LATITUDE;
    private static Double LONGITUDE;

    public static void resetHandlers() {
        ica = null;
    }

    public static List<Store> linkStoresWithWebsite(List<Store> stores, double latitude, double longitude) throws IOException {
        LATITUDE = latitude;
        LONGITUDE = longitude;

        //Match by address
        for (Store s : getEmptyStores(stores)) {
            switch (s.getType()) {
                case ICA:
                    s.setDiscountLink(getIcaHandler().findStoreWebsiteAddress(s));
                    break;
                default:
                    throw new RuntimeException("A store must have a type.");
            }
        }

        //Match exact name
        for (Store s : getEmptyStores(stores)) {
            switch (s.getType()) {
                case ICA:
                    s.setDiscountLink(getIcaHandler().findStoreWebsiteNameExact(s));
                    break;
                default:
                    throw new RuntimeException("A store must have a type.");
            }
        }

        //Match not so exact name
        for (Store s : getEmptyStores(stores)) {
            switch (s.getType()) {
                case ICA:
                    s.setDiscountLink(getIcaHandler().findStoreWebsiteNameNotSoExact(s));
                    break;
                default:
                    throw new RuntimeException("A store must have a type.");
            }
        }

        //Match name by distance
        //TODO this is unsafe, maybe add a notification to this
        for (Store s : getEmptyStores(stores)) {
            switch (s.getType()) {
                case ICA:
                    s.setDiscountLink(getIcaHandler().findStoreWebsiteNameDistance(s));
                    break;
                default:
                    throw new RuntimeException("A store must have a type.");
            }
        }

        return stores;
    }

    public static void addStoreDiscountedItems(Store store, double latitude, double longitude) throws IOException {
        LATITUDE = latitude;
        LONGITUDE = longitude;

        switch (store.getType()) {
            case ICA:
                getIcaHandler().addAllDiscountedItems(store);
                break;
            default:
                throw new RuntimeException("A store must have a type.");
        }
    }

    private static IcaHttpRequestHandler getIcaHandler() throws IOException {
        if (ica != null)
            return ica;
        if (LATITUDE == null || LONGITUDE == null)
            throw new IOException("LATITUDE("+LATITUDE+") or LONGITUDE("+LONGITUDE+") cannot be null");
        ica = new IcaHttpRequestHandler(findCityByCords(LATITUDE, LONGITUDE));
        return ica;
    }

    private static String findCityByCords(double latitude, double longitude) throws IOException {
        String googleApiString = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude;
        URL googleApiUrl = new URL(googleApiString);
        HttpURLConnection request = (HttpURLConnection) googleApiUrl.openConnection();
        request.connect();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonArray result = root.getAsJsonObject().get("results").getAsJsonArray();
        request.disconnect();

        JsonArray tempRoot = result.get(0).getAsJsonObject().get("address_components").getAsJsonArray();
        for (int i = 0; i < tempRoot.size(); i++) {
            if (tempRoot.get(i).getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString().equals("postal_town"))
                return tempRoot.get(i).getAsJsonObject().get("long_name").getAsString();
        }

        return null;
    }

    double distanceScore(String str1, String str2) {
        StringDistance distance = StringDistances.damerauLevenshtein();
        return distance.distance(str1, str2);
    }

    private static List<Store> getEmptyStores(List<Store> myStores) {
        List<Store> emptyStores = new ArrayList<Store>();
        for (Store store : myStores) {
            if (store.getDiscountLink() == null || store.getDiscountLink().equals(""))
                emptyStores.add(store);
        }
        return emptyStores;
    }

    protected abstract String findStoreWebsiteAddress(Store store);

    protected abstract String findStoreWebsiteNameExact(Store store);

    protected abstract String findStoreWebsiteNameNotSoExact(Store store);

    protected abstract String findStoreWebsiteNameDistance(Store store);

    protected abstract void addAllDiscountedItems(Store store);
}
