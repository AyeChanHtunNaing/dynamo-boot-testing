package dev.peacechan.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import dev.peacechan.entity.Employee;
import dev.peacechan.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ReportService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Value("${csv.export.dir}")
    private String csvExportDir;

    private List<Long> exportTimes = new ArrayList<>();
    private List<Long> exportMemoryUsages = new ArrayList<>();
    private List<Long> importTimes = new ArrayList<>();
    private List<Long> importMemoryUsages = new ArrayList<>();

    public byte[] exportToCsv(List<Employee> employees) {
        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filePath = Paths.get(csvExportDir, "employees_" + timestamp + ".csv").toString();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            // Write CSV header
            String[] header = {"employeeId", "firstName", "lastName", "email"};
            writer.writeNext(header);

            // Write data
            for (Employee employee : employees) {
                String[] data = {
                        employee.getEmployeeId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail()
                };
                writer.writeNext(data);
            }

            // Ensure file is properly written
            writer.flush();

            // Read file into byte array and return
            return Files.readAllBytes(Paths.get(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        } finally {
            long endTime = System.currentTimeMillis();
            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            long timeTaken = endTime - startTime;
            long memoryUsed = endMemory - startMemory;

            exportTimes.add(timeTaken);
            exportMemoryUsages.add(memoryUsed);

            // Log time and memory usage
            System.out.printf("CSV Export Time: %d ms%n", timeTaken);
            System.out.printf("Memory Used for Export: %d bytes%n", memoryUsed);
        }
    }

    public String importFromCsv(MultipartFile file) {
        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        try (InputStream inputStream = file.getInputStream();
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {

            // Skip header
            reader.skip(1);

            // Read data
            String[] line;
            List<Employee> employees = new ArrayList<>();

            while ((line = reader.readNext()) != null) {
                // Ensure the line has exactly 4 fields
                if (line.length < 4) {
                    System.err.println("Invalid row, skipping: " + String.join(",", line));
                    continue;  // Skip rows with insufficient data
                }

                Employee employee = new Employee();
                employee.setEmployeeId(line[0]);
                employee.setFirstName(line[1]);
                employee.setLastName(line[2]);
                employee.setEmail(line[3]);

                employees.add(employee);
            }

            employeeRepository.saveAll(employees);  // Save all employees after validation
            return "CSV file imported successfully";
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return "Error importing CSV file: " + e.getMessage();
        } finally {
            long endTime = System.currentTimeMillis();
            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            long timeTaken = endTime - startTime;
            long memoryUsed = endMemory - startMemory;

            importTimes.add(timeTaken);
            importMemoryUsages.add(memoryUsed);

            // Log time and memory usage
            System.out.printf("CSV Import Time: %d ms%n", timeTaken);
            System.out.printf("Memory Used for Import: %d bytes%n", memoryUsed);
        }
    }

    public void printReport() {
        if (exportTimes.isEmpty() && importTimes.isEmpty()) {
            System.out.println("No data to report.");
            return;
        }

        // Calculate average export time and memory usage
        if (!exportTimes.isEmpty()) {
            long totalExportTime = exportTimes.stream().mapToLong(Long::longValue).sum();
            long totalExportMemoryUsage = exportMemoryUsages.stream().mapToLong(Long::longValue).sum();

            double averageExportTime = (double) totalExportTime / exportTimes.size();
            double averageExportMemoryUsage = (double) totalExportMemoryUsage / exportMemoryUsages.size();

            System.out.printf("Average CSV Export Time: %.2f ms%n", averageExportTime);
            System.out.printf("Average Memory Usage for Export: %.2f bytes%n", averageExportMemoryUsage);
        }

        // Calculate average import time and memory usage
        if (!importTimes.isEmpty()) {
            long totalImportTime = importTimes.stream().mapToLong(Long::longValue).sum();
            long totalImportMemoryUsage = importMemoryUsages.stream().mapToLong(Long::longValue).sum();

            double averageImportTime = (double) totalImportTime / importTimes.size();
            double averageImportMemoryUsage = (double) totalImportMemoryUsage / importMemoryUsages.size();

            System.out.printf("Average CSV Import Time: %.2f ms%n", averageImportTime);
            System.out.printf("Average Memory Usage for Import: %.2f bytes%n", averageImportMemoryUsage);
        }
    }
}
