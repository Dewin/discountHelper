import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

    public static List<Store> linkStoresWithWebsite(List<Store> stores, double latitude, double longitude) throws IOException {

        String city = findCityByCords(latitude, longitude);
        List<Element> cards = findAllStoreCards(city);
        Map<String, String> foundStoresWithLink = extractNameAndLink(cards);
        matchStores(stores, foundStoresWithLink);
        return stores;
    }

    public static List<Item> addAllDiscountedItems(Store store, String discountLink) {
        List<Element> sliderElements = findAllSliderElements(discountLink);
        String name;
        String info;
        String imageLink;
        double price;
        int amount;
        Unit unit;
        if (sliderElements != null) {
            for (Element e : sliderElements) {
                name = extractName(e);
                info = extractInfo(e);
                imageLink = extractImageLink(e);
                price = extractPrice(e);
                amount = extractAmount(e);
                unit = extractUnit(e);
                store.addItem(new Item(name, info, imageLink, price, amount, unit));
            }
        }
        return null;
    }

    private static String extractName(Element element) {
        return element.getElementsByClass("item-name-product").text();
    }

    private static String extractInfo(Element element) {
        return element.getElementsByClass("item-info").text();
    }

    private static String extractImageLink(Element element) {
        return element.getElementsByClass("item-image").attr("src");
    }

    private static double extractPrice(Element element) {
        String valueString = element.getElementsByClass("product-price__price-value").text().replaceAll("[:\\-%]", "");
        int value = Integer.parseInt(valueString);
        String decimalString = element.getElementsByClass("product-price__decimal").text();
        if (!decimalString.equals("")) {
            int decimal = Integer.parseInt(decimalString);
            return Double.parseDouble(value + "." + decimal);
        }
        return Double.parseDouble(value + "");
    }

    private static int extractAmount(Element element) {
        String amountString = element.getElementsByClass("product-price__amount").text().replaceAll(" för", "");
        int amount = 1;
        if (!amountString.equals("")) {
            amount = Integer.parseInt(amountString);
        }
        return amount;
    }

    private static Unit extractUnit(Element element) {
        String unitString = element.getElementsByClass("product-price__unit-item").text().replace("/", "").replace(" ", "").toUpperCase();
        if (unitString.equals("KG")) {
            return Unit.KG;
        } else if (unitString.equals("ST")) {
            return Unit.ST;
        } else if (unitString.equals("RABATT")) {
            return Unit.RABATT;
        } else if (unitString.equals("")) {
            return Unit.EMPTY;
        } else {
            return null;
        }
    }

    private static List<Element> findAllSliderElements(String discountLink) {
        String requestURL = "https://www.ica.se" + discountLink;
        try {
            Document doc = Jsoup.connect(requestURL).maxBodySize(0).get();
            return doc.select("div.hit-item");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private static List<Element> findAllStoreCards(String city) {
        String errName = city;
        city = city.toLowerCase().replaceAll("[åä]", "a").replaceAll("ö", "o").replaceAll(" ", "-");
        String requestURL = "https://www.ica.se/butiker/" + city;
        try {
            Document doc = Jsoup.connect(requestURL).maxBodySize(0).get();
            return doc.select("store-card-list-item:not(.compact)");
        } catch (IOException e) {
            System.err.print("Seems like ICA made a slip, city " + errName + " is missing it's webpage. ");
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, String> extractNameAndLink(List<Element> storeElements) {
        Map<String, String> legitStores = new HashMap<String, String>();
        for (Element e : storeElements) {
            String storeName = e.getElementsByClass("card-heading").text().toLowerCase().replaceAll("(maxi ica)", "ica maxi");
            String discountLink = e.getElementsByClass("store-card-store-link").last().attr("href");
            legitStores.put(storeName, discountLink);
        }
        return legitStores;
    }

    private static void matchStores(List<Store> myStores, Map<String, String> foundStoreNamesWithLink) {
        //Match exact
        Set<String> foundStoreNames = foundStoreNamesWithLink.keySet();
        for (Store myStore : myStores) {
            for (Iterator<String> iterator = foundStoreNames.iterator(); iterator.hasNext(); ) {
                String foundStore = iterator.next();
                if (myStore.getName().equals(foundStore)) {
                    myStore.setDiscountLink(foundStoreNamesWithLink.get(foundStore));
                    iterator.remove();
                    break;
                }
            }
        }

        //Match not so exact
        List<Store> emptyStores = getEmptyStores(myStores);
        for (Store myStore : emptyStores) {
            for (Iterator<String> iterator = foundStoreNames.iterator(); iterator.hasNext(); ) {
                String foundStore = iterator.next();
                int lengthSearch = (myStore.getName().length() > 15 && foundStore.length() > 15) ? 15 : (foundStore.length() < myStore.getName().length()) ? foundStore.length() : myStore.getName().length();
                boolean attempt = false;
                if (myStore.getName().split(" ").length > 2) {
                    attempt = myStore.getName().split(" ")[2].contains(foundStore);
                }
                if (attempt || myStore.getName().substring(0, lengthSearch).equals(foundStore.substring(0, lengthSearch))) {
                    myStore.setDiscountLink(foundStoreNamesWithLink.get(foundStore));
                    iterator.remove();
                    break;
                }
            }
        }

        //Match the rest by distance
        //TODO this is unsafe, maybe add a notification to this
        emptyStores = getEmptyStores(myStores);
        for (Store emptyStore : emptyStores) {
            double bestScore = emptyStore.getName().length();
            String currentWinner = "";
            for (Iterator<String> iterator = foundStoreNames.iterator(); iterator.hasNext(); ) {
                String foundStore = iterator.next();
                double distanceScore = distanceScore(foundStore, emptyStore.getName());
                if (distanceScore < bestScore) {
                    bestScore = distanceScore;
                    currentWinner = foundStore;
                }
            }

            if (bestScore < emptyStore.getName().length()) {
                emptyStore.setDiscountLink(foundStoreNamesWithLink.get(currentWinner));
                foundStoreNames.remove(currentWinner);
            }
        }

    }

    private static double distanceScore(String str1, String str2) {
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
}
