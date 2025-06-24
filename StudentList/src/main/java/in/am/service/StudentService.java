package in.am.service;

import java.util.List;

import in.am.entity.Student;

public interface StudentService {
	
    List<Student> getAllStudents();
    void deleteStudentById(Long id);
    Student updateStudent(Long id, Student updatedStudent);
}
