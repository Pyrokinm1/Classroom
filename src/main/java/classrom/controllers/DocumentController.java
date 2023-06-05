package classrom.controllers;

import classrom.models.Group;
import classrom.models.Student;
import classrom.repositories.GroupRepository;
import classrom.repositories.StudentRepository;
import classrom.services.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@PreAuthorize("hasAnyAuthority('TEACHER')")
@Controller
public class DocumentController {

    private final ResourceLoader resourceLoader;

    private final GroupRepository groupRepository;

    private final StudentRepository studentRepository;

    public DocumentController(ResourceLoader resourceLoader, GroupRepository groupRepository, StudentRepository studentRepository) {
        this.resourceLoader = resourceLoader;
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/importMarks")
    public String importMarks(@RequestParam("marksFile") MultipartFile marksFile, Model model) {
        Iterable<Group> groups = groupRepository.findAll();
        String errorText;
        model.addAttribute("groups", groups);
        if (marksFile.isEmpty()) {
            errorText = "Выберите файл";
            model.addAttribute("errorText", errorText);
            return "group/Index";
        }
        try {
            String successText = "Импорт оценок выполнен успешно";
            DocumentService.importMarksFromFile(marksFile, studentRepository, groupRepository);
            model.addAttribute("successText", successText);
        } catch (Exception ex) {
            errorText = "Некорректный файл";
            model.addAttribute("errorText", errorText);
            return "group/Index";
        }
        return "group/Index";
    }

    @PostMapping("/importStudents")
    public String importStudents(@RequestParam("studentsFile") MultipartFile studentsFile, Model model) {
        Iterable<Group> groups = groupRepository.findAll();
        String errorText;
        model.addAttribute("groups", groups);
        if (studentsFile.isEmpty()) {
            errorText = "Выберите файл";
            model.addAttribute("errorText", errorText);
            return "group/Index";
        }
        try {
            String successText = "Импорт студентов выполнен успешно";
            DocumentService.importStudentsFromFile(studentsFile, studentRepository, groupRepository);
            model.addAttribute("successText", successText);
        } catch (Exception ex) {
            errorText = "Некорректный файл";
            model.addAttribute("errorText", errorText);
            return "group/Index";
        }
        return "group/Index";
    }

    @PostMapping("/importGroups")
    public String importGroups(@RequestParam("groupsFile") MultipartFile groupsFile, Model model) {
        String errorText;
        if (groupsFile.isEmpty()) {
            Iterable<Group> groups = groupRepository.findAll();
            model.addAttribute("groups", groups);
            errorText = "Выберите файл";
            model.addAttribute("errorText", errorText);
            return "group/Index";
        }
        try {
            DocumentService.importGroupsFromFile(groupsFile, groupRepository);
            String successText = "Импорт групп выполнен успешно";
            model.addAttribute("successText", successText);
            Iterable<Group> groups = groupRepository.findAll();
            model.addAttribute("groups", groups);
        } catch (Exception ex) {
            errorText = "Некорректный файл";
            model.addAttribute("errorText", errorText);
            Iterable<Group> groups = groupRepository.findAll();
            model.addAttribute("groups", groups);
            return "group/Index";
        }
        return "group/Index";
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportMarks(@RequestParam Student student, Model model) throws Exception {
        Resource resource = DocumentService.exportMarksToPDF(student, resourceLoader);
        model.addAttribute("student", student);
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(resource.getFilename(), StandardCharsets.UTF_8)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDisposition(contentDisposition);
        return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
    }
}