package lk.ousl.student.dispatch_mate.service;

import lk.ousl.student.dispatch_mate.entity.Student;
import lk.ousl.student.dispatch_mate.entity.StudentEnrollment;
import lk.ousl.student.dispatch_mate.repository.StudentEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final StudentEnrollmentRepository enrollmentRepository;

    public List<StudentEnrollment> getEnrollmentsForStudent(Student student) {
        return enrollmentRepository.findByStudent(student);
    }

    @Transactional
    public void markBookCollected(Integer enrollmentId) {
        StudentEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Enrollment record not found: " + enrollmentId));

        if (Boolean.TRUE.equals(enrollment.getCollected())) {
            throw new IllegalStateException("This book has already been marked as collected.");
        }

        enrollment.setCollected(true);
        enrollmentRepository.save(enrollment);
    }

    public List<StudentEnrollment> getAllEnrollments() {
        return enrollmentRepository.findAllWithDetails();
    }
}
