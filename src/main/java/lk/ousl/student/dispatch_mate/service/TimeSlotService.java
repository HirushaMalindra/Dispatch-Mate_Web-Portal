package lk.ousl.student.dispatch_mate.service;

import lk.ousl.student.dispatch_mate.entity.TimeSlot;
import lk.ousl.student.dispatch_mate.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    // ── All slots (admin view — shows full + available) ───────
    public List<TimeSlot> getAllSlots() {
        return timeSlotRepository.findAll();
    }

    // ── Only slots with remaining space (student booking view) ─
    // Uses Java Stream API to filter: keeps slots where hasAvailableSpace() is true
    public List<TimeSlot> getAvailableSlots() {
        return timeSlotRepository.findAll()
                .stream()
                .filter(TimeSlot::hasAvailableSpace)   // method reference shorthand
                .collect(Collectors.toList());
    }

    // ── Find one slot by ID (used when verifying a token) ─────
    public Optional<TimeSlot> getSlotById(Integer id) {
        return timeSlotRepository.findById(id);
    }
}
