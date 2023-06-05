package classrom.repositories;

import classrom.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Teacher findTeacherByEmail(String email);
}

