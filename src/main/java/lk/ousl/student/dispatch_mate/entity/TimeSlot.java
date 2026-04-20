package lk.ousl.student.dispatch_mate.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

// Represents a physical collection time window at the OUSL library.
// e.g. "2026-05-12, 09:00 – 10:00, capacity 30"

@Entity
@Table(name = "time_slots")
@Data
@NoArgsConstructor
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Integer slotId;

    // LocalDate maps to MySQL DATE
    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    // LocalTime maps to MySQL TIME
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "current_bookings")
    private Integer currentBookings = 0;

    // ── Helper: is there still room? ──────────────────────────
    public boolean hasAvailableSpace() {
        return currentBookings < maxCapacity;
    }

    // ── Helper: how many seats remain ─────────────────────────
    public int getRemainingSlots() {
        return maxCapacity - currentBookings;
    }
}
