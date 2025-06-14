import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class EmployeeJsonService {
    private final ObjectMapper objectMapper;

    public EmployeeJsonService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // для корректной работы с LocalDate
    }

    // сохранение одного сотрудника
    public void saveEmployee(Employee employee, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), employee);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения сотрудника в JSON", e);
        }
    }

    // загрузка одного сотрудника
    public Employee loadEmployee(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename), Employee.class);
        } catch (IOException e) {
            throw new IOException("ошибка загрузки сотрудника из JSON", e);
        }
    }

    // сохранение списка сотрудников
    public void saveAllEmployees(List<Employee> employees, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), employees);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения списка сотрудников", e);
        }
    }

    // загрузка списка сотрудников
    public List<Employee> loadAllEmployees(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Employee.class));
        } catch (IOException e) {
            throw new IOException("ошибка загрузки списка сотрудников", e);
        }
    }
}