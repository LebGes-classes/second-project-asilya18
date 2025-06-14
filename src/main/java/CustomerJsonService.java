import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CustomerJsonService {
    private final ObjectMapper objectMapper;

    public CustomerJsonService() {
        this.objectMapper = new ObjectMapper();
    }

    // сохранение одного клиента
    public void saveCustomer(Customer customer, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), customer);
        } catch (IOException e) {
            throw new IOException("oшибка сохранения клиента: " + e.getMessage(), e);
        }
    }

    // загрузка одного клиента
    public Customer loadCustomer(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename), Customer.class);
        } catch (IOException e) {
            throw new IOException("ошибка загрузки клиента: " + e.getMessage(), e);
        }
    }

    // сохранение списка клиентов
    public void saveAllCustomers(List<Customer> customers, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), customers);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения списка клиентов: " + e.getMessage(), e);
        }
    }

    // загрузка списка клиентов
    public List<Customer> loadAllCustomers(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Customer.class));
        } catch (IOException e) {
            throw new IOException("ошибка загрузки списка клиентов: " + e.getMessage(), e);
        }
    }
}
