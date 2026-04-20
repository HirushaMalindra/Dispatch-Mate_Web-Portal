package lk.ousl.student.dispatch_mate.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// @ControllerAdvice lets this class catch exceptions thrown by ANY controller.
// Instead of each controller having try-catch for every possible error,
// we handle them here in one place.

@ControllerAdvice
public class GlobalExceptionHandler {

    // Catches any unhandled IllegalArgumentException across all controllers
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorTitle",   "Invalid Request");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/general";
    }

    // Catches any unhandled IllegalStateException
    @ExceptionHandler(IllegalStateException.class)
    public String handleBadState(IllegalStateException ex, Model model) {
        model.addAttribute("errorTitle",   "Action Not Allowed");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/general";
    }

    // Catches any other unexpected exception (safety net)
    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("errorTitle",   "Something went wrong");
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        return "error/general";
    }
}
