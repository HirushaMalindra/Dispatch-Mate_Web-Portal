package lk.ousl.student.dispatch_mate.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Integer tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_reg_number")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = true)
    private TimeSlot timeSlot;

    @Column(name = "token_string", nullable = false, unique = true, length = 100)
    private String tokenString;

    // Renamed to 'used' — Lombok generates isUsed() getter for boolean,
    // but Thymeleaf/Spring need getUsed() for Boolean wrapper type.
    // Using 'used' (Boolean wrapper) gives getUsed() which works in both.
    @Column(name = "is_used")
    private Boolean used = false;
}
