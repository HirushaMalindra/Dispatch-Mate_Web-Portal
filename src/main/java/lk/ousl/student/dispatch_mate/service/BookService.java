package lk.ousl.student.dispatch_mate.service;

import lk.ousl.student.dispatch_mate.entity.Book;
import lk.ousl.student.dispatch_mate.entity.RestockAlert;
import lk.ousl.student.dispatch_mate.repository.BookRepository;
import lk.ousl.student.dispatch_mate.repository.RestockAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository       bookRepository;
    private final RestockAlertRepository alertRepository;

    // ── Get all books (for catalogue pages) ───────────────────
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // ── Get one book by its database ID ───────────────────────
    public Optional<Book> getBookById(Integer id) {
        return bookRepository.findById(id);
    }

    // ── Search by title keyword ────────────────────────────────
    public List<Book> searchByTitle(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllBooks();
        return bookRepository.findByTitleContainingIgnoreCase(keyword.trim());
    }

    // ── Out-of-stock books (for alert subscription list) ───────
    public List<Book> getOutOfStockBooks() {
        return bookRepository.findByStatus(Book.BookStatus.Out_of_Stock);
    }

    // ── Admin: update a book's stock ──────────────────────────
    // @Transactional = if anything goes wrong mid-method, the DB change is rolled back.
    @Transactional
    public void updateStock(Integer bookId, Integer newStock) {
        // findById returns Optional; orElseThrow throws exception if not found
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Book not found with ID: " + bookId));

        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }

        int oldStock = book.getCurrentStock();
        book.setCurrentStock(newStock);
        book.recalculateStatus();           // update Available / Out_of_Stock
        bookRepository.save(book);

        // If stock was INCREASED from zero → notify subscribers
        if (oldStock == 0 && newStock > 0) {
            notifyRestockSubscribers(book);
        }
    }

    // ── Internal: mark all Pending alerts for this book as Notified ──
    @Transactional
    private void notifyRestockSubscribers(Book book) {
        List<RestockAlert> pending = alertRepository.findByBookAndAlertStatus(
                book, RestockAlert.AlertStatus.Pending);

        for (RestockAlert alert : pending) {
            alert.setAlertStatus(RestockAlert.AlertStatus.Notified);
            alertRepository.save(alert);
            // In a real app, you would also send an email here using JavaMailSender
        }
    }
}
