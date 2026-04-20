package lk.ousl.student.dispatch_mate.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lk.ousl.student.dispatch_mate.entity.Book.BookStatus;

// JPA AttributeConverter bridges between Java enum values and MySQL column strings.
// Without this, JPA would write "Out_of_Stock" to MySQL but the column stores "Out of Stock".

@Converter(autoApply = true)   // applies to ALL BookStatus fields automatically
public class BookStatusConverter implements AttributeConverter<BookStatus, String> {

    @Override
    public String convertToDatabaseColumn(BookStatus status) {
        if (status == null) return null;
        return switch (status) {
            case Available    -> "Available";
            case Out_of_Stock -> "Out of Stock";
        };
    }

    @Override
    public BookStatus convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return switch (dbValue) {
            case "Available"    -> BookStatus.Available;
            case "Out of Stock" -> BookStatus.Out_of_Stock;
            default -> throw new IllegalArgumentException("Unknown BookStatus: " + dbValue);
        };
    }
}
