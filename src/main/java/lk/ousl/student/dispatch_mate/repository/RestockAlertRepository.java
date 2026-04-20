package lk.ousl.student.dispatch_mate.repository;

import lk.ousl.student.dispatch_mate.entity.Book;
import lk.ousl.student.dispatch_mate.entity.RestockAlert;
import lk.ousl.student.dispatch_mate.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestockAlertRepository extends JpaRepository<RestockAlert, Integer> {

    // Check for duplicate subscription
    Optional<RestockAlert> findByStudentAndBook(Student student, Book book);

    // Get all Pending alerts for a specific book (used when admin restocks)
    List<RestockAlert> findByBookAndAlertStatus(Book book, RestockAlert.AlertStatus status);

    // Admin view: all alerts with student and book details
    @Query("SELECT a FROM RestockAlert a JOIN FETCH a.student JOIN FETCH a.book")
    List<RestockAlert> findAllWithDetails();
}
