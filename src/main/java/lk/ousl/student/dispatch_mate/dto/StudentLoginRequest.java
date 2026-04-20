package lk.ousl.student.dispatch_mate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// This object gets populated when the student submits the login form.
// @NotBlank = Validation: field cannot be empty (shown as form errors in Thymeleaf).

@Data
public class StudentLoginRequest {

    @NotBlank(message = "Registration number is required")
    private String regNumber;

    @NotBlank(message = "Password is required")
    private String password;
}
