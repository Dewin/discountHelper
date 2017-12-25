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

    public static List<Store> linkStoresWithWebsite(List<Store> stores, double latitude, double longitude) throws IOException {

        for (Store s : stores) {
            switch (s.getType()) {
                case ICA:
                    s.setDiscountLink(getIcaHandler(latitude, longitude).findStoreWebsite(s));
                    break;
                default:
                    throw new RuntimeException("A store must have a type.");
            }
        }
        return stores;
    }

    public static void addStoreDiscountedItems(Store store, double latitude, double longitude) throws IOException {
        switch (store.getType()) {
            case ICA:
                getIcaHandler(latitude, longitude).addAllDiscountedItems(store);
                break;
            default:
                throw new RuntimeException("A store must have a type.");
        }
    }

    private static IcaHttpRequestHandler getIcaHandler(double latitude, double longitude) throws IOException {
        if (ica != null)
            return ica;
        ica = new IcaHttpRequestHandler(findCityByCords(latitude, longitude));
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

    static double distanceScore(String str1, String str2) {
        StringDistance distance = StringDistances.damerauLevenshtein();
        return distance.distance(str1, str2);
    }

    protected abstract String findStoreWebsite(Store store);

    protected abstract void addAllDiscountedItems(Store store);
}
