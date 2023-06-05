package classrom.controllers;

import classrom.models.Group;
import classrom.models.Student;
import classrom.repositories.GroupRepository;
import classrom.repositories.StudentRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@PreAuthorize("hasAnyAuthority('TEACHER')")
@Controller
public class MessageController {

    private final JavaMailSender mailSender;

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;

    public MessageController(JavaMailSender mailSender, StudentRepository studentRepository, GroupRepository groupRepository) {
        this.mailSender = mailSender;
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
    }

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestParam(required = false) String textEmail, @RequestParam Student student, Model model) {
        createAndSendMessage(student, textEmail);
        model.addAttribute("student", student);
        return "student/StudentCardEdit";
    }

    @PostMapping("/sendEmails")
    public String sendEmails(@RequestParam(required = false) String textEmail, @RequestParam boolean check, @RequestParam Group group, Model model) {
        Iterable<Student> students = studentRepository.findAllByGroup(group);
        if (!check) {
            List<Student> studentList = new ArrayList<>();
            for (Student student : students) {
                if (student.getRusLang() == 2 || student.getMath() == 2 || student.getOBJ() == 2 ||
                        student.getOPD() == 2 || student.getPhysEducation() == 2 || student.getAstronomy() == 2 ||
                        student.getLiterature() == 2 || student.getNatLiterature() == 2 || student.getEngLang() == 2 ||
                        student.getHistory() == 2 || student.getSocialStudies() == 2 || student.getInformatics() == 2 || student.getPhysics() == 2) {
                    studentList.add(student);
                }
            }
            students = studentList;
        }
        for (Student student : students) {
            if (student.getParentEmail() != null) createAndSendMessage(student, textEmail);
        }
        model.addAttribute("students", students);
        model.addAttribute("group", group);
        model.addAttribute("check", false);
        return "student/Index";
    }

    @PostMapping("/sendAllEmails")
    public String sendAllEmails(@RequestParam(required = false) String textEmail, Model model) {
        Iterable<Student> students = studentRepository.findAll();
        for (Student student : students) {
            if (student.getParentEmail() != null) createAndSendMessage(student, textEmail);
        }
        model.addAttribute("groups", groupRepository.findAll());
        return "group/Index";
    }

    private void createAndSendMessage(Student student, String textEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("eduardkutermin@gmail.com");
        message.setTo(student.getParentEmail());
        String fullTextEmail = "Студент: " + student.getSurname() + " " + student.getName() + " " + student.getPatronymic() + "\n" +
                "Русский язык: " + student.getRusLang() + "\n" +
                "Математика: " + student.getMath() + "\n" +
                "Литература: " + student.getLiterature() + "\n" +
                "Родная литература: " + student.getNatLiterature() + "\n" +
                "Иностранный язык: " + student.getEngLang() + "\n" +
                "История: " + student.getHistory() + "\n" +
                "Обществознание: " + student.getSocialStudies() + "\n" +
                "Информатика: " + student.getInformatics() + "\n" +
                "Физика: " + student.getPhysics() + "\n" +
                "Астрономия: " + student.getAstronomy() + "\n" +
                "Основы безопасности жизнедеятельности: " + student.getOBJ() + "\n" +
                "Основы проектной деятельности: " + student.getOPD() + "\n" +
                "Физическая культура: " + student.getNatLiterature() + "\n";
        if (textEmail != null && !textEmail.equals("")) {
            fullTextEmail += "Комментарий: " + textEmail;
        }
        message.setText(fullTextEmail);
        message.setSubject("Успеваемость");
        mailSender.send(message);
    }
}
