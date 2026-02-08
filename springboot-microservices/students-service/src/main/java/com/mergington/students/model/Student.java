package com.mergington.students.model;

public class Student {
    private String email;
    private String name;
    private int grade;

    public Student() {
    }

    public Student(String email, String name, int grade) {
        this.email = email;
        this.name = name;
        this.grade = grade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
