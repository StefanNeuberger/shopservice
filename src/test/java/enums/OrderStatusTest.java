package enums;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderStatusTest {

    @Test
    void isValid_shouldReturnTrue_whenGivenStatusStringIsValid() {
        List<String> validStatusesStringRepresentations = List.of(OrderStatus.PROCESSING.name(), OrderStatus.COMPLETED.name(), OrderStatus.IN_DELIVERY.name());
        for (String statusString : validStatusesStringRepresentations) {
            assertTrue(OrderStatus.isValid(statusString));

        }
    }

    @Test
    void isValid_shouldReturnFalse_whenGivenStatusStringIsNotValid() {
        assertFalse(OrderStatus.isValid("invalid"));
        assertFalse(OrderStatus.isValid(""));
    }
}