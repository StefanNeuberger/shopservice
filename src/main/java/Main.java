import enums.Commands;
import enums.OrderStatus;
import model.Order;
import model.Product;
import repository.*;
import service.ShopService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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

        // Read from file
        try {
            processCommandsFromFile(shopService);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void processCommandsFromFile(ShopService shopService) {
        Path filePath = Path.of("src/main/resources/transactions.txt");
        Map<String, Order> ordersByName = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                try {
                    processLine(line, shopService, ordersByName);
                } catch (Exception e) {
                    System.err.println("Error processing line: " + line);
                    System.err.println("  Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
        }
    }

    private static void processLine(String line, ShopService shopService, Map<String, Order> ordersByName) {
        String[] parts = line.split(" ");

        // Early validation
        if (parts.length == 0) return; // Empty line

        String command = parts[0];

        if (!Commands.isValid(command)) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }

        // Use switch for better performance and readability
        switch (command) {
            case "addOrder" -> handleAddOrder(parts, shopService, ordersByName);
            case "setStatus" -> handleSetStatus(parts, shopService, ordersByName);
            case "printOrder" -> handlePrintOrders(ordersByName);
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private static void handleAddOrder(String[] parts, ShopService shopService, Map<String, Order> ordersByName) {
        // Format: addOrder A 1 2 3
        if (parts.length < 3) {
            throw new IllegalArgumentException("addOrder requires at least: orderName and one product");
        }

        // get orderName
        String orderName = parts[1];

        // get productIds
        List<String> productIds = List.of(parts).subList(2, parts.length);

        // create order
        Order order = shopService.addOrder(productIds);

        // save order to map
        ordersByName.put(orderName, order);
        System.out.println("Created order " + orderName + " with " + order.id());
    }

    private static void handleSetStatus(String[] parts, ShopService shopService, Map<String, Order> ordersByName) {
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
        shopService.updateOrder(order.id(), status);
        System.out.println("Updated order " + orderName + " to status: " + statusStr);
    }

    private static void handlePrintOrders(Map<String, Order> ordersByName) {
        System.out.println("\nAll orders:");
        ordersByName.forEach((name, order) ->
                System.out.println(name + ": " + order)
        );
    }
}
