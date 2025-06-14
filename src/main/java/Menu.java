import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.*;

    public class Menu {

        private Company company;
        private final Scanner scanner;
        private static final String dataDir = "company_data";

        // добавила сервисы для работы с json
        private final CompanyJsonService companyJsonService = new CompanyJsonService();
        private final EmployeeJsonService employeeJsonService = new EmployeeJsonService();
        private final WarehouseJsonService warehouseJsonService = new WarehouseJsonService();
        private final SalesPointJsonService salesPointJsonService = new SalesPointJsonService();
        private final ProductJsonService productJsonService = new ProductJsonService();
        private final CustomerJsonService customerJsonService = new CustomerJsonService();


        public static void main(String[] args) {
            new Menu().run();
        }

        public Menu() {
            this.scanner = new Scanner(System.in);
        }

        public void run() {
            initializeData();
            showWelcomeMessage();

            boolean running = true;
            while (running) {
                showMainMenu();
                int choice = getMenuChoice(1, 6);
                switch (choice) {
                    case 1 -> manageProducts();
                    case 2 -> manageWarehouses();
                    case 3 -> manageSalesPoints();
                    case 4 -> manageEmployees();
                    case 5 -> saveDataProducts();
                    case 6 -> running = false;
                }
            }

            saveAllDataOnExit();
            scanner.close();
        }

        public void initializeData() {
            try {
                createDataDirectory();

                // Всегда создаем новую компанию, игнорируя старые данные
                company = new Company();
                System.out.println("Создана новая компания (старые данные игнорируются)");

                // Перезаписываем файлы пустыми данными
                companyJsonService.saveCompany(company, dataDir + "/company.json");
                employeeJsonService.saveAllEmployees(new ArrayList<>(), dataDir + "/employees.json");
                warehouseJsonService.saveAllWarehouses(new ArrayList<>(), dataDir + "/warehouses.json");
                salesPointJsonService.saveAllSalesPoints(new ArrayList<>(), dataDir + "/salesPoints.json");
                productJsonService.saveAllProducts(new ArrayList<>(), dataDir + "/products.json");

            } catch (IOException e) {
                System.err.println("Ошибка инициализации: " + e.getMessage());
                company = new Company();
            }
        }

        public void createDataDirectory()  throws IOException {
            Path path = Paths.get(dataDir);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
                System.out.println("создана директория для данных: " + path);
            }
        }

        private void saveAllDataOnExit() {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nОчистка данных перед выходом...");
                try {
                    // Очищаем все данные компании
                    company.clearAllData();

                    // Создаем пустые файлы
                    Path dir = Paths.get(dataDir);
                    if (!Files.exists(dir)) {
                        Files.createDirectories(dir);
                    }

                    // Создаем пустые JSON файлы
                    Files.write(Paths.get(dataDir, "company.json"), "{}".getBytes());
                    Files.write(Paths.get(dataDir, "employees.json"), "[]".getBytes());
                    Files.write(Paths.get(dataDir, "warehouses.json"), "[]".getBytes());
                    Files.write(Paths.get(dataDir, "salesPoints.json"), "[]".getBytes());
                    Files.write(Paths.get(dataDir, "products.json"), "[]".getBytes());

                    System.out.println("Все данные очищены");
                } catch (Exception e) {
                    System.err.println("Ошибка при очистке данных: " + e.getMessage());
                }
            }));
        }

        public void showWelcomeMessage() {
            System.out.println();
            System.out.println("==============================");
            System.out.println("  система управления оборотом");
            System.out.println("  компания: " + company.getName());
            System.out.println("==============================");
        }

        public void showMainMenu() {
            System.out.println();
            System.out.println(" ~ главное меню ~ ");
            System.out.println("1. управление продуктами");
            System.out.println("2. управление складами");
            System.out.println("3. управление магазинами");
            System.out.println("4. управление сотрудниками");
            System.out.println("5. сохранить данные");
            System.out.println("6. выход");
            System.out.print("выберите пункт меню: ");
        }

        private int getMenuChoice(int min, int max) {
            int choice = -1;
            while (choice < min || choice > max) {
                try {
                    choice = scanner.nextInt();
                    if (choice < min || choice > max) {
                        System.out.println("пожалуйста, выберите корректный вариант.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("некорректный ввод. пожалуйста, введите число.");
                    scanner.next(); // очистка некорректного ввода
                }
            }
            return choice;
        }

        private void manageProducts() {
            boolean managing = true;
            while (managing) {
                System.out.println("\n ~ управление продуктами ~ ");
                System.out.println("1. добавить продукт на склад ");
                System.out.println("2. удалить продукт со склада ");
                System.out.println("3. просмотреть все товары ");
                System.out.println("4. вернуться в главное меню");
                System.out.print("выберите действие: ");

                int choice = getMenuChoice(1, 4);
                switch (choice) {
                    case 1 -> addProductToWarehouse();
                    case 2 -> removeProductFromWarehouse();
                    case 3 -> viewProducts();
                    case 4 -> managing = false;
                }
            }
        }

        private void addProductToWarehouse() {
            System.out.print("введите ID склада: ");
            int warehouseId = scanner.nextInt();
            System.out.print("введите ID ячейки: ");
            int cellId = scanner.nextInt();
            System.out.print("введите ID товара: ");
            int productId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("введите название товара: ");
            String name = scanner.nextLine();
            System.out.print("введите цену: ");
            double price = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("введите категорию: ");
            String category = scanner.nextLine();
            System.out.print("введите количество: ");
            int quantity = scanner.nextInt();

            Product product = new Product(productId, name, price, category, quantity);
            company.getProducts().add(product);

            Optional<Warehouse> warehouseOpt = company.getWarehouses().stream()
                    .filter(w -> w.getId() == warehouseId)
                    .findFirst();

            if (warehouseOpt.isEmpty()) {
                System.out.println("склад с ID " + warehouseId + " не найден.");
                return;
            }
            Warehouse warehouse = warehouseOpt.get();

            StorageCell cell = warehouse.findCellById(cellId);
            if (cell == null) {
                // если ячейка не найдена, создаём новую
                System.out.print("ячейка не найдена. создать новую? (1 - да, 2 - нет): ");
                int choice = scanner.nextInt();
                if (choice == 1) {
                    System.out.print("введите вместимость ячейки: ");
                    int capacity = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("введите ID ответственного сотрудника: ");
                    int employeeId = scanner.nextInt();

                    Employee employee = company.getEmployees().stream()
                            .filter(e -> e.getId() == employeeId)
                            .findFirst()
                            .orElse(null);

                    if (employee == null) {
                        System.out.println("сотрудник с ID " + employeeId + " не найден");
                        return;
                    }

                    cell = new StorageCell(cellId, capacity, employee);
                    warehouse.addStorageCell(cell);
                    System.out.println("ячейка создана");
                } else {
                    return;
                }
            }

            company.purchaseProduct(product, quantity, warehouseId, cellId);
            System.out.println("товар добавлен на склад");
            saveDataProducts();
        }

        private void viewProducts() {
            List<Product> products = company.getProducts();
            if (products.isEmpty()) {
                System.out.println("список продуктов пуст.");
            } else {
                System.out.println("список продуктов:");
                for (Product product : products) {
                    System.out.println(product);
                }
            }
        }

        private void removeProductFromWarehouse() {
            System.out.print("введите ID склада: ");
            int warehouseId = scanner.nextInt();
            System.out.print("введите ID ячейки: ");
            int cellId = scanner.nextInt();
            System.out.print("введите ID товара: ");
            int productId = scanner.nextInt();
            System.out.print("введите количество для удаления: ");
            int quantity = scanner.nextInt();

            try {
                boolean success = company.removeProductFromWarehouse(warehouseId, cellId, productId, quantity);
                if (success) {
                    System.out.println("товар успешно удалён со склада!");
                    saveDataProducts();
                    saveDataWarehouses();
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("ошибка: " + e.getMessage());
            }
        }


        private void manageWarehouses() {
            boolean managing = true;
            while (managing) {
                System.out.println("\n ~ управление складами ~ ");
                System.out.println("1. открыть новый склад");
                System.out.println("2. закрыть склад");
                System.out.println("3. просмотреть все склады");
                System.out.println("4. вернуться в главное меню");
                System.out.print("выберите действие: ");

                int choice = getMenuChoice(1, 4);
                switch (choice) {
                    case 1 -> openWarehouse();
                    case 2 -> closeWarehouse();
                    case 3 -> viewWarehouses();
                    case 4 -> managing = false;
                }
            }
        }

        private void openWarehouse() {
            System.out.print("введите ID склада: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("введите название склада: ");
            String name = scanner.nextLine();
            System.out.print("введите ID ответственного сотрудника: ");
            int employeeId = scanner.nextInt();

            Employee responsibleEmployee = company.getEmployees().stream()
                    .filter(e -> e.getId() == employeeId)
                    .findFirst()
                    .orElse(null);

            if (responsibleEmployee == null) {
                System.out.println("сотрудник с ID " + employeeId + " не найден");
                return;
            }

            try {
                Warehouse warehouse = new Warehouse(id, name, responsibleEmployee);
                company.openWarehouse(warehouse);
                System.out.println("склад открыт: " + warehouse);
                saveDataWarehouses();
            } catch (IllegalArgumentException e) {
                System.out.println("ошибка при открытии склада: " + e.getMessage());
            }
        }

        private void closeWarehouse() {
            System.out.print("введите ID склада для закрытия: ");
            int warehouseId = scanner.nextInt();

            try {
                company.closeWarehouse(warehouseId); // Закрываем склад в компании
                System.out.println("склад с ID " + warehouseId + " закрыт");
                saveDataWarehouses();
            } catch (IllegalArgumentException e) {
                System.out.println("ошибка при закрытии склада: " + e.getMessage());
            }
        }

        private void viewWarehouses() {
            List<Warehouse> warehouses = company.getWarehouses();
            if (warehouses.isEmpty()) {
                System.out.println("список складов пуст.");
            } else {
                System.out.println("список складов:");
                for (Warehouse warehouse : warehouses) {
                    System.out.println(warehouse);
                }
            }
        }

        private void manageEmployees() {
           boolean managing = true;
           while (managing) {
               System.out.println("\n ~ управление сотрудниками ~ ");
               System.out.println("1. добавить сотрудника");
               System.out.println("2. удалить сотрудника");
               System.out.println("3. просмотреть всех сотрудников");
               System.out.println("4. вернуться в главное меню");
               System.out.print("выберите действие: ");


               int choice = getMenuChoice(1, 4);
               switch (choice) {
                   case 1 -> addEmployee();
                   case 2 -> removeEmployee();
                   case 3 -> viewEmployees();
                   case 4 -> managing = false;
               }
           }
        }

        private void addEmployee(){
            System.out.print("введите ID сотрудника: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("введите имя сотрудника: ");
            String name = scanner.nextLine();
            System.out.print("введите должность сотрудника: ");
            String position = scanner.nextLine();
            System.out.print("введите зарплату сотрудника: ");
            double salary = scanner.nextDouble();

            try {
                Employee employee = new Employee(id, name, position, salary);
                company.getEmployees().add(employee); // добавляем сотрудника в список компании
                System.out.println("сотрудник добавлен: " + employee);
                saveDataEmployees();
            } catch (IllegalArgumentException e) {
                System.out.println("ошибка при добавлении сотрудника: " + e.getMessage());
            }
        }


        private void removeEmployee() {
            if (company.getEmployees().isEmpty()) {
                System.out.println("список сотрудников пуст");
                saveDataEmployees();
            }

            viewEmployees(); // список имеющихся сотрудников

            System.out.print("введите ID сотрудника для удаления: ");
            int employeeId = scanner.nextInt();

            // не является ли сотрудник ответственным за предприятие - их нельзя удалять
            if (isEmployeeInUse(employeeId)) {
                System.out.println("нельзя удалить сотрудника - он назначен ответственным за склад/магазин");
                return;
            }

            Optional<Employee> employeeToRemove = company.getEmployees().stream()
                    .filter(e -> e.getId() == employeeId)
                    .findFirst();

            if (employeeToRemove.isPresent()) {
                company.getEmployees().remove(employeeToRemove.get());
                System.out.println("сотрудник удалён: " + employeeToRemove.get());
            } else {
                System.out.println("сотрудник с ID " + employeeId + " не найден.");
            }
        }

        private boolean isEmployeeInUse(int employeeId) {

            boolean inWarehouses = company.getWarehouses().stream() // не является ли сотрудник ответственным за склад
                    .anyMatch(w -> w.getResponsibleEmployee().getId() == employeeId);


            boolean inSalesPoints = company.getSalesPoints().stream() // не является ли сотрудник ответственным за магазин
                    .anyMatch(s -> s.getResponsibleEmployee().getId() == employeeId);

            return inWarehouses || inSalesPoints;
        }


        private void viewEmployees() {
            List<Employee> employees = company.getEmployees();
            if (employees.isEmpty()) {
                System.out.println("список сотрудников пуст.");
            }
            else {
                System.out.println("список сотрудников:");
                for (Employee employee : employees) {
                    System.out.println(employee);
                }
            }
        }

        private void manageSalesPoints() {
            boolean managing = true;
            while (managing) {
                System.out.println("\n ~ управление магазинами ~ ");
                System.out.println("1. открыть новый магазин");
                System.out.println("2. закрыть магазин ");
                System.out.println("3. просмотреть все магазины");
                System.out.println("4. закупить продукт в магазин");
                System.out.println("5. продать продукт ");
                System.out.println("6. открыть магазин в начале рабочего дня");
                System.out.println("7. вернуться в главное меню");
                System.out.print("выберите действие: ");

                int choice = getMenuChoice(1, 7);
                switch (choice) {
                    case 1 -> openSalesPoint();
                    case 2 -> closeSalesPoint();
                    case 3 -> viewSalesPoints();
                    case 4 -> purchaseProductForSalesPoint();
                    case 5 -> sellProductFromSalesPoint();
                    case 6 -> startBusinessDayForSalesPoint();
                    case 7 -> managing = false;
                }
            }
        }

        private void startBusinessDayForSalesPoint() {
            System.out.print("введите ID магазина для открытия в начале рабочего дня: ");
            int salesPointId = scanner.nextInt();

            Optional<SalesPoint> salesPoint = company.getSalesPoints().stream()
                    .filter(s -> s.getId() == salesPointId)
                    .findFirst();

            if (salesPoint.isPresent()) {
                try {
                    salesPoint.get().startBusinessDay();
                } catch (IllegalStateException e) {
                    System.out.println("ошибка: " + e.getMessage());
                }
            } else {
                System.out.println("магазин с ID " + salesPointId + " не найден.");
            }
        }

        private void openSalesPoint() {
            System.out.print("введите ID магазина: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("введите название магазина: ");
            String name = scanner.nextLine();
            System.out.print("введите ID ответственного сотрудника: ");
            int employeeId = scanner.nextInt();

            Employee responsibleEmployee = company.getEmployees().stream()
                    .filter(e -> e.getId() == employeeId)
                    .findFirst()
                    .orElse(null);

            if (responsibleEmployee == null) {
                System.out.println("сотрудник с ID " + employeeId + " не найден");
                return;
            }

            try {
                SalesPoint salesPoint = new SalesPoint(id, name, responsibleEmployee, 0);
                company.getSalesPoints().add(salesPoint);

                salesPoint.openNewSalesPoint();
                System.out.println("магазин открыт: " + salesPoint);
                saveDataSalesPoints();

            } catch (IllegalArgumentException e) {
                System.out.println("ошибка при открытии магазина: " + e.getMessage());
            }
        }


        private void closeSalesPoint() {
            System.out.print("введите ID магазина для закрытия: ");
            int salesPointId = scanner.nextInt();

            Optional<SalesPoint> salesPointToClose = company.getSalesPoints().stream()
                    .filter(s -> s.getId() == salesPointId)
                    .findFirst();

            if (salesPointToClose.isPresent()) {
                if (salesPointToClose.get().isOpen()) {
                    salesPointToClose.get().close();
                    saveDataSalesPoints();
                } else {
                    System.out.println("магазин с ID " + salesPointId + " уже закрыт.");
                }
            } else {
                System.out.println("магазин с ID " + salesPointId + " не найден.");
            }
        }


        private void viewSalesPoints() {
            List<SalesPoint> salesPoints = company.getSalesPoints();
            if (salesPoints.isEmpty()) {
                System.out.println("список магазинов пуст.");
            } else {
                System.out.println("список магазинов:");
                for (SalesPoint salesPoint : salesPoints) {
                    System.out.println(salesPoint);
                }
            }
        }

        private void purchaseProductForSalesPoint() {
            System.out.print("введите ID магазина: ");
            int salesPointId = scanner.nextInt();

            Optional<SalesPoint> salesPoint = company.getSalesPoints().stream()
                    .filter(s -> s.getId() == salesPointId)
                    .findFirst();

            if (salesPoint.isPresent()) {
                System.out.print("введите ID продукта для закупки: ");
                int productId = scanner.nextInt();
                System.out.print("введите количество: ");
                int quantity = scanner.nextInt();
                System.out.print("введите цену закупки за единицу: ");
                double purchasePrice = scanner.nextDouble();

                // находим продукт в общем списке компании
                Optional<Product> productOpt = company.getProducts().stream()
                        .filter(p -> p.getId() == productId)
                        .findFirst();

                if (productOpt.isPresent()) {
                    try {
                        Product productToPurchase = productOpt.get();

                        // проверяем наличие товара на складах
                        int availableQuantity = 0;
                        for (Warehouse warehouse : company.getWarehouses()) {
                            for (StorageCell cell : warehouse.getCells()) {
                                // получаем список продуктов в ячейке
                                List<Product> cellProducts = cell.getProducts();
                                // суммируем количество нужного продукта
                                availableQuantity += cellProducts.stream()
                                        .filter(p -> p.getId() == productId)
                                        .mapToInt(Product::getQuantity)
                                        .sum();
                            }
                        }

                        if (availableQuantity < quantity) {
                            System.out.println("недостаточно товара на складах. доступно: " + availableQuantity);
                            return;
                        }

                        // уменьшаем количество на складах
                        int remainingToRemove = quantity;
                        outerLoop:
                        for (Warehouse warehouse : company.getWarehouses()) {
                            for (StorageCell cell : warehouse.getCells()) {
                                List<Product> cellProducts = cell.getProducts();
                                for (Product cellProduct : cellProducts) {
                                    if (cellProduct.getId() == productId) {
                                        int cellQuantity = cellProduct.getQuantity();
                                        int toRemove = Math.min(cellQuantity, remainingToRemove);
                                        cellProduct.setQuantity(cellQuantity - toRemove);
                                        remainingToRemove -= toRemove;

                                        if (remainingToRemove == 0) {
                                            break outerLoop;
                                        }
                                    }
                                }
                            }
                        }

                        // добавляем товар в магазин
                        salesPoint.get().purchaseProduct(productToPurchase, quantity, purchasePrice);
                        System.out.println("товар успешно закуплен для магазина");

                        // 4. Сохраняем изменения
                        saveDataSalesPoints();
                        saveDataWarehouses();
                        saveDataProducts();

                    } catch (IllegalArgumentException | IllegalStateException e) {
                        System.out.println("ошибка при закупке товара: " + e.getMessage());
                    }
                } else {
                    System.out.println("продукт с ID " + productId + " не найден.");
                }
            } else {
                System.out.println("магазин с ID " + salesPointId + " не найден.");
            }
        }

        private void sellProductFromSalesPoint() {
            System.out.print("введите ID магазина: ");
            int salesPointId = scanner.nextInt();

            Optional<SalesPoint> salesPoint = company.getSalesPoints().stream()
                    .filter(s -> s.getId() == salesPointId)
                    .findFirst();

            if (salesPoint.isPresent()) {
                // запрашиваем данные покупателя
                System.out.print("введите ID покупателя: ");
                int customerId = scanner.nextInt();
                scanner.nextLine(); // очистка буфера

                System.out.print("введите имя покупателя: ");
                String customerName = scanner.nextLine();

                // создаем покупателя
                Customer customer = new Customer(customerId, customerName);

                // запрашиваем данные о продаже
                System.out.print("введите ID продукта для продажи: ");
                int productId = scanner.nextInt();

                System.out.print("введите количество: ");
                int quantity = scanner.nextInt();

                try {
                    // выполняем продажу
                    salesPoint.get().sellProduct(productId, customer, quantity);
                    System.out.println("товар успешно продан");

                    // сохраняем данные о продаже
                    saveDataSalesPoints();
                    saveDataProducts(); // если количество продуктов меняется при продаже

                    //customer.saveToJson(dataDir + "/customer_" + customer.getId() + ".json");

                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("ошибка при продаже товара: " + e.getMessage());
                }
            } else {
                System.out.println("магазин с ID " + salesPointId + " не найден.");
            }
        }

            private void saveDataProducts() {

                try {
                    Path dirPath = Paths.get(dataDir);
                    if (!Files.exists(dirPath)) {
                        Files.createDirectories(dirPath);
                        System.out.println("создана директория: " + dirPath.toAbsolutePath());
                    }

                    Path productsPath = Paths.get(dataDir, "products.json");
                    System.out.println("путь к файлу продуктов: " + productsPath.toAbsolutePath());

                    List<Product> productsToSave = company.getProducts();

                    productJsonService.saveAllProducts(productsToSave, productsPath.toString());
                    System.out.println("продукты сохранены"); // Debug


                    if (Files.exists(productsPath) && Files.size(productsPath) > 0) {
                        System.out.println("файл успешно создан, размер: " + Files.size(productsPath) + " байт");
                    } else {
                        System.err.println("файл не создан или пуст!");
                    }

                } catch (IOException e) {
                    System.err.println("КРИТИЧЕСКАЯ ОШИБКА СОХРАНЕНИЯ:");
                    e.printStackTrace();
                }
            }


        private void saveDataEmployees() {
            try {
                Path dirPath = Paths.get(dataDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                    System.out.println("создана директория: " + dirPath.toAbsolutePath());
                }

                Path employeesPath = Paths.get(dataDir, "employees.json");
                System.out.println("путь к файлу сотрудников: " + employeesPath.toAbsolutePath());

                List<Employee> employeesToSave = company.getEmployees();

                employeeJsonService.saveAllEmployees(employeesToSave, employeesPath.toString());
                System.out.println("сотрудники сохранены");

                if (Files.exists(employeesPath) && Files.size(employeesPath) > 0) {
                    System.out.println("файл успешно создан, размер: " + Files.size(employeesPath) + " байт");
                } else {
                    System.err.println("файл не создан или пуст!");
                }

            } catch (IOException e) {
                System.err.println("КРИТИЧЕСКАЯ ОШИБКА СОХРАНЕНИЯ:");
                e.printStackTrace();
            }
        }

        private void saveDataWarehouses() {

            try {
                Path dirPath = Paths.get(dataDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                    System.out.println("создана директория: " + dirPath.toAbsolutePath());
                }

                Path warehousesPath = Paths.get(dataDir, "warehouses.json");
                System.out.println("путь к файлу складов: " + warehousesPath.toAbsolutePath());

                List<Warehouse> warehousesToSave = company.getWarehouses();

                warehouseJsonService.saveAllWarehouses(warehousesToSave, warehousesPath.toString());
                System.out.println("склады сохранены");

                if (Files.exists(warehousesPath) && Files.size(warehousesPath) > 0) {
                    System.out.println("файл успешно создан, размер: " + Files.size(warehousesPath) + " байт");
                } else {
                    System.err.println("файл не создан или пуст!");
                }

            } catch (IOException e) {
                System.err.println("КРИТИЧЕСКАЯ ОШИБКА СОХРАНЕНИЯ:");
                e.printStackTrace();
            }
        }

        private void saveDataSalesPoints() {

            try {
                Path dirPath = Paths.get(dataDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                    System.out.println("создана директория: " + dirPath.toAbsolutePath());
                }

                Path salesPointsPath = Paths.get(dataDir, "salesPoints.json");
                System.out.println("путь к файлу точек продаж: " + salesPointsPath.toAbsolutePath());

                List<SalesPoint> salesPointsToSave = company.getSalesPoints();

                salesPointJsonService.saveAllSalesPoints(salesPointsToSave, salesPointsPath.toString());
                System.out.println("точки продаж сохранены");

                if (Files.exists(salesPointsPath) && Files.size(salesPointsPath) > 0) {
                    System.out.println("файл успешно создан, размер: " + Files.size(salesPointsPath) + " байт");
                } else {
                    System.err.println("файл не создан или пуст!");
                }

            } catch (IOException e) {
                System.err.println("КРИТИЧЕСКАЯ ОШИБКА СОХРАНЕНИЯ:");
                e.printStackTrace();
            }
        }


    }
