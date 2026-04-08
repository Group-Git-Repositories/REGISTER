package com.example.register;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class register extends Application {

    private Connection connect() {
        String url = "jdbc:sqlite:students.db";
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS students (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                department TEXT NOT NULL,
                courses TEXT
            );
        """;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Student> getStudents() {
        ObservableList<Student> students = FXCollections.observableArrayList();
        String sql = "SELECT * FROM students";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(new Student(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("courses")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public void start(Stage primaryStage) {
        createTable();

        //  Student Info
        TextField nameField = new TextField();
        nameField.setPromptText("Enter student name");

        TextField deptField = new TextField();
        deptField.setPromptText("Enter department");

        TextField idField = new TextField();
        idField.setPromptText("Enter ID number");

        //  Courses
        CheckBox course1 = new CheckBox("Software Engineering");
        CheckBox course2 = new CheckBox("Advanced Object Oriented Programming");
        CheckBox course3 = new CheckBox("Internet Programming");
        CheckBox course4 = new CheckBox("Operating System");
        CheckBox course5 = new CheckBox("Assembly Language Programming");

        VBox coursesBox = new VBox(5, course1, course2, course3, course4, course5);

        //  Submit
        Button submitBtn = new Button("Register");
        Label resultLabel = new Label();

        // --- TableView for Students ---
        TableView<Student> tableView = new TableView<>();
        TableColumn<Student, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> data.getValue().idProperty());

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());

        TableColumn<Student, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(data -> data.getValue().departmentProperty());

        TableColumn<Student, String> coursesCol = new TableColumn<>("Courses");
        coursesCol.setCellValueFactory(data -> data.getValue().coursesProperty());

        tableView.getColumns().addAll(idCol, nameCol, deptCol, coursesCol);
        tableView.setItems(getStudents());

        submitBtn.setOnAction(e -> {
            String name = nameField.getText();
            String dept = deptField.getText();
            String id = idField.getText();

            List<String> selectedCourses = new ArrayList<>();
            if (course1.isSelected()) selectedCourses.add("Mathematics");
            if (course2.isSelected()) selectedCourses.add("Physics");
            if (course3.isSelected()) selectedCourses.add("Computer Science");
            if (course4.isSelected()) selectedCourses.add("History");
            if (course5.isSelected()) selectedCourses.add("Biology");

            String courses = String.join(", ", selectedCourses);

            String sql = "INSERT INTO students(id, name, department, courses) VALUES(?,?,?,?)";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, dept);
                pstmt.setString(4, courses);
                pstmt.executeUpdate();
                resultLabel.setText("Student registered successfully!");

                // Refresh table
                tableView.setItems(getStudents());
            } catch (SQLException ex) {
                resultLabel.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(10,
                new Label("Student Name:"), nameField,
                new Label("Department:"), deptField,
                new Label("ID Number:"), idField,
                new Label("Select Courses:"), coursesBox,
                submitBtn, resultLabel,
                new Label("Registered Students:"), tableView
        );

        root.setStyle("-fx-padding: 20; -fx-font-size: 14;");

        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Student Registration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
