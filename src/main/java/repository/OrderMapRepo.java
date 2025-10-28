package repository;

import model.Order;
import model.OrderStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderMapRepo implements OrderRepo {
    private Map<String, Order> orders = new HashMap<>();

    @Override
    public List<Order> getOrders() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Order getOrderById(String id) {
        return orders.get(id);
    }

    @Override
    public Order addOrder(Order newOrder) {
        orders.put(newOrder.id(), newOrder);
        return newOrder;
    }

    @Override
    public void removeOrder(String id) {
        orders.remove(id);
    }

    @Override
    public Optional<Order> updateOrder(String id, OrderStatus orderStatus) {
        Order existingOrder = orders.get(id);
        if (existingOrder != null) {
            Order updatedOrder = existingOrder.withOrderStatus(orderStatus);
            orders.put(id, updatedOrder);
            return Optional.of(updatedOrder);
        }
        return Optional.empty();
    }
}
