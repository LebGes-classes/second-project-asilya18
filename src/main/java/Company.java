import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;



public class Company {
    private String name; // название компании
    private List<Warehouse> warehouses;
    private List<SalesPoint> salesPoints;
    private List<Employee> employees;
    private List<Product> products;

    public Company() {
        this.name = "EUROSPAR";
        this.warehouses = new ArrayList<>();
        this.salesPoints = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.products = new ArrayList<>();
    }

    public List<Warehouse> getWarehouses() {
        return warehouses;
    }

    public List<SalesPoint> getSalesPoints() {
        return salesPoints;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Product> getProducts() {
        return products;
    }


        public void openWarehouse(Warehouse warehouse){ // открыть новый склад
        if (warehouse==null) {
            throw new IllegalArgumentException("склад не может быть null");
        }
        if (warehouses.stream().anyMatch(w -> w.getId() == warehouse.getId())) {
            throw new IllegalStateException("склад с ID " + warehouse.getId() + " уже существует");
        }
        warehouses.add(warehouse);
    }

    public void closeWarehouse(int warehouseId){ // закрыть склад полностью (его больше не будет существовать)
        Warehouse warehouse = findWarehouseById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("склад с ID" + warehouseId + "не найден"));
// так как наш метод findWarehouseById(warehouseId) возвращает тип Optional<Warehouse>

        warehouse.close();
        warehouses.remove(warehouse); // удаляем склад из коллекции складов
    }

    private Optional<Warehouse> findWarehouseById(int id) { // вспомогательный метод для нахождения склада по id
        return warehouses.stream().filter(w -> w.getId() == id).findFirst();
    } // использование обертки optional после выполнения терминальной операции findFirst()

        private Optional<SalesPoint> findSalesPointById(int id) { //вспомогательный метод для нахождения пункта продаж по id
        return salesPoints.stream().filter(sp -> sp.getId() == id).findFirst();
    }


    public void purchaseProduct(Product product, int quantity, int warehouseId, int cellId) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException("некорректные данные");
        }

        Warehouse warehouse = findWarehouseById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("склад не найден"));
        StorageCell cell = warehouse.findCellById(cellId);
        if (cell == null) {
            throw new IllegalArgumentException("ячейка не найдена");
        }

        if (cell.getFreeSpace() < quantity) {
            throw new IllegalStateException("недостаточно места в ячейке");
        }

        // добавляем товар в ячейку склада
        Optional<Product> existingProduct = cell.getProducts().stream()
                .filter(p -> p.getId() == product.getId())
                .findFirst();

        if (existingProduct.isPresent()) {
            existingProduct.get().increaseQuantity(quantity);
        } else {
            Product newProduct = new Product(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getCategory(),
                    quantity
            );
            cell.addProduct(newProduct);
        }
    }

    public boolean removeProductFromWarehouse(int warehouseId, int cellId, int productId, int quantity) {
        Warehouse warehouse = findWarehouseById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("cклад не найден"));

        StorageCell cell = warehouse.findCellById(cellId);
        if (cell == null) {
            throw new IllegalArgumentException("ячейка не найдена");
        }

        Product product = cell.getProducts().stream()
                .filter(p -> p.getId() == productId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("товар не найден в ячейке"));

        if (product.getQuantity() < quantity) {
            throw new IllegalStateException("недостаточно товара в ячейке");
        }

        product.decreaseQuantity(quantity);

        // если количество стало 0, полностью удаляем товар из ячейки
        if (product.getQuantity() == 0) {
            cell.removeProduct(product);
        }

        return true;
    }

    public void clearAllData() {
        this.employees.clear();
        this.warehouses.clear();
        this.salesPoints.clear();
        this.products.clear();
        // другие списки данных, если есть
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}