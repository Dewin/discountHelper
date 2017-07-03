import java.util.List;

/**
 * Created by Simon on 2017-06-24.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        StoreScanner storeScanner = new StoreScanner("79.142.208.0");
        List<Store> stores = storeScanner.getStoresInRadius(2900);
        System.out.println(storeScanner.getLocation());
        for (Store store : stores) {
            System.out.println(store.getName());
            System.out.println(store.getDiscountLink());
            System.out.println("DISTANCE FROM YOU: " + store.getDistanceToUser());
        }
    }
}
