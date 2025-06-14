import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProductJsonService {
    private final ObjectMapper objectMapper;

    public ProductJsonService() {
        this.objectMapper = new ObjectMapper();
    }

    // сохранение одного товара
    public void saveProduct(Product product, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), product);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения товара в JSON: " + e.getMessage(), e);
        }
    }

    // загрузка одного товара
    public Product loadProduct(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename), Product.class);
        } catch (IOException e) {
            throw new IOException("ошибка загрузки товара из JSON: " + e.getMessage(), e);
        }
    }

    // сохранение списка товаров
    public void saveAllProducts(List<Product> products, String filename) throws IOException {
        try {
            System.out.println("Сохраняем " + products.size() + " продуктов в файл: " + filename);
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(products);
            System.out.println("Сериализованный JSON: " + json); // Выводим сериализованный JSON
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), products);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения списка товаров: " + e.getMessage(), e);
        }
    }

    // загрузка списка товаров
    public List<Product> loadAllProducts(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));
        } catch (IOException e) {
            throw new IOException("ошибка загрузки списка товаров: " + e.getMessage(), e);
        }
    }
}