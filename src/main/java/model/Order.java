package model;

import lombok.With;

import java.util.List;

public record Order(
    @With String id,
    @With List<Product> products,
    @With OrderStatus orderStatus
) {
}
