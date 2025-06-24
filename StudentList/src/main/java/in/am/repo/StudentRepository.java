package in.am.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import in.am.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
	
	
}
