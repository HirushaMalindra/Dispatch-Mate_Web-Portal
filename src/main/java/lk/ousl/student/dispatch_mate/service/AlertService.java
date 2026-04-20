package lk.ousl.student.dispatch_mate.service;

import lk.ousl.student.dispatch_mate.entity.Book;
import lk.ousl.student.dispatch_mate.entity.RestockAlert;
import lk.ousl.student.dispatch_mate.entity.Student;
import lk.ousl.student.dispatch_mate.repository.BookRepository;
import lk.ousl.student.dispatch_mate.repository.RestockAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final RestockAlertRepository alertRepository;
    private final BookRepository         bookRepository;

    // ── Student subscribes to restock alert ───────────────────
    @Transactional
    public RestockAlert subscribe(Student student, Integer bookId) {

        // 1. Find the book — throw if it doesn't exist
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Book not found with ID: " + bookId));

        // 2. Don't subscribe if book is already in stock
        if (book.getCurrentStock() > 0) {
            throw new IllegalStateException(
                    "\"" + book.getTitle() + "\" is already in stock. You can collect it now.");
        }

        // 3. Prevent duplicate subscriptions
        Optional<RestockAlert> duplicate = alertRepository.findByStudentAndBook(student, book);
        if (duplicate.isPresent()) {
            throw new IllegalStateException(
                    "You are already subscribed for restock alerts on this book.");
        }

        // 4. Save the alert
        RestockAlert alert = new RestockAlert();
        alert.setStudent(student);
        alert.setBook(book);
        alert.setAlertStatus(RestockAlert.AlertStatus.Pending);
        return alertRepository.save(alert);
    }

    // ── Admin: view all alerts ─────────────────────────────────
    public List<RestockAlert> getAllAlerts() {
        return alertRepository.findAllWithDetails();
    }

    // ── Admin: manually mark an alert as notified ─────────────
    @Transactional
    public void markNotified(Integer alertId) {
        RestockAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found."));
        alert.setAlertStatus(RestockAlert.AlertStatus.Notified);
        alertRepository.save(alert);
    }
}
