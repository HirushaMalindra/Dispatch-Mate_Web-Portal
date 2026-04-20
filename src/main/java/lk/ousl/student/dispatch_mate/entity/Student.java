package lk.ousl.student.dispatch_mate.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
public class Student {

    // Primary key is NOT auto-increment — it's the student reg number like "526774619"
    @Id
    @Column(name = "reg_number", length = 20)
    private String regNumber;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "academic_year", length = 10)
    private String academicYear;

    // Stored as plain text in your DB. In production you'd use BCrypt.
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // One student → many enrollments (OneToMany relationship)
    // mappedBy = "student" means the foreign key is on the StudentEnrollment side
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<StudentEnrollment> enrollments;

    // One student → at most one token
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Token> tokens;
}
