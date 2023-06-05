package classrom.controllers;

import classrom.models.Group;
import classrom.models.Student;
import classrom.repositories.StudentRepository;
import classrom.services.StudentImageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("hasAnyAuthority('TEACHER')")
@RequestMapping("/student")
@Controller
public class StudentController {

    private final StudentRepository studentRepository;

    private final StudentImageService studentImageService;

    public StudentController(StudentRepository studentRepository, StudentImageService studentImageService) {
        this.studentRepository = studentRepository;
        this.studentImageService = studentImageService;
    }

    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @GetMapping("card")
    public String studentCardGet(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = studentRepository.findStudentByEmail(authentication.getName());
        model.addAttribute("student", student);
        model.addAttribute("marks", getMarksList(student));
        model.addAttribute("markTypes", getMarkTypesList());
        return "student/StudentCard";
    }

    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @PostMapping("card")
    public String studentCardPost(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = studentRepository.findStudentByEmail(authentication.getName());
        model.addAttribute("student", student);
        model.addAttribute("marks", getMarksList(student));
        model.addAttribute("markTypes", getMarkTypesList());
        return "student/StudentCard";
    }

    @GetMapping("index")
    public String studentIndex(@RequestParam Group group, Model model) {
        model.addAttribute("students", studentRepository.findAllByGroup(group));
        model.addAttribute("group", group);
        model.addAttribute("check", true);
        return "student/Index";
    }

    @GetMapping("search")
    public String studentSearch(@RequestParam Group group, @RequestParam(required = false) String surname, Model model) {
        Iterable<Student> students;
        if (surname != null && !surname.equals(""))
            students = studentRepository.findBySurnameContainsAndGroup(surname, group);
        else
            students = studentRepository.findAllByGroup(group);
        model.addAttribute("students", students);
        model.addAttribute("group", group);
        model.addAttribute("check", true);
        return "student/Index";
    }

    @GetMapping("card/edit")
    public String studentCardEdit(@RequestParam Student student, Model model) {
        model.addAttribute("student", student);
        return "student/StudentCardEdit";
    }

    @GetMapping("debtors")
    public String studentDebtors(@RequestParam Group group, Model model) {
        Iterable<Student> students = studentRepository.findAllByGroup(group);
        List<Student> studentListDebtors = new ArrayList<>();
        for (Student student : students) {
            if (student.getRusLang() <= 2 || student.getMath() <= 2 || student.getOBJ() <= 2 ||
                    student.getOPD() <= 2 || student.getPhysEducation() <= 2 || student.getAstronomy() <= 2 ||
                    student.getLiterature() <= 2 || student.getNatLiterature() <= 2 || student.getEngLang() <= 2 ||
                    student.getHistory() <= 2 || student.getSocialStudies() <= 2 || student.getInformatics() <= 2 || student.getPhysics() <= 2) {
                studentListDebtors.add(student);
            }
        }

        students = studentListDebtors;
        model.addAttribute("students", students);
        model.addAttribute("group", group);
        model.addAttribute("check", false);
        return "student/Debtors";
    }

