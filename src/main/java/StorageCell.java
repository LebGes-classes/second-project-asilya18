import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageCell {
    private final int id;
    private int capacity; // int , потому что я буду работать только с товарами , которые считаются поштучно
    private List<Product> products; // список продуктов в этой ячейке
    private Employee responsibleEmployee;

    public StorageCell(int id, int capacity, Employee responsibleEmployee) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным");
        }
        if (capacity <= 0){
            throw new IllegalArgumentException("вместимость должна быть положительной");
        }
        this.id = id;
        this.capacity = capacity;
        this.products = new ArrayList<>();
        this.responsibleEmployee = responsibleEmployee;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products); // защитная от изменений копия
    }

    public Employee getResponsibleEmployee() {
        return responsibleEmployee;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("вместимость ячейки должна быть положительной");
        }
        if (capacity < products.size()) {
            throw new IllegalStateException("новая вместимость меньше текущего количества товаров");
        }
        this.capacity = capacity;
    }

    public void setResponsibleEmployee(Employee responsibleEmployee) {
        if (responsibleEmployee != null) {
            this.responsibleEmployee = responsibleEmployee;
        } else {
            throw new IllegalArgumentException("сотрудник не может быть null");
        }
    }

    public void addProduct(Product product){
        if (product == null) {
            throw new IllegalArgumentException("товар не может быть пустым");
        }
        if (products.size() >= capacity) {
            throw new IllegalStateException("ячейка заполнена, невозможно добавить товар");
        }
        this.products.add(product);
    }

    public boolean removeProduct(Product product){
        if (product == null) {
            throw new IllegalArgumentException("товар не может быть пустым");
        }
        return products.remove(product); // успешно ли прошло удаление товара
    }

        public int getFreeSpace(){ // сколько еще свободного места в ячейке
        return capacity - products.size();
    }



    public String toString(){
        return String.format( "StorageCell[id=%d, capacity=%d/%d, responsible=%s]", id, products.size(), capacity,
                responsibleEmployee != null ? responsibleEmployee.getName() : "не назначен" );
    }
}
