import java.util.List;

/**
 * Created by Simon on 2017-06-24.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        StoreScanner storeScanner = new StoreScanner();
        List<Store> stores = storeScanner.getStoresInRadius(2000);
        for (Store store : stores) {
            System.out.println(store.getName());
            System.out.println("DISTANCE FROM YOU: " + store.getDistanceToUser());
        }
    }
}
