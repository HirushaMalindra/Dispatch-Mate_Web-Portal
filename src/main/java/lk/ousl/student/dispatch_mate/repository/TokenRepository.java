package lk.ousl.student.dispatch_mate.repository;

import lk.ousl.student.dispatch_mate.entity.Student;
import lk.ousl.student.dispatch_mate.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    // Find a token by its code string (for admin verification)
    Optional<Token> findByTokenString(String tokenString);

    // Check if a student already has a token
    Optional<Token> findFirstByStudent(Student student);

    // Admin: view all tokens with student + slot info
    @Query("SELECT t FROM Token t JOIN FETCH t.student LEFT JOIN FETCH t.timeSlot")
    List<Token> findAllWithDetails();
}
