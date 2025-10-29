import enums.OrderStatus;
import model.Order;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.*;
import service.ShopService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    private ShopService shopService;

    @BeforeEach
    void setUp() {
        ProductRepo productRepo = new ProductRepo();
        OrderRepo orderRepo = new OrderMapRepo();
        IdGeneratorRepository idGeneratorRepository = new StringIdGeneratorRepo();
        productRepo.addProduct(new Product("1", "Apfel"));
        shopService = new ShopService(productRepo, orderRepo, idGeneratorRepository);

    }

    @Test
    void addOrderTest() {
        //GIVEN
        List<String> productsIds = List.of("1");

        //WHEN
        Order actual = shopService.addOrder(productsIds);

        //THEN
        assertEquals(List.of(new Product("1", "Apfel")), actual.products());
        assertEquals(OrderStatus.PROCESSING, actual.orderStatus());
        assertNotNull(actual.id());
        assertNotNull(actual.orderedAt());
    }

    @Test
    void addOrderTest_whenInvalidProductId_expectNoSuchFieldError() {
        //GIVEN
        List<String> productsIds = List.of("1", "2");

        assertThrows(NoSuchFieldError.class, () -> shopService.addOrder(productsIds));
    }

    @Test
    void getOrdersByOrderStatus_returnsOrderList_whenOrderWithStatusExists() {
        shopService.addOrder(List.of("1"));

        List<Order> foundOrders = shopService.getOrdersByOrderStatus(OrderStatus.PROCESSING);

        assertEquals(1, foundOrders.size());
        assertEquals(OrderStatus.PROCESSING, foundOrders.get(0).orderStatus());
    }

    @Test
    void getOrdersByOrderStatus_returnsEmptyList_whenOrderWithStatusNotExists() {
        shopService.addOrder(List.of("1"));

        List<Order> foundOrders = shopService.getOrdersByOrderStatus(OrderStatus.COMPLETED);

        assertEquals(0, foundOrders.size());
    }

    @Test
    void getOrdersByOrderStatus_returnsEmptyList_whenNoOrdersExists() {

        List<Order> foundOrders = shopService.getOrdersByOrderStatus(OrderStatus.COMPLETED);

        assertEquals(0, foundOrders.size());
    }

    @Test
    void updateOrder_whenOrderExists_shouldReturnUpdatedOrder() {
        //GIVEN
        Order order = shopService.addOrder(List.of("1"));

        //WHEN
        Order updated = shopService.updateOrder(order.id(), OrderStatus.COMPLETED);

        //THEN
        assertEquals(OrderStatus.COMPLETED, updated.orderStatus());
        assertEquals(order.id(), updated.id());
        assertEquals(order.products(), updated.products());
    }

    @Test
    void updateOrder_whenOrderExists_shouldUpdateInRepository() {
        //GIVEN
        Order order = shopService.addOrder(List.of("1"));

        //WHEN
        shopService.updateOrder(order.id(), OrderStatus.COMPLETED);

        //THEN
        List<Order> completedOrders = shopService.getOrdersByOrderStatus(OrderStatus.COMPLETED);
        assertEquals(1, completedOrders.size());
        assertEquals(order.id(), completedOrders.get(0).id());
    }

    @Test
    void updateOrder_whenOrderNotExists_shouldThrowException() {
        //GIVEN
        String nonExistentId = "999";

        //WHEN & THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> shopService.updateOrder(nonExistentId, OrderStatus.COMPLETED)
        );
    }


    @Test
    void updateOrder_whenMultipleOrders_shouldUpdateCorrectOrder() {
        //GIVEN
        Order order1 = shopService.addOrder(List.of("1"));
        Order order2 = shopService.addOrder(List.of("1"));

        //WHEN
        shopService.updateOrder(order1.id(), OrderStatus.COMPLETED);

        //THEN
        List<Order> completedOrders = shopService.getOrdersByOrderStatus(OrderStatus.COMPLETED);
        List<Order> processingOrders = shopService.getOrdersByOrderStatus(OrderStatus.PROCESSING);

        assertEquals(1, completedOrders.size());
        assertEquals(order1.id(), completedOrders.get(0).id());
        assertEquals(1, processingOrders.size());
        assertEquals(order2.id(), processingOrders.get(0).id());
    }

    @Test
    void getOldestOrderPerStatus_returnsMapWithNullValues_whenNoOrdersExists() {
        Map<OrderStatus, Order> oldestOrderPerStatus = shopService.getOldestOrderPerStatus();
        System.out.println("oldestOrderPerStatus!!!" + oldestOrderPerStatus);
        assertEquals(OrderStatus.values().length, oldestOrderPerStatus.size());
        for (OrderStatus orderStatus : OrderStatus.values()) {
            assertNull(oldestOrderPerStatus.get(orderStatus));
        }
    }

    @Test
    void getOldestOrderPerStatus_returnsMapWithCorrectValues_whenOrdersExists() {
        Order oldestOrder = shopService.addOrder(List.of("1"));
        shopService.getProductRepo().addProduct(new Product("2", "Birne"));
        shopService.addOrder(List.of("2"));

        Map<OrderStatus, Order> oldestOrderPerStatus = shopService.getOldestOrderPerStatus();
        assertEquals(OrderStatus.values().length, oldestOrderPerStatus.size());
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus == OrderStatus.PROCESSING) {
                assertEquals(OrderStatus.PROCESSING, oldestOrderPerStatus.get(orderStatus).orderStatus());
                assertEquals(oldestOrder.id(), oldestOrderPerStatus.get(orderStatus).id());
            } else {
                assertNull(oldestOrderPerStatus.get(orderStatus));
            }
        }
    }

    @Test
    void processCommandsFromFile_shouldThrowIOException_whenFileDoesNotExist() throws Exception {
        Path nonExistentFile = Path.of("nonExistentFile");
        assertThrows(IOException.class, () -> shopService.processCommandsFromFile(nonExistentFile));
    }


    @Test
    void processCommandsFromFile_shouldProcessSuccessfully_whenFileIsEmpty() throws IOException {
        //GIVEN
        // Create temporary empty file
        Path emptyFile = Files.createTempFile("test-empty", ".txt");

        try {
            //WHEN
            shopService.processCommandsFromFile(emptyFile);

            //THEN
            // Verify no orders were processed (service should still work)
            List<Order> allOrders = shopService.getOrders();
            assertEquals(0, allOrders.size(), "No orders should be created from empty file");
        } finally {
            Files.deleteIfExists(emptyFile); // Clean up
        }
    }

    @Test
    void processCommandsFromFile_shouldCreateOrder_whenAddOrderCommandIsValid() throws IOException {
        //GIVEN
        Path testFile = Files.createTempFile("test-addOrder", ".txt");
        Files.writeString(testFile, "addOrder A 1");

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> allOrders = shopService.getOrders();
            assertEquals(1, allOrders.size());
            assertEquals(OrderStatus.PROCESSING, allOrders.get(0).orderStatus());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void processCommandsFromFile_shouldCreateMultipleOrders_whenMultipleAddOrderCommands() throws IOException {
        //GIVEN
        Path testFile = Files.createTempFile("test-multiple", ".txt");
        Files.writeString(testFile, "addOrder A 1\naddOrder B 1");

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> allOrders = shopService.getOrders();
            assertEquals(2, allOrders.size());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void processCommandsFromFile_shouldUpdateOrderStatus_whenSetStatusCommandIsValid() throws IOException {
        //GIVEN
        Path testFile = Files.createTempFile("test-setStatus", ".txt");
        Files.writeString(testFile, "addOrder A 1\nsetStatus A COMPLETED");

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> completedOrders = shopService.getOrdersByOrderStatus(OrderStatus.COMPLETED);
            assertEquals(1, completedOrders.size());
            assertEquals(OrderStatus.COMPLETED, completedOrders.get(0).orderStatus());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void processCommandsFromFile_shouldContinueProcessing_whenInvalidCommand() throws IOException {
        //GIVEN
        Path testFile = Files.createTempFile("test-invalid", ".txt");
        Files.writeString(testFile, "invalidCommand A 1\naddOrder B 1");

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> allOrders = shopService.getOrders();
            assertEquals(1, allOrders.size());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void processCommandsFromFile_shouldContinueProcessing_whenInvalidProductId() throws IOException {
        //GIVEN
        Path testFile = Files.createTempFile("test-invalid-product", ".txt");
        Files.writeString(testFile, "addOrder A 999\naddOrder B 1");

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> allOrders = shopService.getOrders();
            assertEquals(1, allOrders.size());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void processCommandsFromFile_shouldContinueProcessing_whenInvalidStatus() throws IOException {
        //GIVEN
        Path testFile = Files.createTempFile("test-invalid-status", ".txt");
        Files.writeString(testFile, "addOrder A 1\nsetStatus A INVALID_STATUS");

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> processingOrders = shopService.getOrdersByOrderStatus(OrderStatus.PROCESSING);
            assertEquals(1, processingOrders.size());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void processCommandsFromFile_shouldContinueProcessing_whenSetStatusForNonExistentOrder() throws IOException {
        //GIVEN
        Path testFile = Files.createTempFile("test-nonexistent-order", ".txt");
        Files.writeString(testFile, "setStatus NONEXISTENT COMPLETED\naddOrder A 1");

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> allOrders = shopService.getOrders();
            assertEquals(1, allOrders.size());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void processCommandsFromFile_shouldIgnoreEmptyLines() throws IOException {
        //GIVEN
        Path testFile = Files.createTempFile("test-empty-lines", ".txt");
        Files.writeString(testFile, "addOrder A 1\n\naddOrder B 1\n   \n");

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> allOrders = shopService.getOrders();
            assertEquals(2, allOrders.size());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void processCommandsFromFile_shouldHandleCompleteWorkflow() throws IOException {
        //GIVEN
        Path testFile = Files.createTempFile("test-workflow", ".txt");
        Files.writeString(testFile, 
            "addOrder A 1\n" +
            "addOrder B 1\n" +
            "setStatus A COMPLETED\n" +
            "setStatus B IN_DELIVERY\n" +
            "printOrders"
        );

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> completedOrders = shopService.getOrdersByOrderStatus(OrderStatus.COMPLETED);
            List<Order> inDeliveryOrders = shopService.getOrdersByOrderStatus(OrderStatus.IN_DELIVERY);
            
            assertEquals(1, completedOrders.size());
            assertEquals(1, inDeliveryOrders.size());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void processCommandsFromFile_shouldContinueProcessing_whenAddOrderHasNoProductIds() throws IOException {
        //GIVEN
        Path testFile = Files.createTempFile("test-insufficient-args", ".txt");
        Files.writeString(testFile, "addOrder\naddOrder B 1");

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> allOrders = shopService.getOrders();
            assertEquals(1, allOrders.size());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void processCommandsFromFile_shouldHandleMultipleProductsInOrder() throws IOException {
        //GIVEN
        // Add more products for this test
        shopService.getProductRepo().addProduct(new Product("2", "Banane"));
        shopService.getProductRepo().addProduct(new Product("3", "Kiwi"));
        
        Path testFile = Files.createTempFile("test-multiple-products", ".txt");
        Files.writeString(testFile, "addOrder A 1 2 3");

        try {
            //WHEN
            shopService.processCommandsFromFile(testFile);

            //THEN
            List<Order> allOrders = shopService.getOrders();
            assertEquals(1, allOrders.size());
            assertEquals(3, allOrders.get(0).products().size());
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

}
