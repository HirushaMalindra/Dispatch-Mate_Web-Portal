package lk.ousl.student.dispatch_mate.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lk.ousl.student.dispatch_mate.config.SessionInterceptor;
import lk.ousl.student.dispatch_mate.dto.UpdateStockRequest;
import lk.ousl.student.dispatch_mate.entity.*;
import lk.ousl.student.dispatch_mate.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookService       bookService;
    private final TokenService      tokenService;
    private final EnrollmentService enrollmentService;
    private final AlertService      alertService;
    private final TimeSlotService   timeSlotService;

    private Staff getSessionStaff(HttpSession session) {
        return (Staff) session.getAttribute(SessionInterceptor.SESSION_STAFF);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Staff staff = getSessionStaff(session);
        List<Token> allTokens = tokenService.getAllTokensWithDetails();
        long usedCount    = allTokens.stream().filter(t -> Boolean.TRUE.equals(t.getUsed())).count();
        long activeCount  = allTokens.stream().filter(t -> !Boolean.TRUE.equals(t.getUsed())).count();

        model.addAttribute("staff",        staff);
        model.addAttribute("totalBooks",   bookService.getAllBooks().size());
        model.addAttribute("activeTokens", activeCount);
        model.addAttribute("usedTokens",   usedCount);
        model.addAttribute("totalAlerts",  alertService.getAllAlerts().size());
        model.addAttribute("recentTokens", allTokens.stream().limit(5).toList());
        return "admin/dashboard";
    }

    @GetMapping("/books")
    public String books(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("books",   bookService.searchByTitle(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Book not found.");
            return "redirect:/admin/books";
        }
        model.addAttribute("book",               book.get());
        model.addAttribute("updateStockRequest", new UpdateStockRequest());
        return "admin/edit-book";
    }

    @PostMapping("/books/{id}/update-stock")
    public String updateStock(@PathVariable Integer id,
                              @Valid @ModelAttribute("updateStockRequest") UpdateStockRequest form,
                              BindingResult errors,
                              Model model,
                              RedirectAttributes ra) {
        if (errors.hasErrors()) {
            model.addAttribute("book", bookService.getBookById(id).orElse(null));
            return "admin/edit-book";
        }
        try {
            bookService.updateStock(id, form.getNewStock());
            ra.addFlashAttribute("successMsg", "Stock updated successfully.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/books";
    }

    @GetMapping("/tokens")
    public String tokens(Model model) {
        model.addAttribute("tokens", tokenService.getAllTokensWithDetails());
        return "admin/tokens";
    }

    @GetMapping("/tokens/verify")
    public String showVerifyForm(@RequestParam(required = false) String prefill, Model model) {
        model.addAttribute("tokenString", prefill != null ? prefill : "");
        return "admin/verify-token";
    }

    @PostMapping("/tokens/verify")
    public String verifyToken(@RequestParam String tokenString,
                              Model model, RedirectAttributes ra) {
        try {
            Optional<Token> found = tokenService.findByTokenString(tokenString);
            if (found.isEmpty()) {
                model.addAttribute("errorMsg",    "Token not found: " + tokenString);
                model.addAttribute("tokenString", tokenString);
                return "admin/verify-token";
            }
            Token token = found.get();
            List<StudentEnrollment> enrollments =
                enrollmentService.getEnrollmentsForStudent(token.getStudent());
            model.addAttribute("token",       token);
            model.addAttribute("enrollments", enrollments);
            return "admin/token-detail";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg",    e.getMessage());
            model.addAttribute("tokenString", tokenString);
            return "admin/verify-token";
        }
    }

    @PostMapping("/tokens/{id}/mark-used")
    public String markTokenUsed(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            tokenService.markTokenAsUsed(id);
            ra.addFlashAttribute("successMsg", "Token marked as used. Collection complete.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/tokens";
    }

    @PostMapping("/enrollments/{id}/mark-collected")
    public String markEnrollmentCollected(@PathVariable Integer id,
                                          @RequestParam(required = false) String tokenString,
                                          RedirectAttributes ra) {
        try {
            enrollmentService.markBookCollected(id);
            ra.addFlashAttribute("successMsg", "Book marked as collected.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        if (tokenString != null && !tokenString.isBlank()) {
            return "redirect:/admin/tokens/verify?prefill=" + tokenString;
        }
        return "redirect:/admin/tokens";
    }

    @GetMapping("/alerts")
    public String alerts(Model model) {
        model.addAttribute("alerts", alertService.getAllAlerts());
        return "admin/alerts";
    }

    @PostMapping("/alerts/{id}/mark-notified")
    public String markNotified(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            alertService.markNotified(id);
            ra.addFlashAttribute("successMsg", "Alert marked as notified.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/alerts";
    }

    @GetMapping("/enrollments")
    public String enrollments(Model model) {
        model.addAttribute("enrollments", enrollmentService.getAllEnrollments());
        return "admin/enrollments";
    }

    @GetMapping("/slots")
    public String slots(Model model) {
        model.addAttribute("slots", timeSlotService.getAllSlots());
        return "admin/slots";
    }
}
