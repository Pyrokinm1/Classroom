package classrom.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "Prepod")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_Prepod")
    private Long idTeacher;

    @NotBlank(message = "Email не должен быть пустым")
    @Size(min = 5, max = 40, message = "Email должно быть от 5 до 40 символов")
    @Column(name = "Email")
    private String email;

    //    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()â€\"[{}]:;'.,?/*~$^+=<>]).{15}$", message = "Пароль должен должно быть 15 символов и состоять из латинских букв и цифр")
    @Column(name = "Password")
    private String password;

    @Column(name = "active")
    private boolean active;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "PrepodRole", joinColumns = @JoinColumn(name = "PrepodId"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public Teacher() {}

    public Long getIdTeacher() {
        return idTeacher;
    }

    public void setIdTeacher(Long idTeacher) {
        this.idTeacher = idTeacher;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
