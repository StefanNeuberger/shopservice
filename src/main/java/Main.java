import model.Order;
import model.OrderStatus;
import model.Product;
import repository.*;
import service.ShopService;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        ProductRepo productRepo = new ProductRepo();
        OrderRepo orderRepo = new OrderMapRepo();
        IdGeneratorRepository idGeneratorRepository = new StringIdGeneratorRepo();

        // create new products
        productRepo.addProduct(new Product("1", "Banana"));
        productRepo.addProduct(new Product("2", "Kiwi"));
        productRepo.addProduct(new Product("3", "Pear"));
        productRepo.addProduct(new Product("4", "Orange"));

        ShopService shopService = new ShopService(productRepo, orderRepo, idGeneratorRepository);

        // create new orders
        Order order1 = shopService.addOrder(List.of("1", "2", "3"));
        shopService.addOrder(List.of("2", "3"));
        Order order2 = shopService.addOrder(List.of("1", "3"));
        Order order3 = shopService.addOrder(List.of("1", "3"));
        Order order4 = shopService.addOrder(List.of("1", "3"));
        shopService.addOrder(List.of("1"));

        // update order status
        shopService.updateOrder(order1.id(), OrderStatus.IN_DELIVERY);
        shopService.updateOrder(order2.id(), OrderStatus.COMPLETED);
        shopService.updateOrder(order3.id(), OrderStatus.COMPLETED);
        shopService.updateOrder(order4.id(), OrderStatus.COMPLETED);

        Map<OrderStatus, Order> oldestOrderPerStatus = shopService.getOldestOrderPerStatus();
        System.out.println();
        System.out.println(oldestOrderPerStatus);


        System.out.println();
        System.out.println(shopService.getOrders());
    }
}
