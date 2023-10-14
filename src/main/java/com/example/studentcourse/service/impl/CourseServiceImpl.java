package com.example.studentcourse.service.impl;

import com.example.studentcourse.dto.CourseDto;
import com.example.studentcourse.dto.CourseResponse;
import com.example.studentcourse.dto.StudentDto;
import com.example.studentcourse.dto.StudentResponse;
import com.example.studentcourse.model.Course;
import com.example.studentcourse.model.Student;
import com.example.studentcourse.repository.CourseRepository;
import com.example.studentcourse.repository.StudentRepository;
import com.example.studentcourse.service.CourseService;
import com.example.studentcourse.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    CourseRepository courseRepository;


    private CourseDto mapToDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setStudents(course.getStudents());

        return courseDto;
    }

    private Course mapToEntity(CourseDto courseDto) {
        Course course = new Course();
        course.setId(courseDto.getId());
        course.setName(courseDto.getName());
        course.setStudents(courseDto.getStudents());

        return course;
    }

    @Override
    public CourseDto getCourseById(Integer id) {
        Course course = courseRepository
                .findById(id)
                .orElseThrow(() ->new ResponseStatusException((HttpStatus.NOT_FOUND), "Invalid course id "+ id));

        return mapToDto(course);
    }

    @Override
    public CourseDto createCourse(CourseDto courseDto) {
        Course course = mapToEntity(courseDto);

        Course courseSaved = courseRepository.save(course);
        return mapToDto(courseSaved);
    }

//    @Override
//    public List<CourseDto> searchCourseByName(String name, Pageable pageable) {
//        Page<Course> courseList = courseRepository.findByName(name, pageable);
//
//        List<CourseDto> courseDtos = new ArrayList<>();
//
//        if (courseList != null && !courseList.isEmpty() ) {
//            for (Course course:courseList) {
//                courseDtos.add(mapToDto(course));
//            }
//        }
//
//        return courseDtos;
//    }

    @Override
    public CourseResponse getCourseByPage(String name, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Course> courseData = courseRepository.findByNameContainingIgnoreCase(name, pageable);

//      Map to CourseResponse
        List<Course> listCourseData = courseData.getContent();
        List<CourseDto> content = new ArrayList<>();
        for (Course course:listCourseData) {
            content.add(mapToDto(course));
        }
        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setContent(content);
        courseResponse.setPageNo(courseData.getNumber());
        courseResponse.setPageSize(courseData.getSize());
        courseResponse.setTotalPages(courseData.getTotalPages());
        courseResponse.setTotalElements(courseData.getTotalElements());
        courseResponse.setLast(courseData.isLast());

        return courseResponse;
    }

    @Override
    public void deleteCourseById(List<Integer> courseId) {
        courseId.stream().forEach(id -> {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid course id "+ courseId));
            if (course.getStudents() != null) {
                for (Student student : course.getStudents()) {
                    student.getCourses().remove(course);
                }
            }
            courseRepository.delete(course);
        });

    }

    @Override
    public CourseDto updateCourse(Integer courseId, CourseDto courseDto) {
        Course courseData = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid course id "+ courseId));

        courseData.setName(courseDto.getName());
        courseData.setStudents(courseDto.getStudents());

        Course updatedCourse = courseRepository.save(courseData);

        return mapToDto(updatedCourse);
    }

    @Override
    public List<CourseDto> getAllCourses() {
        List<Course> courseList = courseRepository.findAll();

        List<CourseDto> courseDtos = new ArrayList<>();

        if (courseList != null && !courseList.isEmpty() ) {
            for (Course course:courseList) {
                courseDtos.add(mapToDto(course));
            }
        }
        return courseDtos;
    }
}
