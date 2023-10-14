package com.example.studentcourse.controller;

import com.example.studentcourse.dto.CourseDto;
import com.example.studentcourse.dto.CourseResponse;
import com.example.studentcourse.dto.StudentDto;
import com.example.studentcourse.model.Course;
import com.example.studentcourse.service.CourseService;
import com.example.studentcourse.type.IntegerArrayRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseController {
    @Autowired
    CourseService courseService;

    @PostMapping("/course/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto) {
        return new ResponseEntity<>(courseService.createCourse(courseDto), HttpStatus.CREATED);
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Integer courseId) {
        return new ResponseEntity<>(courseService.getCourseById(courseId), HttpStatus.OK);
    }

    @GetMapping("/all-course")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return new ResponseEntity<>(courseService.getAllCourses(), HttpStatus.OK);
    }

//
//    @GetMapping("/course")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public ResponseEntity<List<CourseDto>> searchCourseByName(
//            @RequestParam(value = "name", defaultValue = "0", required = false) String name
//    ) {
//        return new ResponseEntity<>(courseService.searchCourseByName(name), HttpStatus.OK);
//    }

    @GetMapping("/courses")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
    public ResponseEntity<CourseResponse> getCourseByPage(
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "0", required = false) int pageSize) {
        return new ResponseEntity<>(courseService.getCourseByPage(name, pageNo, pageSize), HttpStatus.OK);
    }

    @DeleteMapping("/course/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteCourseById(@RequestBody IntegerArrayRequest courseIds) {
        courseService.deleteCourseById(courseIds.getIds());
        return new ResponseEntity<>("Delete course success!!", HttpStatus.OK);
    }

    @PutMapping("/course/update/{courseId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Integer courseId, @Valid @RequestBody CourseDto courseDto) {
        CourseDto courseUpdated = courseService.updateCourse(courseId, courseDto);

        return new ResponseEntity<>(courseUpdated, HttpStatus.OK);
    }
}
