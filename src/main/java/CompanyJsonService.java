import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CompanyJsonService {
    private final ObjectMapper objectMapper;

    public CompanyJsonService() {
        this.objectMapper = new ObjectMapper();
    }

    // сохранение компании
    public void saveCompany(Company company, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), company);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения компании: " + e.getMessage(), e);
        }
    }

    // загрузка компании
    public Company loadCompany(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename), Company.class);
        } catch (IOException e) {
            throw new IOException("ошибка загрузки компании: " + e.getMessage(), e);
        }
    }

    // сохранение списка компаний
    public void saveAllCompanies(List<Company> companies, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), companies);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения списка компаний: " + e.getMessage(), e);
        }
    }

    // загрузка списка компаний
    public List<Company> loadAllCompanies(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Company.class));
        } catch (IOException e) {
            throw new IOException("ошибка загрузки списка компаний: " + e.getMessage(), e);
        }
    }
}
