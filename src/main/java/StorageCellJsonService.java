import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StorageCellJsonService {
    private final ObjectMapper objectMapper;

    public StorageCellJsonService() {
        this.objectMapper = new ObjectMapper();
    }

    // сохранение одной ячейки хранения
    public void saveStorageCell(StorageCell storageCell, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), storageCell);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения ячейки хранения: " + e.getMessage(), e);
        }
    }

    // загрузка одной ячейки хранения
    public StorageCell loadStorageCell(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename), StorageCell.class);
        } catch (IOException e) {
            throw new IOException("ошибка загрузки ячейки хранения: " + e.getMessage(), e);
        }
    }

    // сохранение списка ячеек хранения
    public void saveAllStorageCells(List<StorageCell> storageCells, String filename) throws IOException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), storageCells);
        } catch (IOException e) {
            throw new IOException("ошибка сохранения списка ячеек хранения: " + e.getMessage(), e);
        }
    }

    // загрузка списка ячеек хранения
    public List<StorageCell> loadAllStorageCells(String filename) throws IOException {
        try {
            return objectMapper.readValue(new File(filename),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, StorageCell.class));
        } catch (IOException e) {
            throw new IOException("ошибка загрузки списка ячеек хранения: " + e.getMessage(), e);
        }
    }
}
