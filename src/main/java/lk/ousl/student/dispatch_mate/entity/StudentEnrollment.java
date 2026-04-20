package lk.ousl.student.dispatch_mate.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_enrollments")
@Data
@NoArgsConstructor
public class StudentEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Integer enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_reg_number")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_code", referencedColumnName = "module_code")
    private Book book;

    // Use 'collected' not 'isCollected' — Lombok Boolean getter fix
    @Column(name = "is_collected")
    private Boolean collected = false;
}
