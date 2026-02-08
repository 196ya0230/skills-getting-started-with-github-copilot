package com.mergington.students.controller;

import com.mergington.students.model.Student;
import com.mergington.students.service.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class StudentsController {
    private final StudentRepository studentRepository;

    public StudentsController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/students")
    public Map<String, Student> getStudents() {
        return studentRepository.getAllStudents();
    }

    @GetMapping("/students/{email}")
    public Student getStudent(@PathVariable String email) {
        Student student = studentRepository.getStudent(email);
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        return student;
    }
}
