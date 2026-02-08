package com.mergington.students.service;

import com.mergington.students.model.Student;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class StudentRepository {
    private final Map<String, Student> students = new HashMap<>();

    public StudentRepository() {
        students.put("michael@mergington.edu", new Student("michael@mergington.edu", "Michael", 11));
        students.put("daniel@mergington.edu", new Student("daniel@mergington.edu", "Daniel", 12));
        students.put("emma@mergington.edu", new Student("emma@mergington.edu", "Emma", 10));
        students.put("sophia@mergington.edu", new Student("sophia@mergington.edu", "Sophia", 11));
        students.put("john@mergington.edu", new Student("john@mergington.edu", "John", 9));
        students.put("olivia@mergington.edu", new Student("olivia@mergington.edu", "Olivia", 10));
    }

    public Map<String, Student> getAllStudents() {
        return Collections.unmodifiableMap(students);
    }

    public Student getStudent(String email) {
        return students.get(email);
    }
}
