import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SalesPoint {
    private final int id;
    private final String name;
    private List<Product> availableProducts; // товары, которые можно купить в этом магазине
    private Employee responsibleEmployee;
    private double revenue; // доход
    private LocalDateTime closingTime; // время закрытия (null если открыт)
    private LocalDateTime lastOpeningTime; // время последнего открытия

    public SalesPoint(int id, String name, Employee responsibleEmployee, double revenue){
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("название магазина не может быть пустым");
        }
        if (responsibleEmployee == null) {
            throw new IllegalArgumentException("сотрудник не может быть null");
        }
        if (revenue < 0) {
            throw new IllegalArgumentException("доход не может быть отрицательным");
        }
        this.id = id;
        this.name = name;
        this.responsibleEmployee = responsibleEmployee;
        this.revenue = revenue;
        this.availableProducts = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Product> getAvailableProducts() {
        return new ArrayList<>(availableProducts); // защитная от изменений копия
    }

    public Employee getResponsibleEmployee() {
        return responsibleEmployee;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setResponsibleEmployee(Employee responsibleEmployee) {
        if (responsibleEmployee != null) {
            this.responsibleEmployee = responsibleEmployee;
        } else {
            throw new IllegalArgumentException("сотрудник не может быть null");
        }
    }

    public void setRevenue(double revenue) {
        if (revenue >= 0) {
            this.revenue = revenue;
        } else {
            throw new IllegalArgumentException("доход не может быть отрицательным");
        }
    }

    public void close() { // закрываем пункт продаж в конце рабочего дня
        if (closingTime != null) {
            throw new IllegalStateException("пункт продаж уже закрыт");
        }
        this.closingTime = LocalDateTime.now();
        System.out.printf("пункт продаж '%s' закрыт в %s%n",
                name, closingTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("товар не может быть null");
        }
        availableProducts.add(product);
    }


    public double getProfit() { // посмотреть доход
        return revenue;
    }

    public boolean isOpen() { // открыт ли пункт продаж
        return closingTime == null;
    }

    public void openNewSalesPoint() {
        open();
    }

    public void startBusinessDay() {
        open();
    }

    private void open() {
        this.lastOpeningTime = LocalDateTime.now();
        this.closingTime = null;
        System.out.printf("пункт продаж '%s' открыт в %s%n",
                name, lastOpeningTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }


    public void purchaseProduct(Product product, int quantity, double purchasePrice) {
        if (product == null) {
            throw new IllegalArgumentException("товар не может быть null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("количество должно быть положительным");
        }
        if (purchasePrice <= 0) {
            throw new IllegalArgumentException("цена закупки должна быть положительной");
        }

        // проверяем, есть ли уже такой товар в магазине
        Optional<Product> existingProduct = availableProducts.stream()
                .filter(p -> p.getId() == product.getId())
                .findFirst();

        if (existingProduct.isPresent()) {
            // если товар уже есть - увеличиваем его количество
            existingProduct.get().increaseQuantity(quantity);
        } else {
            // если товара нет - добавляем новый
            Product newProduct = new Product(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getCategory(),
                    quantity);
            availableProducts.add(newProduct);
        }

        // уменьшаем доход магазина на стоимость закупки
        this.revenue -= purchasePrice * quantity;
        System.out.printf("закуплено %d единиц товара '%s' по цене %.2f за единицу%n",
                quantity, product.getName(), purchasePrice);
    }


    public void sellProduct(int productId, Customer customer, int quantity) {
        if (customer == null) {
            throw new IllegalArgumentException("покупатель не может быть null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("количество должно быть положительным");
        }
        if (closingTime != null) {
            throw new IllegalStateException("пункт продаж закрыт");
        }

        Product product = availableProducts.stream()
                .filter(p -> p.getId() == productId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("товар с ID " + productId + " не найден"));

        if (product.getQuantity() < quantity) {
            throw new IllegalStateException("недостаточно товара на складе");
        }

        // уменьшаем количество товара
        product.decreaseQuantity(quantity);

        // увеличиваем доход магазина
        double totalPrice = product.getPrice() * quantity;
        this.revenue += totalPrice;

        // добавляем товар в историю покупок покупателя
        for (int i = 0; i < quantity; i++) {
            customer.addToHistory(product);
        }

        System.out.printf("продано %d единиц товара '%s' покупателю %s на сумму %.2f%n",
                quantity, product.getName(), customer.getName(), totalPrice);
    }

    public void saveToJson(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return String.format(
                "SalesPoint[id=%d, name='%s', products=%d, revenue=%.2f, responsible='%s', status=%s]",
                id, name, availableProducts.size(), revenue, responsibleEmployee.getName(),
                isOpen() ? "OPEN" : "CLOSED");
    }
}