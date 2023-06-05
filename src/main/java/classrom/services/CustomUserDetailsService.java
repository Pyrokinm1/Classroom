package classrom.services;

import classrom.models.Student;
import classrom.models.Teacher;
import classrom.repositories.StudentRepository;
import classrom.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepository.findStudentByEmail(email);
        if (student != null) {
            return new CustomUserDetails(student.getEmail(), student.getPassword(), student.getRoles().toArray()[0].toString());
        } else {
            Teacher teacher = teacherRepository.findTeacherByEmail(email);
            if (teacher != null) {
                return new CustomUserDetails(teacher.getEmail(), teacher.getPassword(), teacher.getRoles().toArray()[0].toString());
            }
        }
        throw new UsernameNotFoundException("User not found");
    }

    public class CustomUserDetails implements UserDetails {

        private String email;
        private String password;
        private Collection<? extends GrantedAuthority> authorities;

        public CustomUserDetails() {
            super();
        }

        public CustomUserDetails(String email, String password, String role) {
            this.email = email;
            this.password = password;
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
            this.authorities = grantedAuthorities;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }
}