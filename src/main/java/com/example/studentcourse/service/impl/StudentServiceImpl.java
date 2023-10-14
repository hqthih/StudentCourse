package com.example.studentcourse.service.impl;

import com.example.studentcourse.dto.StudentDto;
import com.example.studentcourse.dto.StudentResponse;
import com.example.studentcourse.model.Course;
import com.example.studentcourse.model.Role;
import com.example.studentcourse.model.Student;
import com.example.studentcourse.repository.CourseRepository;
import com.example.studentcourse.repository.RoleRepository;
import com.example.studentcourse.repository.StudentRepository;
import com.example.studentcourse.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.rmi.StubNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class StudentServiceImpl implements StudentService, UserDetailsService {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private StudentDto mapToDto(Student student) {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(student.getId());
        studentDto.setAge(student.getAge());
        studentDto.setAddress(student.getAddress());
        studentDto.setName(student.getName());
        studentDto.setCourses(student.getCourses());
        studentDto.setEmail(student.getEmail());
        return studentDto;
    }

    private Student mapToEntity(StudentDto studentDto) {
        Student student = new Student();
        student.setId(studentDto.getId());
        student.setName(studentDto.getName());
        student.setAddress(studentDto.getAddress());
        student.setAge(studentDto.getAge());
        student.setCourses(studentDto.getCourses());
        student.setEmail(studentDto.getEmail());
        return student;
    }

    @Override
    public Student  saveStudent(Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepository.save(student);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToStudent(String studentEmail, String roleName) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND), "Invalid student email "+ studentEmail));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND), "Invalid role "+ roleName));
        student.getRoles().add(role);
        studentRepository.save(student);
    }

    @Override
    public StudentDto createStudent(Student student) {
        Student studentResponse = saveStudent(student);

        addRoleToStudent(studentResponse.getEmail(), "STUDENT");

        return mapToDto(studentResponse);
    }

    @Override
    public StudentDto getStudentById(Integer studentId) {
        Student studentData = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND), "Invalid student id "+ studentId));

        return mapToDto(studentData);
    }


    @Override
    public List<StudentDto> filterStudent(String name, Integer age, String email, String address) {
        List<Student> studentData;
        List<StudentDto> studentDtos = new ArrayList<>();

        if (age == null) {
            studentData = studentRepository.filterWithoutAge(name, email, address);
        } else  {
            studentData = studentRepository.filterWithAge(name, age , email, address);
        }

        if (studentData != null && !studentData.isEmpty() ) {
            for (Student student:studentData) {
                studentDtos.add(mapToDto(student));
            }
        }
        return studentDtos;
    }

    @Override
    public StudentResponse getStudentByPage(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Student> studentData = studentRepository.findAll(pageable);

//      Map to StudentResponse
        List<Student> listStudentData = studentData.getContent();
        List<StudentDto> content = new ArrayList<>();
        for (Student student:listStudentData) {
            content.add(mapToDto(student));
        }
        StudentResponse studentResponse = new StudentResponse();
        studentResponse.setContent(content);
        studentResponse.setPageNo(studentData.getNumber());
        studentResponse.setPageSize(studentData.getSize());
        studentResponse.setTotalPages(studentData.getTotalPages());
        studentResponse.setTotalElements(studentData.getTotalElements());
        studentResponse.setLast(studentData.isLast());

        return studentResponse;
    }

    @Override
    public StudentDto updateStudent(StudentDto studentDto, Integer studentId) {
        Student studentData = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND), "Invalid student id "+ studentId));

        if (studentDto.getName() != null) {
            studentData.setName(studentDto.getName());
        }
        if (studentDto.getAge() != null) {
            studentData.setAge(studentDto.getAge());
        }
        if (studentDto.getAddress() != null) {
            studentData.setAddress(studentDto.getAddress());
        }
        if (studentDto.getEmail() != null) {
            studentData.setEmail(studentDto.getEmail());
        }

        Student studentUpdated = studentRepository.save(studentData);

        return mapToDto(studentUpdated);
    }

    @Override
    public void deleteStudent(List<Integer> studentIds) {
        studentIds.stream().forEach(id -> {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND), "Invalid student id "+ id));
//            if(student.getCourses() != null) {
//                for (Course course: student.getCourses()) {
//                    course.getStudents().remove(student);
//                }
//            }
            studentRepository.delete(student);
        });
    }

    @Override
    public StudentDto registerForCourse(Integer studentId, List<Integer> courseIds) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND), "Invalid student id "+ studentId));

        Set<Course> listCourse = student.getCourses();
        courseIds.stream().forEach(courseId -> {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND), "Invalid course id "+ courseId));

            if (student.getCourses() != null && !student.getCourses().isEmpty()) {
                student.getCourses().stream().forEach(c -> {
                    if (c.getId() != null && Objects.equals(c.getId(), courseId)) {
                        throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "Student is enrolled this course!!");
                    }
                });
            }
            listCourse.add(course);
            student.setCourses(listCourse);
        });

        Student studentSaved = studentRepository.save(student);

        return mapToDto(studentSaved);
    }

    @Override
    public StudentDto cancelCourse(Integer studentId, List<Integer> courseIds) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND), "Invalid student id "+ studentId));

        Set<Course> listCourse = student.getCourses();
//        courseIds.stream().forEach(courseId -> {
//            Course course = courseRepository.findById(courseId)
//                    .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND), "Invalid course id "+ courseId));
//
//            listCourse.add(course);
//            student.setCourses(listCourse);
//        });

        Set<Course> newCourses = new HashSet<>();

        for (Course course: listCourse) {
            if(!courseIds.contains(course.getId())) {
                newCourses.add(course);
            }
        }

        student.setCourses((newCourses));

        Student studentSaved = studentRepository.save(student);

        return mapToDto(studentSaved);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found student!!"));

        return Student.builder()
                .id(student.getId())
                .name(student.getName())
                .email(student.getUsername())
                .roles(student.getRoles())
                .build();
    }
}
