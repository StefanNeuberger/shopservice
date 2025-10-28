import enums.OrderStatus;
import model.Order;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.OrderMapRepo;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapRepoTest {

    private static final ZonedDateTime FIXED_TIME = ZonedDateTime.parse("2025-01-01T10:00:00+01:00[Europe/Berlin]");

    private OrderMapRepo repo = new OrderMapRepo();
    private Order newOrder;

    @BeforeEach
    void setUp() {
        Product appleProduct = new Product("1", "Apfel");
        newOrder = new Order("1", List.of(appleProduct), OrderStatus.PROCESSING, FIXED_TIME);
    }

    @Test
    void getOrders() {
        //GIVEN

        repo.addOrder(newOrder);

        //WHEN
        List<Order> actual = repo.getOrders();

        //THEN
        List<Order> expected = new ArrayList<>();
        Product product1 = new Product("1", "Apfel");
        expected.add(new Order("1", List.of(product1), OrderStatus.PROCESSING, FIXED_TIME));

        assertEquals(actual, expected);
    }

    @Test
    void getOrderById() {
        //GIVEN
        repo.addOrder(newOrder);

        //WHEN
        Order actual = repo.getOrderById("1");

        //THEN
        Product product1 = new Product("1", "Apfel");
        Order expected = new Order("1", List.of(product1), OrderStatus.PROCESSING, FIXED_TIME);

        assertEquals(actual, expected);
    }

    @Test
    void addOrder() {
        //GIVEN

        //WHEN
        Order actual = repo.addOrder(newOrder);

        //THEN
        Product product1 = new Product("1", "Apfel");
        Order expected = new Order("1", List.of(product1), OrderStatus.PROCESSING, FIXED_TIME);
        assertEquals(actual, expected);
        assertEquals(repo.getOrderById("1"), expected);
    }

    @Test
    void removeOrder() {
        //GIVEN


        //WHEN
        repo.removeOrder("1");

        //THEN
        assertNull(repo.getOrderById("1"));
    }

    @Test
    void updateOrder_whenOrderExists_shouldReturnUpdatedOrder() {
        //GIVEN
        repo.addOrder(newOrder);

        //WHEN
        var result = repo.updateOrder("1", OrderStatus.COMPLETED);

        //THEN
        assertTrue(result.isPresent());
        assertEquals(OrderStatus.COMPLETED, result.get().orderStatus());
        assertEquals("1", result.get().id());
        assertEquals(newOrder.products(), result.get().products());
    }

    @Test
    void updateOrder_whenOrderExists_shouldUpdateInRepository() {
        //GIVEN
        repo.addOrder(newOrder);

        //WHEN
        repo.updateOrder("1", OrderStatus.COMPLETED);

        //THEN
        Order retrievedOrder = repo.getOrderById("1");
        assertEquals(OrderStatus.COMPLETED, retrievedOrder.orderStatus());
    }

    @Test
    void updateOrder_whenOrderNotExists_shouldReturnEmpty() {
        //WHEN
        var result = repo.updateOrder("999", OrderStatus.COMPLETED);

        //THEN
        assertTrue(result.isEmpty());
    }

}
