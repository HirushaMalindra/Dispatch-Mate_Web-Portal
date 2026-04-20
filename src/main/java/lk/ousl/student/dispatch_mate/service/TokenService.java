package lk.ousl.student.dispatch_mate.service;

import lk.ousl.student.dispatch_mate.entity.Student;
import lk.ousl.student.dispatch_mate.entity.TimeSlot;
import lk.ousl.student.dispatch_mate.entity.Token;
import lk.ousl.student.dispatch_mate.repository.TimeSlotRepository;
import lk.ousl.student.dispatch_mate.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository    tokenRepository;
    private final TimeSlotRepository timeSlotRepository;

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RNG = new SecureRandom();

    private String generateTokenString() {
        StringBuilder sb = new StringBuilder("TK-");
        for (int i = 0; i < 3; i++) sb.append(CHARS.charAt(RNG.nextInt(CHARS.length())));
        sb.append("-");
        for (int i = 0; i < 5; i++) sb.append(CHARS.charAt(RNG.nextInt(CHARS.length())));
        return sb.toString();
    }

    @Transactional
    public Token generateToken(Student student, Integer slotId) {
        Optional<Token> existing = tokenRepository.findFirstByStudent(student);
        if (existing.isPresent()) {
            throw new IllegalStateException(
                "You already have a collection token: " + existing.get().getTokenString()
                + ". Please use or cancel it before generating a new one.");
        }

        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found."));

        if (!slot.hasAvailableSpace()) {
            throw new IllegalStateException(
                "That time slot is fully booked. Please choose another.");
        }

        Token token = new Token();
        token.setStudent(student);
        token.setTimeSlot(slot);
        token.setTokenString(generateTokenString());
        token.setUsed(false);

        slot.setCurrentBookings(slot.getCurrentBookings() + 1);
        timeSlotRepository.save(slot);

        return tokenRepository.save(token);
    }

    public Optional<Token> getTokenForStudent(Student student) {
        return tokenRepository.findFirstByStudent(student);
    }

    public Optional<Token> findByTokenString(String tokenString) {
        if (tokenString == null || tokenString.isBlank()) {
            throw new IllegalArgumentException("Token cannot be empty.");
        }
        return tokenRepository.findByTokenString(tokenString.trim().toUpperCase());
    }

    @Transactional
    public void markTokenAsUsed(Integer tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new IllegalArgumentException("Token not found."));

        if (Boolean.TRUE.equals(token.getUsed())) {
            throw new IllegalStateException("This token has already been used.");
        }

        token.setUsed(true);
        tokenRepository.save(token);
    }

    public List<Token> getAllTokensWithDetails() {
        return tokenRepository.findAllWithDetails();
    }
}
