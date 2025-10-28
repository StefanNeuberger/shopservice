package enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum OrderStatus {
    PROCESSING,
    IN_DELIVERY,
    COMPLETED;

    private static final Set<String> VALID_VALUES = Arrays.stream(OrderStatus.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    public static void print() {
        System.out.println(VALID_VALUES);
    }

    public static boolean isValid(String status) {
        return VALID_VALUES.contains(status);
    }
}
