package lk.ousl.student.dispatch_mate.service;

import lk.ousl.student.dispatch_mate.entity.Staff;
import lk.ousl.student.dispatch_mate.entity.Student;
import lk.ousl.student.dispatch_mate.repository.StaffRepository;
import lk.ousl.student.dispatch_mate.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

// @Service marks this as a Spring-managed service bean.
// @RequiredArgsConstructor = Lombok generates a constructor that injects all final fields.

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;
    private final StaffRepository   staffRepository;

    // ── Student login ──────────────────────────────────────────
    // Returns the Student if credentials match, or empty Optional if not.
    public Optional<Student> loginStudent(String regNumber, String password) {
        if (regNumber == null || regNumber.isBlank() ||
            password  == null || password.isBlank()) {
            return Optional.empty();
        }
        // The repository compares reg_number AND password_hash in one SQL query
        return studentRepository.findByRegNumberAndPasswordHash(
                regNumber.trim(), password.trim());
    }

    // ── Admin / Staff login ────────────────────────────────────
    public Optional<Staff> loginStaff(String email, String password) {
        if (email   == null || email.isBlank() ||
            password == null || password.isBlank()) {
            return Optional.empty();
        }
        return staffRepository.findByEmailAndPasswordHash(
                email.trim(), password.trim());
    }
}
