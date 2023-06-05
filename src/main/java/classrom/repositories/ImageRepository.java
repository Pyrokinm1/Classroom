package classrom.repositories;

import classrom.models.Image;
import classrom.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Image findImageByStudent(Student student);
}