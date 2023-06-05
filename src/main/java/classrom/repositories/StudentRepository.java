package classrom.repositories;

import classrom.models.Group;
import classrom.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findStudentByEmail(String email);

    Iterable<Student> findAllByGroup(Group group);

    Student findByGroupAndSurnameAndNameAndPatronymic(Group group, String surname, String name, String patronymic);

    Iterable<Student> findBySurnameContainsAndGroup(String surname, Group group);
}
