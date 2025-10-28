import lombok.RequiredArgsConstructor;
import model.Order;
import model.OrderStatus;
import model.Product;
import repository.OrderRepo;
import repository.ProductRepo;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ShopService {

    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;


    public Order addOrder(List<String> productIds) {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            var productOpt = productRepo.getProductById(productId);
            if (productOpt.isEmpty()) {
                System.out.println("model.Product mit der Id: " + productId + " konnte nicht bestellt werden!");
                throw new NoSuchFieldError("Product with id: " + productId + " could not be ordered. Product not found.");
            }
            products.add(productOpt.get());
        }

        Order newOrder = new Order(UUID.randomUUID().toString(), products, OrderStatus.PROCESSING, ZonedDateTime.now());

        return orderRepo.addOrder(newOrder);
    }

    public List<Order> getOrders() {
        return orderRepo.getOrders();
    }

    public Order updateOrder(String orderId, OrderStatus orderStatus) {
        var updatedOpt = orderRepo.updateOrder(orderId, orderStatus);
        if (updatedOpt.isEmpty()) {
            throw new IllegalArgumentException("Order with id: " + orderId + " not found.");
        }
        return updatedOpt.get();
    }

    public List<Order> getOrdersByOrderStatus(OrderStatus orderStatus) {
        return orderRepo.getOrders().stream().filter(o -> o.orderStatus().equals(orderStatus)).toList();
    }
}
