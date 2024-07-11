package com.example.Assignment_0.service;

import com.example.Assignment_0.dto.*;
import com.example.Assignment_0.entity.Employee;
import com.example.Assignment_0.entity.Project;
import com.example.Assignment_0.entity.ProjectAllocation;
import com.example.Assignment_0.repository.EmployeeRepository;
import com.example.Assignment_0.repository.ProjectAllocationRepository;
import com.example.Assignment_0.service.interfaces.EmployeeService;
import com.example.Assignment_0.utils.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.impl.FileUploadIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectAllocationRepository projectAllocationRepository;

    @Override
    public Employee registerEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Employee Already Exists!!!");
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return employeeRepository.save(employee);
    }

    @Override
    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee with provided email does not exist!!"));
    }

    @Override
    public List<Employee> findAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(this::sanitizeEmployeeDetails)
                .collect(Collectors.toList());
    }

    @Override
    public Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    @Override
    public Page<EmployeeAllocationDto> getAllEmployeesWithAllocations(Pageable pageable) {
        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        List<EmployeeAllocationDto> employeeAllocationDtos = employeePage.getContent().stream()
                .map(this::convertToEmployeeAllocationDto)
                .collect(Collectors.toList());

        return new PageImpl<>(employeeAllocationDtos, pageable, employeePage.getTotalElements());
    }

    @Override
    public List<EmployeeDto> getBenchList() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDto> responseList = new ArrayList<>();

        log.debug("Starting creation of the bench list");

        employees.forEach(employee -> {
            List<ProjectAllocation> projectAllocations = projectAllocationRepository.findByEmployeeId(employee.getId());
            double totalAllocationPercentage = projectAllocations.stream()
                    .mapToDouble(ProjectAllocation::getAllocationPercentage)
                    .sum();

            if (totalAllocationPercentage < 90) {
                log.debug("Adding employee {}", employee);
                responseList.add(createEmployeeDto(employee));
            }
        });

        log.debug("Completed creation of the bench list");

        return responseList;
    }

    @Override
    public void importEmployees(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Employee> employees = new ArrayList<>();

        if (file.isEmpty()) {
            throw new FileUploadIOException(new FileUploadException("File is empty"));
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build());
            for (CSVRecord csvRecord : csvParser) {
                Employee employee = new Employee();
                employee.setName(csvRecord.get("Name"));
                employee.setEmail(csvRecord.get("Email"));
                String techStackJson = csvRecord.get("TechStack");
                List<String> techStack = Arrays.asList(objectMapper.readValue(techStackJson, String[].class));
                employee.setTechStack(techStack);
                employee.setYearsOfExperience(Integer.parseInt(csvRecord.get("Total Years Of Experience")));
                employee.setYearsInWebknot(Double.parseDouble(csvRecord.get("Years in Webknot")));
                employee.setRole(Role.EMPLOYEE);
                employee.setPassword(passwordEncoder.encode(employee.getName().trim())); // Initial password set to employee's name
                log.debug("Parsed Employee: {}", employee);
                employees.add(employee);
            }
        } catch (IOException e) {
            log.error("Exception occurred while importing employees: {}", e.getMessage(), e);
            throw e;
        }

        employeeRepository.saveAll(employees);
        log.debug("All employees saved to database");
    }

    @Override
    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return createEmployeeDto(employee);
    }

    private EmployeeDto createEmployeeDto(Employee employee) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(employee.getId());
        employeeDto.setRole(employee.getRole());
        employeeDto.setEmployeeName(employee.getName());
        employeeDto.setEmployeeEmail(employee.getEmail());
        employeeDto.setTechStack(employee.getTechStack());
        employeeDto.setTotalYearsOfExperience(employee.getYearsInWebknot());
        employeeDto.setYearsInWebknot(employee.getYearsInWebknot());
        employeeDto.setManagedAccounts(createProjectDtoList(employee.getManagedAccounts()));
        employeeDto.setManagedProjects(createProjectDtoList(employee.getManagedProjects()));

        return employeeDto;
    }

    private List<ProjectDto> createProjectDtoList(List<Project> projects) {
        return projects.stream()
                .map(this::createProjectDto)
                .collect(Collectors.toList());
    }

    private EmployeeAllocationDto convertToEmployeeAllocationDto(Employee employee) {
        List<ProjectAllocationDto> projectAllocations = projectAllocationRepository.findByEmployeeId(employee.getId()).stream()
                .map(this::convertToProjectAllocationDto)
                .collect(Collectors.toList());

        EmployeeAllocationDto dto = new EmployeeAllocationDto();
        dto.setEmployeeId(employee.getId());
        dto.setEmployeeName(employee.getName());
        dto.setProjectAllocations(projectAllocations);

        return dto;
    }

    private Employee sanitizeEmployeeDetails(Employee employee) {
        employee.setPassword(""); // Clear password for security reasons
        employee.setManagedAccounts(null);
        employee.setManagedProjects(null);
        employee.setProjectAllocations(null);
        return employee;
    }

    public ProjectAllocationDto convertToProjectAllocationDto(ProjectAllocation allocation) {
        ProjectAllocationDto dto = new ProjectAllocationDto();
        dto.setId(allocation.getId());
        dto.setEmployeeId(allocation.getEmployee().getId());
        dto.setEmployeeName(allocation.getEmployee().getName());
        dto.setProjectId(allocation.getProject().getId());
        dto.setProjectRole(allocation.getProjectRole());
        dto.setAllocationPercentage(allocation.getAllocationPercentage());
        dto.setTechStack(allocation.getTechStack());
        dto.setDurationWeeks(allocation.getDurationWeeks());
        return dto;
    }

    public ProjectDto createProjectDto(Project project) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setProjectType(project.getProjectType());
        projectDto.setSourceClient(project.getSourceClient());
        projectDto.setEndClient(project.getEndClient());
        projectDto.setDescription(project.getDescription());
        projectDto.setAccountManagerId(project.getAccountManager().getId());
        projectDto.setAccountManagerName(project.getAccountManager().getName());
        projectDto.setProjectManagerId(project.getProjectManager().getId());
        projectDto.setProjectManagerName(project.getProjectManager().getName());
        projectDto.setStatus(project.getStatus());
        projectDto.setAllocations(project.getAllocations().stream()
                .map(this::convertToProjectAllocationDto)
                .collect(Collectors.toList()));

        return projectDto;
    }

    public Boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
