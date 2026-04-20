package lk.ousl.student.dispatch_mate.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// ── @Entity   → maps this class to the 'books' table in MySQL
// ── @Data     → Lombok generates getters, setters, toString, equals, hashCode
// ── @NoArgsConstructor → Lombok generates a no-arg constructor (required by JPA)

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    // module_code is the link between books and student_enrollments
    @Column(name = "module_code", nullable = false, unique = true, length = 20)
    private String moduleCode;

    @Column(name = "current_stock")
    private Integer currentStock = 0;

    // MySQL ENUM mapped to Java enum
    @Column(name = "status")
    private BookStatus status = BookStatus.Available;

    // ── Inner enum matches the MySQL ENUM values exactly ──────
    public enum BookStatus {
        Available,
        Out_of_Stock
    }

    // ── Helper: call this whenever stock changes ───────────────
    public void recalculateStatus() {
        this.status = (this.currentStock > 0) ? BookStatus.Available : BookStatus.Out_of_Stock;
    }
}
