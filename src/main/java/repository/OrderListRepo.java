package repository;

import enums.OrderStatus;
import model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderListRepo implements OrderRepo {
    private List<Order> orders = new ArrayList<>();

    public List<Order> getOrders() {
        return orders;
    }

    public Order getOrderById(String id) {
        for (Order order : orders) {
            if (order.id().equals(id)) {
                return order;
            }
        }
        return null;
    }

    public Order addOrder(Order newOrder) {
        orders.add(newOrder);
        return newOrder;
    }

    public void removeOrder(String id) {
        for (Order order : orders) {
            if (order.id().equals(id)) {
                orders.remove(order);
                return;
            }
        }
    }

    @Override
    public Optional<Order> updateOrder(String id, OrderStatus orderStatus) {
        for (Order order : orders) {
            if (order.id().equals(id)) {
                Order updatedOrder = order.withOrderStatus(orderStatus);
                int index = orders.indexOf(order);
                orders.set(index, updatedOrder);
                return Optional.of(updatedOrder);
            }
        }
        return Optional.empty();
    }
}
