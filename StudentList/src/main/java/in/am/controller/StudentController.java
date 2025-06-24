package in.am.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.am.entity.Student;
import in.am.service.StudentService;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/studentlist")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }
    
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return ResponseEntity.noContent().build(); 
    }
    
    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Student student = studentService.updateStudent(id, updatedStudent);
        return ResponseEntity.ok(student);
    }


}
