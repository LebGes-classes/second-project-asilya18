    import com.fasterxml.jackson.databind.ObjectMapper;

    import java.io.File;
    import java.io.IOException;

    public class Product {
        private final int id;
        private final String name;
        private double price;
        private String category;
        private int quantity;

        public Product(int id, String name, double price, String category, int quantity){
            if (id <= 0) {
                throw new IllegalArgumentException("ID должен быть положительным");
            }
            this.id = id;
            this.name = name;
            this.setPrice(price);
            this.setCategory(category);
            this.setQuantity(quantity); // используем сеттеры с валидацией
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
        public double getPrice() {
            return price;
        }

        public String getCategory() {
            return category;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setPrice(double price) {
            if (price >= 0) {
                this.price = price;
            } else {
                throw new IllegalArgumentException("цена не может быть отрицательной");
            }
        }

        public void setCategory(String category) {
            if (category != null && !category.isEmpty()) {
                this.category = category;
            } else {
                throw new IllegalArgumentException("категория не может быть пустой");
            }
        }

        public void setQuantity(int quantity) {
            if (quantity >= 0) {
                this.quantity = quantity;
            } else {
                throw new IllegalArgumentException("количество не может быть отрицательным");
            }
        }

        public void increaseQuantity(int amount){
            if (amount > 0) {
                this.quantity += amount;
            } else {
                throw new IllegalArgumentException("количество для увеличения должно быть положительным");
            }
        }

        public boolean decreaseQuantity(int amount){
            if (amount <= 0) {
                throw new IllegalArgumentException("количество для уменьшения должно быть положительным");
            }
            if (this.quantity < amount) {
                throw new IllegalArgumentException("недостаточно товара для уменьшения");
            }
            this.quantity -= amount;
            return true;
        }

        public void saveToJson(String filename) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String toString(){ // стандартный java-метод для представления инфы об обьектах
            return String.format("Product[id=%d, name='%s', price=%.2f, category='%s', quantity=%d]",
                    id, name, price, category, quantity);
        }

    }
