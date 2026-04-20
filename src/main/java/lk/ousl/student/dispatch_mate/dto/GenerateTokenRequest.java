package lk.ousl.student.dispatch_mate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Student chooses a time slot, then submits this form to generate their token.

@Data
public class GenerateTokenRequest {

    @NotNull(message = "Please select a time slot")
    private Integer slotId;
}
