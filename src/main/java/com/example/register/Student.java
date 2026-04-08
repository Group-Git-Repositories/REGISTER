package com.example.register;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty department;
    private final StringProperty courses;

    public Student(String id, String name, String department, String courses) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.department = new SimpleStringProperty(department);
        this.courses = new SimpleStringProperty(courses);
    }

    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty departmentProperty() { return department; }
    public StringProperty coursesProperty() { return courses; }
}
