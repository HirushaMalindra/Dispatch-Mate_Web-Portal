package lk.ousl.student.dispatch_mate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Used on the admin "Update Stock" form.

@Data
public class UpdateStockRequest {

    @NotNull(message = "Please enter a quantity")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer newStock;
}
