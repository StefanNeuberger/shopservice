import java.util.List;

//so

public record Order(
        String id,
        List<Product> products
) {
}
