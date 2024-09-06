package dev.peacechan.service;

import dev.peacechan.entity.Employee;
import dev.peacechan.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<Employee> getEmployee(String id) {
        return employeeRepository.findById(id);
    }

    public Iterable<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public String deleteEmployee(String id) {
        if(!employeeRepository.existsById(id)){
            throw new RuntimeException("Employee not found with id " + id);
        }
        employeeRepository.deleteById(id);
        return "Successfully deleted employee with id " + id;
    }

    public Employee updateEmployee(String id, Employee employee) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id " + id);
        }
        return employeeRepository.save(employee);
    }
}
