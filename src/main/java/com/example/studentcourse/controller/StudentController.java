package com.example.studentcourse.controller;

import com.example.studentcourse.dto.StudentDto;
import com.example.studentcourse.dto.StudentResponse;
import com.example.studentcourse.model.Student;
import com.example.studentcourse.service.StudentService;
import com.example.studentcourse.type.IntegerArrayRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/student/create")
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody Student student) {
        return new ResponseEntity<>(studentService.createStudent(student), HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Integer studentId) {
        return new ResponseEntity<>(studentService.getStudentById(studentId), HttpStatus.OK);
    }

    @GetMapping("/student")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<StudentDto>> filterStudent(
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "age", defaultValue = "", required = false) Integer age,
            @RequestParam(value = "email", defaultValue = "", required = false) String email,
            @RequestParam(value = "address", defaultValue = "", required = false) String address)
    {
        return new ResponseEntity<>(studentService.filterStudent(name, age, email, address), HttpStatus.OK);
    }

    @GetMapping("/students")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StudentResponse> getStudents(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "0", required = false) int pageSize) {
        return new ResponseEntity<>(studentService.getStudentByPage(pageNo, pageSize), HttpStatus.OK);
    }

    @PutMapping("/student/update/{studentId}")
    @PreAuthorize("hasAuthority('ADMIN') or #studentId == authentication.credentials") // Check studentId same as userLogin's id
    public ResponseEntity<StudentDto> updateStudent(@Valid @RequestBody StudentDto studentDto, @PathVariable Integer studentId ) {
        return new ResponseEntity<>(studentService.updateStudent(studentDto, studentId), HttpStatus.OK);
    }

    @DeleteMapping("/student/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteStudent(@RequestBody IntegerArrayRequest studentIds) {
        studentService.deleteStudent(studentIds.getIds());
        return new ResponseEntity<>("Delete student success!!",HttpStatus.OK);
    }

    @PostMapping("/student/{studentId}/register")
    @PreAuthorize("hasAuthority('ADMIN') or #studentId == authentication.credentials") // Check studentId same as studentLogin's id
    public ResponseEntity<StudentDto> registerForCourse(@PathVariable Integer studentId, @RequestBody IntegerArrayRequest courseIds) {
        StudentDto studentInfo = studentService.registerForCourse(studentId, courseIds.getIds());
        return new ResponseEntity<>(studentInfo, HttpStatus.OK);
    }

    @PostMapping("/student/{studentId}/cancel")
    @PreAuthorize("hasAuthority('ADMIN') or #studentId == authentication.credentials") // Check studentId same as studentLogin's id
    public ResponseEntity<StudentDto> cancelCourse(@PathVariable Integer studentId, @RequestBody IntegerArrayRequest courseIds) {
        StudentDto studentInfo = studentService.cancelCourse(studentId,courseIds.getIds());
        return new ResponseEntity<>(studentInfo, HttpStatus.OK);
    }

}
