package repository;

import enums.OrderStatus;
import model.Order;

import java.util.*;

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
