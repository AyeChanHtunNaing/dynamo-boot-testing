package dev.peacechan.repository;


import dev.peacechan.entity.Employee;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@EnableScan
public interface EmployeeRepository extends CrudRepository<Employee,String> {

}
