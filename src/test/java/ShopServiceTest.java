import model.Order;
import model.OrderStatus;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    private ShopService shopService;

    @BeforeEach
    void setUp() {
        shopService = new ShopService();
    }

    @Test

    void addOrderTest() {
        //GIVEN
        List<String> productsIds = List.of("1");

        //WHEN
        Order actual = shopService.addOrder(productsIds);

        //THEN
        Order expected = new Order("-1", List.of(new Product("1", "Apfel")), OrderStatus.PROCESSING);
        assertEquals(expected.products(), actual.products());
        assertNotNull(expected.id());
    }

    @Test
    void addOrderTest_whenInvalidProductId_expectNull() {
        //GIVEN
        List<String> productsIds = List.of("1", "2");

        //WHEN
        Order actual = shopService.addOrder(productsIds);

        //THEN
        assertNull(actual);
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

}
