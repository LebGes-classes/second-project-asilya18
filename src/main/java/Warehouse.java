import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// мой склад - круглосуточный , а вот точки продаж - нет
public class Warehouse {
        private final int id;
        private final String name;
        private List<StorageCell> cells;
        private Employee responsibleEmployee;

    public Warehouse(int id, String name, Employee responsibleEmployee) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("название склада не может быть пустым");
        }
        this.id = id;
        this.name = name;
        this.cells = new ArrayList<>();
        this.responsibleEmployee = responsibleEmployee;
        this.isClosed = false;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<StorageCell> getCells() {
        return new ArrayList<>(cells); // защитная от изменений копия
    }

    public Employee getResponsibleEmployee() {
        return responsibleEmployee;
    }

    public void setResponsibleEmployee(Employee responsibleEmployee) {
        if (responsibleEmployee != null) {
            this.responsibleEmployee = responsibleEmployee;
        } else {
            throw new IllegalArgumentException("сотрудник не может быть null");
        }
    }

    public void addStorageCell(StorageCell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("ячейка не может быть null");
        }
        this.cells.add(cell);
    }

    StorageCell findCellById(int cellId) { // метод нахождения ячейки по его id
        return cells.stream()
                .filter(cell -> cell.getId() == cellId) // только ячейки с соответсвующим id
                .findAny() // наша ячейка с таким id уникальна
                .orElse(null);
    }


    private boolean isClosed;

    public void close() {
        if (isClosed) {
            throw new IllegalStateException("склад уже закрыт");
        }
        cells.clear();
        isClosed = true;
        System.out.println("склад " + name + " был очищен.");
    }

    public void saveToJson(String filename) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String toString(){
        return String.format(
                "Warehouse[id=%d, name='%s', cells=%d, responsible='%s']", id, name, cells.size(),
                responsibleEmployee != null ? responsibleEmployee.getName() : "не назначен" );
    }

}
