import model.Product;
import org.junit.jupiter.api.BeforeEach;
import repository.ProductRepo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductRepoTest {

    private ProductRepo repo;

    @BeforeEach
    void setUp() {
        repo = new ProductRepo();
        Product product1 = new Product("1", "Apfel");
        repo.addProduct(product1);
    }

    @org.junit.jupiter.api.Test
    void getProducts() {
        //GIVEN

        //WHEN
        List<Product> actual = repo.getProducts();

        //THEN
        List<Product> expected = new ArrayList<>();
        expected.add(new Product("1", "Apfel"));
        assertEquals(actual, expected);
    }

    @org.junit.jupiter.api.Test
    void getProductById() {
        //GIVEN

        //WHEN
        Product actual = repo.getProductById("1").orElse(null);

        //THEN
        Product expected = new Product("1", "Apfel");
        assertEquals(actual, expected);
    }

    @org.junit.jupiter.api.Test
    void addProduct() {
        //GIVEN
        Product newProduct = new Product("2", "Banane");

        //WHEN
        Product actual = repo.addProduct(newProduct);

        //THEN
        Product expected = new Product("2", "Banane");
        assertEquals(actual, expected);
        assertEquals(repo.getProductById("2").orElse(null), expected);
    }

    @org.junit.jupiter.api.Test
    void removeProduct() {
        //GIVEN

        //WHEN
        repo.removeProduct("1");

        //THEN
        assertTrue(repo.getProductById("1").isEmpty());
    }
}
