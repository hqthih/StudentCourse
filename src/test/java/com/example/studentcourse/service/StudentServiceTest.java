package com.example.studentcourse.service;

import com.example.studentcourse.dto.StudentDto;
import com.example.studentcourse.dto.StudentResponse;
import com.example.studentcourse.model.Course;
import com.example.studentcourse.model.Role;
import com.example.studentcourse.model.Student;
import com.example.studentcourse.repository.CourseRepository;
import com.example.studentcourse.repository.RoleRepository;
import com.example.studentcourse.repository.StudentRepository;
import com.example.studentcourse.service.impl.StudentServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertAll;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
public class StudentServiceTest {

    @Mock PasswordEncoder passwordEncoder;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private StudentDto studentDto;
    private StudentResponse studentResponse;
    private Course course;
    private Role role;

    @BeforeEach
    public void init() {

        student = Student.builder().id(1).age(21)
                .name("Ha Quoc Dat").address("648 Tay son")
                .email("hqdat0809@gmail.com")
                .password("0392338494").roles(new HashSet<>()).courses(new HashSet<>()).build();
        studentDto = StudentDto.builder().age(21)
                .name("Ha Quoc Dat").address("648 Tay son")
                .email("hqdat0809@gmail.com")
                .build();
         course = Course.builder().name("ABC").build();
        role = Role.builder().name("STUDENT").build();

    }

    @Test
    public void CreateStudentReturnStudentDto() {

        Mockito.when(studentRepository.save(Mockito.any(Student.class))).thenReturn(student);
        Mockito.when(studentRepository.findByEmail(Mockito.any(String.class))).thenReturn(Optional.ofNullable(student));
        Mockito.when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.ofNullable(role));


        StudentDto studentReturn = studentService.createStudent(student);
        Assertions.assertThat(studentReturn).isNotNull();
        Assertions.assertThat(studentReturn.getId()).isGreaterThan(0);
    }

    @Test
    public void GetStudentByIdReturnStudentDto() {
        Mockito.when(studentRepository.findById(1)).thenReturn(Optional.ofNullable(student));

        StudentDto studentReturn = studentService.getStudentById(1);

        Assertions.assertThat(studentReturn).isNotNull();
        Assertions.assertThat(studentReturn.getId()).isGreaterThan(0);
    }

    @Test
    public void GetStudentByPageReturnStudentResponse() {
        Page<Student> students = Mockito.mock(Page.class);

        Mockito.when(studentRepository.findAll(Mockito.any(Pageable.class))).thenReturn(students);

        StudentResponse studentsReturn = studentService.getStudentByPage(1, 10);
        Assertions.assertThat(studentsReturn).isNotNull();
    }

    @Test
    public void UpdateStudentReturnStudentDto() {
        Mockito.when(studentRepository.findById(1)).thenReturn(Optional.ofNullable(student));
        Mockito.when(studentRepository.save(Mockito.any(Student.class))).thenReturn(student);

        StudentDto studentReturn = studentService.updateStudent(studentDto, 1);

        Assertions.assertThat(studentReturn).isNotNull();
    }

    @Test
    public void DeleteStudentReturnVoid() {

        assertAll(() -> studentService.deleteStudent(new ArrayList<>(1)));
    }

    @Test
    public void RegisterForCourseReturnStudent() {
        Mockito.when(studentRepository.findById(1)).thenReturn(Optional.ofNullable(student));
        Mockito.when(studentRepository.save(Mockito.any(Student.class))).thenReturn(student);

        StudentDto studentReturn = studentService.registerForCourse(1, new ArrayList<>(1));

        Assertions.assertThat(studentReturn).isNotNull();
        Assertions.assertThat(studentReturn.getCourses()).isNotNull();
    }
}
