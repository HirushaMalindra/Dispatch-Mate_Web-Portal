package lk.ousl.student.dispatch_mate.repository;

import lk.ousl.student.dispatch_mate.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// JpaRepository<Book, Integer> gives us:
//   findAll(), findById(), save(), delete(), count() … for FREE.
// We only add the extra queries our app needs.

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    // SELECT * FROM books WHERE module_code = ?
    Optional<Book> findByModuleCode(String moduleCode);

    // SELECT * FROM books WHERE title LIKE %keyword%
    List<Book> findByTitleContainingIgnoreCase(String keyword);

    // SELECT * FROM books WHERE status = 'Out_of_Stock'
    List<Book> findByStatus(Book.BookStatus status);
}
