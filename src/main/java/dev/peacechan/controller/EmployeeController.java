package dev.peacechan.controller;

import dev.peacechan.entity.Employee;
import dev.peacechan.repository.EmployeeRepository;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {
    private EmployeeRepository employeeRepository;

    @PostMapping("employee")
    public Employee saveEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    @GetMapping("/employee/{id}")
    public Employee getEmployee(@PathVariable("id") String employeeId){
        return employeeRepository.getEmployeeById(employeeId);
    }
    @DeleteMapping("/employee/{id}")
    public String deleteEmployee(@PathVariable("id") String employeeId){
        return employeeRepository.delete(employeeId);
    }
    @PutMapping("/employee/{id}")
    public String upodateEmployeee(@PathVariable("id") String employeeId,@RequestBody Employee employee){
        return employeeRepository.update(employeeId,employee);
    }
}
