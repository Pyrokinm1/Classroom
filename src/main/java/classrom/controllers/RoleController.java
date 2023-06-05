package classrom.controllers;

import classrom.models.Role;
import classrom.models.Student;
import classrom.repositories.StudentRepository;
import classrom.repositories.TeacherRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RoleController {

    private final StudentRepository studentRepository;

    public RoleController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    @PostMapping("/main")
    public String getMainPagePost() {
        return switch (getRoleType(SecurityContextHolder.getContext().getAuthentication())) {
            case TEACHER -> "redirect:/group/index";
            case STUDENT -> "redirect:/student/card";
            default -> "redirect:/login";
        };
    }

    @GetMapping("/main")
    public String getMainPageGet() {
        return switch (getRoleType(SecurityContextHolder.getContext().getAuthentication())) {
            case TEACHER -> "redirect:/group/index";
            case STUDENT -> "redirect:/student/card";
            default -> "redirect:/login";
        };
    }

    public Role getRoleType(Authentication auth) {
        Student student = studentRepository.findStudentByEmail(auth.getName());
        if (student != null) return Role.STUDENT;
        else return Role.TEACHER;
    }
}
