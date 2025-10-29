package model;

import enums.OrderStatus;
import lombok.With;

import java.time.ZonedDateTime;
import java.util.List;

public record Order(
        @With String id,
        @With List<Product> products,
        @With OrderStatus orderStatus,
        ZonedDateTime orderedAt
) {
}
