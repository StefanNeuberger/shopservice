package enums;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandsTest {

    @Test
    void getValue_shouldReturnCorrectValue_whenCalled() {
        assertEquals("addOrder", Commands.ADD_ORDER.getValue());
        assertEquals("setStatus", Commands.SET_STATUS.getValue());
        assertEquals("printOrders", Commands.PRINT_ORDERS.getValue());
    }

    @Test
    void isValid_shouldReturnTrue_whenCommandIsValid() {

        List<String> validCommands = List.of("addOrder", "setStatus", "printOrders");
        for (String command : validCommands) {
            assertTrue(Commands.isValid(command));
        }
    }

    @Test
    void isValid_shouldReturnFalse_whenCommandIsInvalid() {
        assertFalse(Commands.isValid("invalid"));
        assertFalse(Commands.isValid(""));
    }

    @Test
    void fromString_shouldReturnCorrectEnumConstant_whenCalledWithValidCommandString() {
        assertEquals(Commands.ADD_ORDER, Commands.fromString("addOrder"));
        assertEquals(Commands.SET_STATUS, Commands.fromString("setStatus"));
        assertEquals(Commands.PRINT_ORDERS, Commands.fromString("printOrders"));
    }

    @Test
    void fromString_shouldThrowIllegalArgumentException_whenCalledWithInvalidCommandString() {
        assertThrows(IllegalArgumentException.class, () -> Commands.fromString("invalid"));
        assertThrows(IllegalArgumentException.class, () -> Commands.fromString(""));
    }
}