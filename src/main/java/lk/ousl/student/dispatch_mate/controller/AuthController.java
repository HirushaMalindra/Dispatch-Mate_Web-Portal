package lk.ousl.student.dispatch_mate.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lk.ousl.student.dispatch_mate.config.SessionInterceptor;
import lk.ousl.student.dispatch_mate.dto.AdminLoginRequest;
import lk.ousl.student.dispatch_mate.dto.StudentLoginRequest;
import lk.ousl.student.dispatch_mate.entity.Staff;
import lk.ousl.student.dispatch_mate.entity.Student;
import lk.ousl.student.dispatch_mate.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

// @Controller = Spring MVC controller that returns VIEW NAMES (Thymeleaf templates).
// (Not @RestController — that returns JSON. We want HTML pages.)

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── GET / → redirect to /login ─────────────────────────────
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // ── GET /login → show the login page ──────────────────────
    @GetMapping("/login")
    public String showLogin(Model model,
                            @RequestParam(required = false) String error,
                            @RequestParam(required = false) String type) {
        // Pre-populate the form objects so Thymeleaf can bind them
        model.addAttribute("studentLogin", new StudentLoginRequest());
        model.addAttribute("adminLogin",   new AdminLoginRequest());
        // Pass error message from redirect (e.g. "Please log in first")
        if (error != null) model.addAttribute("errorMsg", error);
        // Tell the template which tab to activate (student or admin)
        model.addAttribute("activeTab", type != null ? type : "student");
        return "auth/login";  // → src/main/resources/templates/auth/login.html
    }

    // ── POST /login/student → authenticate student ─────────────
    @PostMapping("/login/student")
    public String loginStudent(
            @Valid @ModelAttribute("studentLogin") StudentLoginRequest form,
            BindingResult errors,   // holds @Valid failures
            Model model,
            HttpSession session) {

        // If @NotBlank failed, re-show the form with error messages
        if (errors.hasErrors()) {
            model.addAttribute("adminLogin", new AdminLoginRequest());
            model.addAttribute("activeTab", "student");
            return "auth/login";
        }

        Optional<Student> student = authService.loginStudent(form.getRegNumber(), form.getPassword());

        if (student.isEmpty()) {
            // Wrong credentials
            model.addAttribute("adminLogin",  new AdminLoginRequest());
            model.addAttribute("activeTab",   "student");
            model.addAttribute("errorMsg",    "Invalid registration number or password.");
            return "auth/login";
        }

        // SUCCESS: store student in session, redirect to portal
        session.setAttribute(SessionInterceptor.SESSION_STUDENT, student.get());
        return "redirect:/student/dashboard";
    }

    // ── POST /login/admin → authenticate admin / staff ─────────
    @PostMapping("/login/admin")
    public String loginAdmin(
            @Valid @ModelAttribute("adminLogin") AdminLoginRequest form,
            BindingResult errors,
            Model model,
            HttpSession session) {

        if (errors.hasErrors()) {
            model.addAttribute("studentLogin", new StudentLoginRequest());
            model.addAttribute("activeTab", "admin");
            return "auth/login";
        }

        Optional<Staff> staff = authService.loginStaff(form.getEmail(), form.getPassword());

        if (staff.isEmpty()) {
            model.addAttribute("studentLogin", new StudentLoginRequest());
            model.addAttribute("activeTab",  "admin");
            model.addAttribute("errorMsg",   "Invalid email or password.");
            return "auth/login";
        }

        session.setAttribute(SessionInterceptor.SESSION_STAFF, staff.get());
        return "redirect:/admin/dashboard";
    }

    // ── GET /logout → clear session, go back to login ──────────
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();  // destroys the session and all its attributes
        ra.addFlashAttribute("successMsg", "You have been logged out successfully.");
        return "redirect:/login";
    }
}
