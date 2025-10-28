package service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import model.Order;
import model.OrderStatus;
import model.Product;
import repository.IdGeneratorRepository;
import repository.OrderRepo;
import repository.ProductRepo;

import java.time.ZonedDateTime;
import java.util.*;

@RequiredArgsConstructor
@Data
public class ShopService {

    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final IdGeneratorRepository idGeneratorRepository;


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

        Order newOrder = new Order(idGeneratorRepository.generateId(), products, OrderStatus.PROCESSING, ZonedDateTime.now());

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

    public Map<OrderStatus, Order> getOldestOrderPerStatus() {
        Map<OrderStatus, Order> oldestOrderPerStatus = new HashMap<>();
        for (OrderStatus orderStatus : OrderStatus.values()) {
            List<Order> ordersOfStatus = getOrdersByOrderStatus(orderStatus);
            
            // Find oldest order (earliest orderedAt) or null if none exist
            Order oldestOrder = ordersOfStatus.stream()
                    .min(Comparator.comparing(Order::orderedAt))
                    .orElse(null);
            
            oldestOrderPerStatus.put(orderStatus, oldestOrder);
        }
        return oldestOrderPerStatus;
    }
}
