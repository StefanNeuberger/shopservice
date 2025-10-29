package enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Commands {
    ADD_ORDER("addOrder"),
    SET_STATUS("setStatus"),
    PRINT_ORDERS("printOrders");

    private final String value;

    Commands(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static final Set<String> VALID_VALUES = Arrays.stream(Commands.values())
            .map(Commands::getValue)
            .collect(Collectors.toSet());

    public static boolean isValid(String command) {
        return VALID_VALUES.contains(command);
    }

    public static Commands fromString(String command) {
        return Arrays.stream(Commands.values())
                .filter(c -> c.value.equals(command))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + command));
    }
}
