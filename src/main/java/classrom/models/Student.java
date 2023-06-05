package classrom.models;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Entity
@Table(name = "Student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_Student")
    private Long idStudent;

    @NotBlank(message = "Email не должен быть пустым")
    @Column(name = "Email")
    private String email;

    @Column(name = "Password")
    private String password;

    @Column(name = "active")
    private boolean active;

    @Pattern(regexp = "[а-яА-Я]{0,30}", message = "Фамилия студента должна состоять только из букв")
    @NotBlank(message = "Фамилия студента не должна быть пустой")
    @Column(name = "FirstName")
    private String surname;

    @Pattern(regexp = "[а-яА-Я]{0,30}", message = "Имя студента должно состоять только из букв")
    @NotBlank(message = "Имя студента не должно быть пустым")
    @Column(name = "Name")
    private String name;

    @Pattern(regexp = "[а-яА-Я]{0,30}", message = "Отчество студента должно состоять только из букв")
    @Column(name = "LastName")
    private String patronymic;

    @Pattern(regexp = "[+]7[(]\\d{3}[)]\\d{3}-\\d{2}-\\d{2}", message = "Номер телефона студента должен состоять из 11 цифр и иметь данный формат: +7(999)999-99-99")
    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "ParentEmail")
    private String parentEmail;

    @Nullable
    @Max(5)
    @Column(name = "RusYaz")
    private int rusLang;

    @Nullable
    @Max(5)
    @Column(name = "Math")
    private int math;

    @Nullable
    @Max(5)
    @Column(name = "OBJ")
    private int OBJ;

    @Nullable
    @Max(5)
    @Column(name = "OPD")
    private int OPD;

    @Nullable
    @Max(5)
    @Column(name = "Fizra")
    private int physEducation;

    @Nullable
    @Max(5)
    @Column(name = "Astro")
    private int astronomy;

    @Nullable
    @Max(5)
    @Column(name = "Liter")
    private int literature;

    @Nullable
    @Max(5)
    @Column(name = "RodLiter")
    private int natLiterature;

    @Nullable
    @Max(5)
    @Column(name = "Angl")
    private int engLang;

    @Nullable
    @Max(5)
    @Column(name = "History")
    private int history;

    @Nullable
    @Max(5)
    @Column(name = "Obche")
    private int socialStudies;

    @Nullable
    @Max(5)
    @Column(name = "Informati")
    private int informatics;

    @Nullable
    @Max(5)
    @Column(name = "Fizika")
    private int physics;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "Image_Id")
    private Image image;
    @ManyToOne
    @JoinColumn(name = "Group_ID", referencedColumnName = "ID_Group")
    private Group group;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "StudentRole", joinColumns = @JoinColumn(name = "StudentId"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public Student() {
    }

    public Long getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(Long idStudent) {
        this.idStudent = idStudent;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public int getRusLang() {
        return rusLang;
    }

    public void setRusLang(int rusLang) {
        this.rusLang = rusLang;
    }

    public int getMath() {
        return math;
    }

    public void setMath(int math) {
        this.math = math;
    }

    public int getOBJ() {
        return OBJ;
    }

    public void setOBJ(int OBJ) {
        this.OBJ = OBJ;
    }

    public int getOPD() {
        return OPD;
    }

    public void setOPD(int OPD) {
        this.OPD = OPD;
    }

    public int getPhysEducation() {
        return physEducation;
    }

    public void setPhysEducation(int physEducation) {
        this.physEducation = physEducation;
    }

    public int getAstronomy() {
        return astronomy;
    }

    public void setAstronomy(int astronomy) {
        this.astronomy = astronomy;
    }

    public int getLiterature() {
        return literature;
    }

    public void setLiterature(int literature) {
        this.literature = literature;
    }

    public int getNatLiterature() {
        return natLiterature;
    }

    public void setNatLiterature(int natLiterature) {
        this.natLiterature = natLiterature;
    }

    public int getEngLang() {
        return engLang;
    }

    public void setEngLang(int engLang) {
        this.engLang = engLang;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    public int getSocialStudies() {
        return socialStudies;
    }

    public void setSocialStudies(int socialStudies) {
        this.socialStudies = socialStudies;
    }

    public int getInformatics() {
        return informatics;
    }

    public void setInformatics(int informatics) {
        this.informatics = informatics;
    }

    public int getPhysics() {
        return physics;
    }

    public void setPhysics(int physics) {
        this.physics = physics;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
