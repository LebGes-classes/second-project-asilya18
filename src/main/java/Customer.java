import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private final int id;
	private final String name;
	private List<Product> purchaseHistory; // история купленных товаров

    public Customer(int id, String name){
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("имя не может быть пустым");
        }
        this.id = id;
        this.name = name;
        this.purchaseHistory = new ArrayList<>();
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public List<Product> getPurchaseHistory(){
        return new ArrayList<>(purchaseHistory); // защитная от изменений копия
    }

    public void addToHistory(Product product){
        if (product == null) {
            throw new IllegalArgumentException("товар не может быть пустым");
        }
        this.purchaseHistory.add(product);
    }


    public String toString(){
        return String.format("Customer[id=%d, name='%s', totalPurchases=%d]", id, name, purchaseHistory.size());
    }
}
