package dev.peacechan.controller;

import dev.peacechan.entity.Employee;
import dev.peacechan.service.ReportService;
import dev.peacechan.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<Employee> employees = (List<Employee>) employeeService.getAllEmployees();
        byte[] csvData = reportService.exportToCsv(employees);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "employees.csv");
        headers.setContentLength(csvData.length);

        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    @GetMapping("/report")
    public ResponseEntity<String> generateReport() {
        reportService.printReport();
        return ResponseEntity.ok("Report generated. Check logs for details.");
    }
}
