import model.Order;
import model.OrderStatus;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.OrderMapRepo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapRepoTest {

    private OrderMapRepo repo = new OrderMapRepo();

    @BeforeEach
    void setUp() {
        repo = new OrderMapRepo();
    }

    @Test
    void getOrders() {
        //GIVEN
        

        Product product = new Product("1", "Apfel");
        Order newOrder = new Order("1", List.of(product), OrderStatus.PROCESSING);
        repo.addOrder(newOrder);

        //WHEN
        List<Order> actual = repo.getOrders();

        //THEN
        List<Order> expected = new ArrayList<>();
        Product product1 = new Product("1", "Apfel");
        expected.add(new Order("1", List.of(product1), OrderStatus.PROCESSING));

        assertEquals(actual, expected);
    }

    @Test
    void getOrderById() {
        //GIVEN
        

        Product product = new Product("1", "Apfel");
        Order newOrder = new Order("1", List.of(product), OrderStatus.PROCESSING);
        repo.addOrder(newOrder);

        //WHEN
        Order actual = repo.getOrderById("1");

        //THEN
        Product product1 = new Product("1", "Apfel");
        Order expected = new Order("1", List.of(product1), OrderStatus.PROCESSING);

        assertEquals(actual, expected);
    }

    @Test
    void addOrder() {
        //GIVEN
        
        Product product = new Product("1", "Apfel");
        Order newOrder = new Order("1", List.of(product), OrderStatus.PROCESSING);

        //WHEN
        Order actual = repo.addOrder(newOrder);

        //THEN
        Product product1 = new Product("1", "Apfel");
        Order expected = new Order("1", List.of(product1), OrderStatus.PROCESSING);
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
        Product product = new Product("1", "Apfel");
        Order originalOrder = new Order("1", List.of(product), OrderStatus.PROCESSING);
        repo.addOrder(originalOrder);

        //WHEN
        var result = repo.updateOrder("1", OrderStatus.COMPLETED);

        //THEN
        assertTrue(result.isPresent());
        assertEquals(OrderStatus.COMPLETED, result.get().orderStatus());
        assertEquals("1", result.get().id());
        assertEquals(originalOrder.products(), result.get().products());
    }

    @Test
    void updateOrder_whenOrderExists_shouldUpdateInRepository() {
        //GIVEN 
        Product product = new Product("1", "Apfel");
        Order originalOrder = new Order("1", List.of(product), OrderStatus.PROCESSING);
        repo.addOrder(originalOrder);

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
