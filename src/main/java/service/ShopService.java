package service;

import enums.Commands;
import enums.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import model.Order;
import model.Product;
import repository.IdGeneratorRepository;
import repository.OrderRepo;
import repository.ProductRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        List<Order> allOrders = orderRepo.getOrders();
        Map<OrderStatus, Order> oldestOrderPerStatus = new HashMap<>();
        for (OrderStatus orderStatus : OrderStatus.values()) {
            List<Order> ordersOfStatus = getOrdersByOrderStatus(orderStatus);
            Order oldestOrder = ordersOfStatus.stream().sorted(Comparator.comparing(Order::orderedAt)).findFirst().orElse(null);
            oldestOrderPerStatus.put(orderStatus, oldestOrder);
        }
        return oldestOrderPerStatus;
    }

    public void processCommandsFromFile() {
        Path filePath = Path.of("src/main/resources/transactions.txt");
        Map<String, Order> ordersByName = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                try {
                    processLine(line, ordersByName);
                } catch (Exception e) {
                    System.err.println("Error processing line: " + line);
                    System.err.println("  Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
        }
    }

    private void processLine(String line, Map<String, Order> ordersByName) {
        String[] parts = line.split(" ");

        // Early validation
        if (parts.length == 0) return; // Empty line

        String commandStr = parts[0];

        if (!Commands.isValid(commandStr)) {
            throw new IllegalArgumentException("Invalid command: " + commandStr);
        }

        // Convert to enum for type-safe switching
        Commands command = Commands.fromString(commandStr);

        // Use enum in switch - type-safe and no magic strings!
        switch (command) {
            case ADD_ORDER -> handleAddOrder(parts, ordersByName);
            case SET_STATUS -> handleSetStatus(parts, ordersByName);
            case PRINT_ORDERS -> handlePrintOrders(ordersByName);
        }
    }

    private void handleAddOrder(String[] parts, Map<String, Order> ordersByName) {
        // Format: addOrder A 1 2 3
        if (parts.length < 3) {
            throw new IllegalArgumentException("addOrder requires at least: orderName and one product");
        }

        // get orderName
        String orderName = parts[1];

        // get productIds
        List<String> productIds = List.of(parts).subList(2, parts.length);

        // create order
        Order order = addOrder(productIds);

        // save order to map
        ordersByName.put(orderName, order);
        System.out.println("Created order " + orderName + " with " + order.id());
    }

    private void handleSetStatus(String[] parts, Map<String, Order> ordersByName) {
        // Format: setStatus A COMPLETED
        if (parts.length < 3) {
            throw new IllegalArgumentException("setStatus requires: orderName and status");
        }

        String orderName = parts[1];
        String statusStr = parts[2];

        // Check if status is valid
        if (!OrderStatus.isValid(statusStr)) {
            throw new IllegalArgumentException("Invalid status: " + statusStr);
        }

        Order order = ordersByName.get(orderName);
        if (order == null) {
            throw new NoSuchFieldError("Order with name: " + orderName + " not found!");
        }

        OrderStatus status = OrderStatus.valueOf(statusStr);

        // Update order
        updateOrder(order.id(), status);
        System.out.println("Updated order " + orderName + " to status: " + statusStr);
    }

    private void handlePrintOrders(Map<String, Order> ordersByName) {
        System.out.println("\nAll orders:");
        ordersByName.forEach((name, order) ->
                System.out.println(name + ": " + order)
        );
    }
}
