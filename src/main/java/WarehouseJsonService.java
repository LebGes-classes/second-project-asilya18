import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WarehouseJsonService {
    private final ObjectMapper objectMapper;

    public WarehouseJsonService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Регистрация модуля для поддержки Java 8 date/time
    }

    // сохранение одного склада
    public void saveWarehouse(Warehouse warehouse, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), warehouse);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения склада: " + e.getMessage(), e);
        }
    }

    // загрузка одного склада
    public Warehouse loadWarehouse(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename), Warehouse.class);
        } catch (IOException e) {
            throw new IOException("ошибка загрузки склада: " + e.getMessage(), e);
        }
    }

    // сохранение списка складов
    public void saveAllWarehouses(List<Warehouse> warehouses, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), warehouses);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения списка складов: " + e.getMessage(), e);
        }
    }

    // загрузка списка складов
    public List<Warehouse> loadAllWarehouses(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Warehouse.class));
        } catch (IOException e) {
            throw new IOException("ошибка загрузки списка складов: " + e.getMessage(), e);
        }
    }
}
