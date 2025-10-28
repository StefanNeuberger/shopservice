import model.Order;
import model.OrderStatus;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.*;
import service.ShopService;

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
        shopService.addOrder(List.of("1"));

    }

}
