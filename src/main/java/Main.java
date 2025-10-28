import model.Product;
import repository.OrderMapRepo;
import repository.OrderRepo;
import repository.ProductRepo;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        ProductRepo productRepo = new ProductRepo();
        OrderRepo orderRepo = new OrderMapRepo();

        // create new products
        productRepo.addProduct(new Product("1", "Banana"));
        productRepo.addProduct(new Product("2", "Kiwi"));
        productRepo.addProduct(new Product("3", "Banana"));

        ShopService shopService = new ShopService(productRepo, orderRepo);

        // create new orders
        shopService.addOrder(List.of("1", "2", "3"));
        shopService.addOrder(List.of("2", "3"));
        shopService.addOrder(List.of("1", "3"));
        shopService.addOrder(List.of("1"));

    }
}
