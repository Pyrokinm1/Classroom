package classrom.services;

import classrom.models.Group;
import classrom.models.Role;
import classrom.models.Student;
import classrom.repositories.GroupRepository;
import classrom.repositories.StudentRepository;
import com.itextpdf.text.Font;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

public class DocumentService {

    public static void importStudentsFromFile(MultipartFile studentsFile, StudentRepository studentRepository, GroupRepository groupRepository) throws Exception {
        Workbook workbook = new XSSFWorkbook(studentsFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Group group = groupRepository.findGroupByGroupName(sheet.getSheetName());
        if (group != null) {
            int studentsCount = 1;
            for (Row row : sheet) {
                if (row.getRowNum() > 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    Cell cell = row.getCell(0);
                    String[] fio = cell.getStringCellValue().split(" ");
                    Student student = studentRepository.findByGroupAndSurnameAndNameAndPatronymic(group, fio[0], fio[1], fio[2]);
                    if (student != null) {
                        while (cellIterator.hasNext()) {
                            cell = cellIterator.next();
                            int columnIndex = cell.getColumnIndex();
                            switch (cell.getCellType()) {
                                case STRING -> updateStudentData(student, cell.getStringCellValue(), columnIndex);
                                case BLANK -> updateStudentData(student, null, columnIndex);
                                case NUMERIC -> {
                                    String cellValue = String.valueOf(cell.getNumericCellValue())
                                            .split("E")[0]
                                            .replace(".", "")
                                            .replaceFirst("8", "+7");
                                    updateStudentData(student, cellValue, columnIndex);
                                }
                            }
                        }
                    } else {
                        student = new Student();
                        while (cellIterator.hasNext()) {
                            cell = cellIterator.next();
                            if (cell.getColumnIndex() == 2) {
                                switch (cell.getCellType()) {
                                    case BLANK -> student.setPhoneNumber(null);
                                    case STRING -> student.setPhoneNumber(row.getCell(2).getStringCellValue());
                                    case NUMERIC -> {
                                        String cellValue = String.valueOf(row.getCell(2).getNumericCellValue())
                                                .split("E")[0]
                                                .replace(".", "")
                                                .replaceFirst("8", "+7");
                                        student.setPhoneNumber(cellValue);
                                    }
                                }
                            }
                            if (cell.getColumnIndex() == 3) {
                                switch (cell.getCellType()) {
                                    case BLANK -> student.setParentEmail(null);
                                    case STRING -> student.setParentEmail(row.getCell(3).getStringCellValue());
                                }
                            }
                            student.setGroup(group);
                            student.setSurname(fio[0]);
                            student.setName(fio[1]);
                            student.setPatronymic(fio[2]);
                            student.setActive(true);
                            student.setEmail(group.getGroupName().toLowerCase() + "-" + studentsCount + "@mpt.ru");
                            student.setPassword(student.getEmail().split("@")[0]);
                            student.setRoles(Collections.singleton(Role.STUDENT));

                        }
                    }
                    studentRepository.save(student);
                    studentsCount++;
                }
            }
        }
    }

    public static void importMarksFromFile(MultipartFile marksFile, StudentRepository studentRepository, GroupRepository groupRepository) throws IOException {
        Workbook workbook = new XSSFWorkbook(marksFile.getInputStream());
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            Group group = groupRepository.findGroupByGroupName(sheet.getSheetName());
            if (group != null) {
                for (Row row : sheet) {
                    if (row.getRowNum() > 4) {
                        Iterator<Cell> cellIterator = row.cellIterator();
                        Cell cell = row.getCell(2);
                        if (cell.getCellType() != CellType.BLANK) {
                            String[] fio = cell.getStringCellValue().split(" ");
                            Student student = studentRepository.findByGroupAndSurnameAndNameAndPatronymic(group, fio[0], fio[1], fio[2]);
                            if (student != null) {
                                while (cellIterator.hasNext()) {
                                    cell = cellIterator.next();
                                    int columnIndex = cell.getColumnIndex();
                                    if (columnIndex > 1 && columnIndex < 16) {
                                        switch (cell.getCellType()) {
                                            case STRING:
                                                if (Objects.equals(cell.getStringCellValue(), "н/a")) {
                                                    setStudentMark(student, 0, columnIndex);
                                                }
                                                break;
                                            case NUMERIC:
                                                int value = (int) cell.getNumericCellValue();
                                                setStudentMark(student, value, columnIndex);
                                                break;
                                        }
                                    }
                                }
                                studentRepository.save(student);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void importGroupsFromFile(MultipartFile groupsFile,  GroupRepository groupRepository) throws IOException {
        Workbook workbook = new XSSFWorkbook(groupsFile.getInputStream());
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            Group group = groupRepository.findGroupByGroupName(sheetName);
            if (group != null) {
                continue;
            }
            group = new Group(sheetName);
            groupRepository.save(group);
        }
    }

    public static Resource exportMarksToPDF(Student student, ResourceLoader resourceLoader) throws Exception {
        String fileName = "output\\Студент-" + student.getName() + student.getSurname() + student.getPatronymic() + ".pdf";
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();
        BaseFont baseFont = BaseFont.createFont("files\\font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont, 11, Font.NORMAL);
        addFirstPageText(student, document, font, writer);
        addSecondPageTable(student, document, font);
        document.close();
        return resourceLoader.getResource(Paths.get(fileName).toUri().toString());
    }

    private static void setStudentMark(Student student, int cellValue, int columnIndex) {
        switch (columnIndex) {
            case 3 -> student.setRusLang(cellValue);
            case 4 -> student.setLiterature(cellValue);
            case 5 -> student.setNatLiterature(cellValue);
            case 6 -> student.setEngLang(cellValue);
            case 7 -> student.setHistory(cellValue);
            case 8 -> student.setOBJ(cellValue);
            case 9 -> student.setPhysEducation(cellValue);
            case 10 -> student.setSocialStudies(cellValue);
            case 11 -> student.setMath(cellValue);
            case 12 -> student.setInformatics(cellValue);
            case 13 -> student.setPhysics(cellValue);
            case 14 -> student.setAstronomy(cellValue);
            case 15 -> student.setOPD(cellValue);
        }
    }

    private static void updateStudentData(Student student, String cellValue, int columnIndex) {
        switch (columnIndex) {
            case 1 -> student.setEmail(cellValue);
            case 2 -> student.setPhoneNumber(cellValue);
            case 3 -> student.setParentEmail(cellValue);
        }
    }

    private static void addFirstPageText(Student student, Document document, Font font, PdfWriter writer) throws DocumentException, IOException {
        Image image = Image.getInstance("files\\mptLogo.png");
        image.scaleToFit(75, 75);
        image.setAbsolutePosition(40, 735);
        document.add(image);
        Paragraph paragraph = new Paragraph();
        paragraph.setFont(font);
        String[] phrases = {"Директору Московского", "Приборостроительного", "Техникума",
                "А.В. Чурилову", "от студента группы", "__________________",
                "фамилия, имя, отчество (полностью)", "__________________", "контактный телефон",
                "Заявление", "Прошу выдать справку о том, что я " + student.getName() + " " + student.getSurname() + " " + student.getPatronymic() + " обучался(ась) в ФГБОУ ВО «РЭУ им Г.В. Плеханова»", "Обучалась (обучался) с _______ года по ____ год, на факультете ________ по специальности ________.",
                "Форма обучения _________ (очная или заочная)", "Дата " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()), "\nПодпись с расшифровкой"};
        for (int i = 0; i < phrases.length; i++) {
            paragraph = new Paragraph(phrases[i], font);
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            switch (i) {
                case 6, 8 -> font.setSize(8);
                case 7 -> font.setSize(11);
                case 9 -> {
                    font.setSize(11);
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                }
                case 10, 11, 12 -> paragraph.setAlignment(Element.ALIGN_LEFT);
                case 13, 14 -> {
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    PdfPTable footerTbl = new PdfPTable(1);
                    footerTbl.setTotalWidth(300);
                    PdfPCell cell = new PdfPCell(paragraph);
                    cell.setBorder(0);
                    footerTbl.addCell(cell);
                    footerTbl.writeSelectedRows(0, -1, 415, 80, writer.getDirectContent());
                    continue;
                }
            }
            document.add(paragraph);
        }
    }

    private static void addSecondPageTable(Student student, Document document, Font font) throws DocumentException {
        document.newPage();
        Paragraph paragraph = new Paragraph("Успеваемость студента", font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        paragraph = new Paragraph("Whitespace", font);
        font.setColor(BaseColor.WHITE);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        font.setColor(BaseColor.BLACK);
        PdfPTable table = new PdfPTable(13);
        font.setStyle(Font.BOLD);
        font.setSize(9);
        String[] items = {"Математика", "Русский язык", "Физическая культура", "Литература", "Родная литература", "Иностранный язык",
                "История", "Обществознание", "Информатика", "Физика", "Астрономия", "ОБЖ", "ОПД"};
        for (String item : items) {
            table.addCell(new Paragraph(item, font));
        }
        font.setStyle(Font.NORMAL);
        addMarksRows(table, font, student);
        document.add(table);
    }

    private static void addMarksRows(PdfPTable table, Font font, Student student) {
        table.addCell(new Paragraph(String.valueOf(student.getMath()), font));
        table.addCell(new Paragraph(String.valueOf(student.getRusLang()), font));
        table.addCell(new Paragraph(String.valueOf(student.getPhysEducation()), font));
        table.addCell(new Paragraph(String.valueOf(student.getLiterature()), font));
        table.addCell(new Paragraph(String.valueOf(student.getNatLiterature()), font));
        table.addCell(new Paragraph(String.valueOf(student.getEngLang()), font));
        table.addCell(new Paragraph(String.valueOf(student.getHistory()), font));
        table.addCell(new Paragraph(String.valueOf(student.getSocialStudies()), font));
        table.addCell(new Paragraph(String.valueOf(student.getInformatics()), font));
        table.addCell(new Paragraph(String.valueOf(student.getPhysics()), font));
        table.addCell(new Paragraph(String.valueOf(student.getAstronomy()), font));
        table.addCell(new Paragraph(String.valueOf(student.getOBJ()), font));
        table.addCell(new Paragraph(String.valueOf(student.getOPD()), font));
    }
}
