import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmployeeReportPDF {

    public static void main(String[] args) {

        String csvFile = "employees.csv";
        String outputDir = "reports";

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {

                float margin = 50;
                float y = 750;

                // ===== TITLE =====
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 20);
                content.setNonStrokingColor(Color.BLACK);
                content.newLineAtOffset(margin, y);
                content.showText("Employee Report");
                content.endText();

                y -= 40;

                // ===== TABLE HEADER BACKGROUND =====
                content.setNonStrokingColor(Color.LIGHT_GRAY);
                content.addRect(margin, y - 5, 500, 25);
                content.fill();

                // ===== TABLE HEADER TEXT =====
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.setNonStrokingColor(Color.BLACK);
                content.newLineAtOffset(margin + 5, y + 5);
                content.showText("ID");
                content.newLineAtOffset(50, 0);
                content.showText("Name");
                content.newLineAtOffset(150, 0);
                content.showText("Department");
                content.newLineAtOffset(150, 0);
                content.showText("Salary");
                content.endText();

                y -= 30;

                // ===== READ CSV =====
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

                    String line;
                    br.readLine(); // skip CSV header
                    content.setFont(PDType1Font.HELVETICA, 11);

                    while ((line = br.readLine()) != null) {
                        String[] data = line.split(",");

                        if (data.length < 4) continue; // safety check

                        content.beginText();
                        content.setNonStrokingColor(Color.DARK_GRAY);
                        content.newLineAtOffset(margin + 5, y);
                        content.showText(data[0]);
                        content.newLineAtOffset(50, 0);
                        content.showText(data[1]);
                        content.newLineAtOffset(150, 0);
                        content.showText(data[2]);
                        content.newLineAtOffset(150, 0);
                        content.showText(data[3]);
                        content.endText();

                        y -= 20;

                        // Page overflow protection
                        if (y < 50) {
                            content.close();
                            page = new PDPage();
                            document.addPage(page);
                            y = 750;
                            break;
                        }
                    }
                }
            }

            // ===== OUTPUT DIRECTORY =====
            File dir = new File(outputDir);
            if (!dir.exists()) dir.mkdirs();

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String fileName = outputDir + "/Employee_Report_" + timestamp + ".pdf";
            document.save(fileName);

            System.out.println("PDF Generated Successfully!");
            System.out.println("Saved at: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}