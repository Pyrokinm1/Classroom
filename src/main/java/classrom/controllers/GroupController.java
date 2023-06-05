package classrom.controllers;

import classrom.models.Group;
import classrom.repositories.GroupRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@PreAuthorize("hasAnyAuthority('TEACHER')")
@RequestMapping("/group")
@Controller
public class GroupController {

    private final GroupRepository groupRepository;

    public GroupController(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @GetMapping("index")
    public String groupIndexGet(Model model) {
        model.addAttribute("groups", groupRepository.findAll());
        return "group/Index";
    }

    @PostMapping("index")
    public String groupIndexPost(Model model) {
        model.addAttribute("groups", groupRepository.findAll());
        return "group/Index";
    }

    @GetMapping("search")
    public String groupSearch(@RequestParam(required = false) String name, Model model) {
        Iterable<Group> groups;
        if (name != null && !name.equals(""))
            groups = groupRepository.findByGroupNameContains(name);
        else
            groups = groupRepository.findAll();
        model.addAttribute("groups", groups);
        return "group/Index";
    }
}
