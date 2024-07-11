package com.example.Assignment_0.service.interfaces;

import com.example.Assignment_0.dto.EmployeeDto;
import com.example.Assignment_0.dto.EmployeeAllocationDto;
import com.example.Assignment_0.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {

    Employee registerEmployee(Employee employee);
    Employee findEmployeeByEmail(String email);

    List<Employee> findAllEmployees();

    Employee findEmployeeById(Long accountManagerId);

    Page<EmployeeAllocationDto> getAllEmployeesWithAllocations(Pageable pageable);

    List<EmployeeDto> getBenchList();

    void importEmployees(MultipartFile file) throws IOException;

    EmployeeDto getEmployeeById(Long id);
}
