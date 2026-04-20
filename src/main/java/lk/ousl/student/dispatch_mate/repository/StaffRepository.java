package lk.ousl.student.dispatch_mate.repository;

import lk.ousl.student.dispatch_mate.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    // Used for admin login: SELECT * FROM staff WHERE email = ? AND password_hash = ?
    Optional<Staff> findByEmailAndPasswordHash(String email, String passwordHash);
}
