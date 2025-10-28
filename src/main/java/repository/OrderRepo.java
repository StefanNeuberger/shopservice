package repository;

import model.Order;
import model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepo {

    List<Order> getOrders();

    Order getOrderById(String id);

    Order addOrder(Order newOrder);

    void removeOrder(String id);

    Optional<Order> updateOrder(String orderId, OrderStatus orderStatus);
}
