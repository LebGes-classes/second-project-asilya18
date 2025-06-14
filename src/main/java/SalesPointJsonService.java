import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SalesPointJsonService {
    private final ObjectMapper objectMapper;

    public SalesPointJsonService() {
        this.objectMapper = new ObjectMapper();
        // это модуль для корректной работы с LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
    }

    // сохранение одного пункта продаж
    public void saveSalesPoint(SalesPoint salesPoint, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), salesPoint);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения пункта продаж: " + e.getMessage(), e);
        }
    }

    // загрузка одного пункта продаж
    public SalesPoint loadSalesPoint(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename), SalesPoint.class);
        } catch (IOException e) {
            throw new IOException("ошибка загрузки пункта продаж: " + e.getMessage(), e);
        }
    }

    // сохранение списка пунктов продаж
    public void saveAllSalesPoints(List<SalesPoint> salesPoints, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), salesPoints);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения списка пунктов продаж: " + e.getMessage(), e);
        }
    }

    // загрузка списка пунктов продаж
    public List<SalesPoint> loadAllSalesPoints(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, SalesPoint.class));
        } catch (IOException e) {
            throw new IOException("ошибка загрузки списка пунктов продаж: " + e.getMessage(), e);
        }
    }
}
