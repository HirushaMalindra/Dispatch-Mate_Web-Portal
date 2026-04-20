package lk.ousl.student.dispatch_mate.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lk.ousl.student.dispatch_mate.config.SessionInterceptor;
import lk.ousl.student.dispatch_mate.dto.GenerateTokenRequest;
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
@RequestMapping("/student")   // all routes in this class start with /student
@RequiredArgsConstructor
public class StudentController {

    private final EnrollmentService enrollmentService;
    private final BookService       bookService;
    private final TokenService      tokenService;
    private final TimeSlotService   timeSlotService;
    private final AlertService      alertService;

    // ── Helper: get the logged-in student from the session ─────
    // Every method below calls this to know WHO is making the request.
    private Student getSessionStudent(HttpSession session) {
        return (Student) session.getAttribute(SessionInterceptor.SESSION_STUDENT);
    }

    // ==========================================================
    //  GET /student/dashboard
    // ==========================================================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Student student = getSessionStudent(session);
        // Load this student's enrolled books
        List<StudentEnrollment> enrollments = enrollmentService.getEnrollmentsForStudent(student);
        // Load their token if they have one
        Optional<Token> token = tokenService.getTokenForStudent(student);

        model.addAttribute("student",     student);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("token",       token.orElse(null));
        return "student/dashboard";
    }

    // ==========================================================
    //  GET /student/books  — browse / search catalogue
    // ==========================================================
    @GetMapping("/books")
    public String books(@RequestParam(required = false) String keyword,
                        Model model) {
        List<Book> books = bookService.searchByTitle(keyword);
        model.addAttribute("books",   books);
        model.addAttribute("keyword", keyword);
        return "student/books";
    }

    // ==========================================================
    //  GET /student/token  — view my token card
    // ==========================================================
    @GetMapping("/token")
    public String viewToken(HttpSession session, Model model) {
        Student student = getSessionStudent(session);
        Optional<Token> token = tokenService.getTokenForStudent(student);
        model.addAttribute("student", student);
        model.addAttribute("token",   token.orElse(null));
        return "student/token";
    }

    // ==========================================================
    //  GET /student/token/new  — show the "pick a slot" page
    // ==========================================================
    @GetMapping("/token/new")
    public String showGenerateTokenForm(HttpSession session, Model model,
                                        RedirectAttributes ra) {
        Student student = getSessionStudent(session);

        // If student already has a token, redirect them to view it
        Optional<Token> existing = tokenService.getTokenForStudent(student);
        if (existing.isPresent()) {
            ra.addFlashAttribute("errorMsg",
                "You already have an active token. View it below.");
            return "redirect:/student/token";
        }

        List<TimeSlot> availableSlots = timeSlotService.getAvailableSlots();
        model.addAttribute("slots",        availableSlots);
        model.addAttribute("tokenRequest", new GenerateTokenRequest());
        return "student/select-slot";
    }

    // ==========================================================
    //  POST /student/token/generate  — create token after slot chosen
    // ==========================================================
    @PostMapping("/token/generate")
    public String generateToken(
            @Valid @ModelAttribute("tokenRequest") GenerateTokenRequest form,
            BindingResult errors,
            HttpSession session,
            Model model,
            RedirectAttributes ra) {

        if (errors.hasErrors()) {
            model.addAttribute("slots", timeSlotService.getAvailableSlots());
            return "student/select-slot";
        }

        Student student = getSessionStudent(session);
        try {
            Token token = tokenService.generateToken(student, form.getSlotId());
            // Flash attribute survives the redirect and shows a success banner
            ra.addFlashAttribute("successMsg",
                "Token generated successfully: " + token.getTokenString());
            return "redirect:/student/token";

        } catch (IllegalStateException | IllegalArgumentException e) {
            // Service threw an exception — show the error on the same page
            model.addAttribute("slots",    timeSlotService.getAvailableSlots());
            model.addAttribute("errorMsg", e.getMessage());
            return "student/select-slot";
        }
    }

    // ==========================================================
    //  GET /student/alerts  — subscribe to restock alert page
    // ==========================================================
    @GetMapping("/alerts")
    public String showAlertPage(Model model) {
        List<Book> outOfStock = bookService.getOutOfStockBooks();
        model.addAttribute("outOfStockBooks", outOfStock);
        return "student/alerts";
    }

    // ==========================================================
    //  POST /student/alerts/subscribe
    // ==========================================================
    @PostMapping("/alerts/subscribe")
    public String subscribeAlert(@RequestParam Integer bookId,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Student student = getSessionStudent(session);
        try {
            alertService.subscribe(student, bookId);
            ra.addFlashAttribute("successMsg",
                "Subscribed! You will be notified when this book is back in stock.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/student/alerts";
    }
}