    @GetMapping("debtors/search")
    public String studentDebtorsSearch(@RequestParam Group group, @RequestParam(required = false) int debtsCount, Model model) {
        Iterable<Student> students = studentRepository.findAllByGroup(group);
        List<Student> studentListDebtors = new ArrayList<>();
        List<Student> studentListCleared = new ArrayList<>();
        List<Integer> marksCountList = new ArrayList<>();
        for (Student student : students) {
            if (student.getRusLang() <= 2 || student.getMath() <= 2 || student.getOBJ() <= 2 ||
                    student.getOPD() <= 2 || student.getPhysEducation() <= 2 || student.getAstronomy() <= 2 ||
                    student.getLiterature() <= 2 || student.getNatLiterature() <= 2 || student.getEngLang() <= 2 ||
                    student.getHistory() <= 2 || student.getSocialStudies() <= 2 || student.getInformatics() <= 2 || student.getPhysics() <= 2) {
                studentListDebtors.add(student);
            }
        }
        for (Student student : studentListDebtors) {
            List<Integer> marksList = new ArrayList<>();
            marksList.add(student.getRusLang());
            marksList.add(student.getMath());
            marksList.add(student.getOBJ());
            marksList.add(student.getOPD());
            marksList.add(student.getPhysEducation());
            marksList.add(student.getAstronomy());
            marksList.add(student.getPhysics());
            marksList.add(student.getInformatics());
            marksList.add(student.getSocialStudies());
            marksList.add(student.getHistory());
            marksList.add(student.getLiterature());
            marksList.add(student.getNatLiterature());
            marksList.add(student.getEngLang());
            int marksCount = 0;
            for (int mark : marksList) {
                if (mark <= 2) {
                    marksCount++;
                }
            }
            marksCountList.add(marksCount);
        }
        for (int i = 0; i < marksCountList.size(); i++) {
            if (marksCountList.get(i) == debtsCount) {
                studentListCleared.add(studentListDebtors.get(i));
            }
        }
        students = studentListCleared;
        model.addAttribute("students", students);
        model.addAttribute("group", group);
        model.addAttribute("check", false);
        return "student/Debtors";
    }

    @PostMapping("updateMarks")
    public String studentUpdateMarks(@ModelAttribute("student") @Valid Student student) {
        studentRepository.save(student);
        return "student/StudentCardEdit";
    }

    @PostMapping("update")
    public String studentUpdate(@ModelAttribute("student") @Valid Student student,  BindingResult bindingResult,
                                @RequestParam(required = false) MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            studentImageService.saveStudentAndImage(student, imageFile);
            return "student/StudentCardEdit";
        }
        if (bindingResult.hasErrors()) {
            return "student/StudentCardEdit";
        }
        studentRepository.save(student);
        return "student/StudentCardEdit";
    }

    private static List<String> getMarksList(Student student) {
        List<String> marksList = new ArrayList<>();
        List<String> marksCountList = new ArrayList<>();
        int countFive = 0;
        int countFour = 0;
        int countThree = 0;
        int countTwo = 0;
        int countNA = 0;
        marksList.add(String.valueOf(student.getRusLang()));
        marksList.add(String.valueOf(student.getMath()));
        marksList.add(String.valueOf(student.getOBJ()));
        marksList.add(String.valueOf(student.getOPD()));
        marksList.add(String.valueOf(student.getPhysEducation()));
        marksList.add(String.valueOf(student.getAstronomy()));
        marksList.add(String.valueOf(student.getPhysics()));
        marksList.add(String.valueOf(student.getInformatics()));
        marksList.add(String.valueOf(student.getSocialStudies()));
        marksList.add(String.valueOf(student.getHistory()));
        marksList.add(String.valueOf(student.getLiterature()));
        marksList.add(String.valueOf(student.getNatLiterature()));
        marksList.add(String.valueOf(student.getEngLang()));
        for (String mark : marksList) {
            switch (mark) {
                case "5" -> countFive++;
                case "4" -> countFour++;
                case "3" -> countThree++;
                case "2" -> countTwo++;
                case "0" -> countNA++;
            }
        }
        marksCountList.add(String.valueOf(countNA));
        marksCountList.add(String.valueOf(countTwo));
        marksCountList.add(String.valueOf(countThree));
        marksCountList.add(String.valueOf(countFour));
        marksCountList.add(String.valueOf(countFive));
        return marksCountList;
    }

    private static List<String> getMarkTypesList() {
        List<String> markTypesList = new ArrayList<>();
        markTypesList.add("н/а");
        markTypesList.add("2");
        markTypesList.add("3");
        markTypesList.add("4");
        markTypesList.add("5");
        return markTypesList;
    }
}
