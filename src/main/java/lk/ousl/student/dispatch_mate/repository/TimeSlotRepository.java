package lk.ousl.student.dispatch_mate.repository;

import lk.ousl.student.dispatch_mate.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    // Only show slots that still have space (for the student booking page)
    List<TimeSlot> findByCurrentBookingsLessThan(int maxCapacity);
}
