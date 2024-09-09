package dev.peacechan.controller;

import dev.peacechan.entity.Employee;
import dev.peacechan.service.EmployeeService;
import dev.peacechan.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ReportService reportService;

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportEmployeesToCsv() {
        try {
            // Fetch employees
            List<Employee> employees = (List<Employee>) employeeService.getAllEmployees();
            if (employees == null || employees.isEmpty()) {
                // Return an empty CSV file with a 204 No Content status
                return ResponseEntity.noContent().build();
            }

            // Export to CSV
            byte[] csvData = reportService.exportToCsv(employees);
            if (csvData.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to generate CSV file".getBytes());
            }

            // Set headers and return response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "employees.csv");
            headers.setContentLength(csvData.length);

            return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while exporting CSV".getBytes());
        }
    }

    @PostMapping("/import/csv")
    public ResponseEntity<String> importEmployeesFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided.");
        }

        try {
            String message = reportService.importFromCsv(file);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while importing the CSV file");
        }
    }

    @GetMapping("/report")
    public ResponseEntity<String> generateReport() {
        try {
            // Generate report
            reportService.printReport();
            return ResponseEntity.ok("Report generated. Check logs for details.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while generating the report");
        }
    }
}
