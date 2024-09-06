package dev.peacechan.controller;



import dev.peacechan.entity.Employee;
import dev.peacechan.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping
    public Employee saveEmployee(@RequestBody Employee employee) {
        return new ResponseEntity<>(employeeService.saveEmployee(employee),HttpStatus.OK).getBody();
    }
    @GetMapping
    public Iterable<Employee> getAllEmployees() {
        return  new ResponseEntity<>(employeeService.getAllEmployees(),HttpStatus.OK).getBody() ;
    }

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable("id") String employeeId) {
        return new ResponseEntity<>(employeeService.getEmployee(employeeId).orElse(null),HttpStatus.OK).getBody();
    }

    @DeleteMapping("/{id}")
    public String deleteEmployee(@PathVariable("id") String employeeId) {
        return new ResponseEntity<>(employeeService.deleteEmployee(employeeId),HttpStatus.OK).getBody();
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable("id") String employeeId, @RequestBody Employee employee) {
        return new ResponseEntity<>(employeeService.updateEmployee(employeeId,employee),HttpStatus.OK).getBody();
    }
}