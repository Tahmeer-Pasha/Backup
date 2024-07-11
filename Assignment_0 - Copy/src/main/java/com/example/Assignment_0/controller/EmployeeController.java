package com.example.Assignment_0.controller;

import com.example.Assignment_0.dto.ApiResponse;
import com.example.Assignment_0.dto.EmployeeDto;
import com.example.Assignment_0.dto.EmployeeAllocationDto;
import com.example.Assignment_0.service.EmployeeServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.Assignment_0.utils.AuthorizationUtils.isAuthorized;

@Slf4j
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeAllocationDto>>> getAllEmployees(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            HttpServletRequest request) {
        try {
            if (!isAuthorized(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>("Unauthorized access", 401, null));
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            Page<EmployeeAllocationDto> employeePage = employeeService.getAllEmployeesWithAllocations(pageable);
            return ResponseEntity.ok(new ApiResponse<>("Retrieved all Employees successfully", 200, employeePage));
        } catch (Exception e) {
            log.error("Error retrieving employees: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployeeById(HttpServletRequest request, @PathVariable Long id) {
        try {
            if (!isAuthorized(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>("Unauthorized access", 401, null));
            }
            EmployeeDto employee = employeeService.getEmployeeById(id);
            if (employee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Employee with the given id: " + id + " not found", 404, null));
            }
            return ResponseEntity.ok(new ApiResponse<>("Retrieved Employee Successfully", 200, employee));
        } catch (Exception e) {
            log.error("Error retrieving employee by ID: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @GetMapping("/bench")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getBenchList(HttpServletRequest request) {
        try {
            if (!isAuthorized(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>("Unauthorized access", 401, null));
            }
            List<EmployeeDto> benchList = employeeService.getBenchList();
            return ResponseEntity.ok(new ApiResponse<>("Retrieved Bench List Successfully", 200, benchList));
        } catch (Exception e) {
            log.error("Error retrieving bench list: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<?>> importEmployees(HttpServletRequest request, @RequestBody MultipartFile file) {
        try {
            if (!isAuthorized(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>("Unauthorized access", 401, null));
            }
            log.debug("Multipart File Sending to the service");
            employeeService.importEmployees(file);
            log.debug("Method returned from the service");
            return ResponseEntity.ok(new ApiResponse<>("Employees imported successfully", 200, null));
        } catch (Exception e) {
            log.error("Error importing employees: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }
}
