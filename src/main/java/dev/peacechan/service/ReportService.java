package dev.peacechan.service;

import com.opencsv.CSVWriter;
import dev.peacechan.entity.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ReportService {

    @Value("${csv.export.dir}")
    private String csvExportDir;

    private List<Long> times = new ArrayList<>();
    private List<Long> memoryUsages = new ArrayList<>();

//    public ReportService() {
//        // Ensure directory exists
//        File dir = new File(csvExportDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//    }

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
            System.err.println("Error writing or reading CSV file: " + e.getMessage());
            return new byte[0];
        } finally {
            long endTime = System.currentTimeMillis();
            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            long timeTaken = endTime - startTime;
            long memoryUsed = endMemory - startMemory;

            times.add(timeTaken);
            memoryUsages.add(memoryUsed);

            // Log or store time and memory usage
            System.out.printf("CSV Export Time: %d ms%n", timeTaken);
            System.out.printf("Memory Used: %d bytes%n", memoryUsed);
        }
    }

    public void printReport() {
        if (times.isEmpty() || memoryUsages.isEmpty()) {
            System.out.println("No data to report.");
            return;
        }

        long totalTimes = times.stream().mapToLong(Long::longValue).sum();
        long totalMemoryUsages = memoryUsages.stream().mapToLong(Long::longValue).sum();

        double averageTime = (double) totalTimes / times.size();
        double averageMemoryUsage = (double) totalMemoryUsages / memoryUsages.size();

        System.out.printf("Average CSV Export Time: %.2f ms%n", averageTime);
        System.out.printf("Average Memory Usage: %.2f bytes%n", averageMemoryUsage);
    }
}
