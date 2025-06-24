package in.am.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import in.am.entity.Student;
import in.am.repo.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepo;

    public StudentServiceImpl(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }
    
    @Override
    public void deleteStudentById(Long id) {
        if (!studentRepo.existsById(id)) {
            throw new RuntimeException("Student not found with ID: " + id);
        }
        studentRepo.deleteById(id);
    }
    
    @Override
    public Student updateStudent(Long id, Student updatedStudent) {
        // Find existing student
        Optional<Student> existingOpt = studentRepo.findById(id);

        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Student not found with ID: " + id);
        }

        Student existingStudent = existingOpt.get();

        // Update fields
        existingStudent.setName(updatedStudent.getName());
        existingStudent.setAge(updatedStudent.getAge());
        existingStudent.setCourse(updatedStudent.getCourse());

        // Save updated student
        return studentRepo.save(existingStudent);
    }
}




