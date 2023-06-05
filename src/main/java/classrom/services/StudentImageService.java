package classrom.services;

import classrom.models.Image;
import classrom.models.Student;
import classrom.repositories.ImageRepository;
import classrom.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentImageService {

    private final StudentRepository studentRepository;

    private final ImageRepository imageRepository;

    public void saveStudentAndImage(Student student, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            Image image = saveImageEntity(file);
            imageRepository.save(image);
            student.setImage(image);
        }
        studentRepository.save(student);
    }

    private Image saveImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }
}
