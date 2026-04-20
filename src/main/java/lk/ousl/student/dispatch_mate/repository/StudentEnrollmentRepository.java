package lk.ousl.student.dispatch_mate.repository;

import lk.ousl.student.dispatch_mate.entity.Student;
import lk.ousl.student.dispatch_mate.entity.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Integer> {

    // Get all enrollments for a student (their book list)
    List<StudentEnrollment> findByStudent(Student student);

    // Find a specific enrollment record
    Optional<StudentEnrollment> findByStudentAndBook_ModuleCode(Student student, String moduleCode);

    // Get all enrollments across all students (for admin view)
    // JOIN FETCH eagerly loads student and book to avoid N+1 queries
    @Query("SELECT e FROM StudentEnrollment e JOIN FETCH e.student JOIN FETCH e.book")
    List<StudentEnrollment> findAllWithDetails();

    // Count how many students have collected for a given book
    @Query("SELECT COUNT(e) FROM StudentEnrollment e WHERE e.book.moduleCode = :mc AND e.collected = true")
    long countCollectedByModuleCode(@Param("mc") String moduleCode);
}
