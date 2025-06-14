import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class Employee {
    private final int id;
    private final String name;
    private String position;
    private double salary;
    private final LocalDate hireDate; // дата приема на работу


    @JsonCreator // в конструктор с параметарми добавлены аннотации jackson для создания
    // обьектов из json
    public Employee(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("position") String position,
            @JsonProperty("salary") double salary) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        this.id = id;
        this.name = name;
        this.setPosition(position);
        this.setSalary(salary);
        this.hireDate = LocalDate.now(); // текущая дата как дата приема
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public double getSalary() {
        return salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setSalary(double salary) {
        if (salary >= 0) {
            this.salary = salary;
        } else {
            throw new IllegalArgumentException("зарплата не может быть отрицательной");
        }
    }

    public void setPosition(String position) {
        if (position != null && !position.isEmpty()) {
            this.position = position;
        } else {
            throw new IllegalArgumentException("должность не может быть пустой");
        }
    }

    public String toString(){ // стандартный java-метод для представления инфы об обьектах
        return String.format( "Employee[id=%d, name='%s', position='%s', salary=%.2f, hireDate=%s]",
                id, name, position, salary, hireDate );
    }
}
