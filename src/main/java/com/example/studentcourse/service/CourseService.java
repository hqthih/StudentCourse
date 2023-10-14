package com.example.studentcourse.service;

import com.example.studentcourse.dto.CourseDto;
import com.example.studentcourse.dto.CourseResponse;
import com.example.studentcourse.dto.StudentDto;
import com.example.studentcourse.model.Course;

import java.util.List;

public interface CourseService {
    CourseDto getCourseById(Integer id);

    CourseDto createCourse(CourseDto courseDto);

    CourseResponse getCourseByPage(String name, int pageNo, int pageSize);

    void deleteCourseById(List<Integer> courseId);

    CourseDto updateCourse(Integer courseId, CourseDto courseDto);

//    List<CourseDto> searchCourseByName(String name);

    List<CourseDto> getAllCourses();
}
