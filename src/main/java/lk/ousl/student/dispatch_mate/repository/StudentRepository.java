package lk.ousl.student.dispatch_mate.repository;

import lk.ousl.student.dispatch_mate.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    // Primary key type is String because reg_number is a VARCHAR

    // Used for login: SELECT * FROM students WHERE reg_number = ? AND password_hash = ?
    Optional<Student> findByRegNumberAndPasswordHash(String regNumber, String passwordHash);
}
