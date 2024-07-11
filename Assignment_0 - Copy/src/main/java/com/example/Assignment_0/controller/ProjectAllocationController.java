package com.example.Assignment_0.controller;

import com.example.Assignment_0.dto.ApiResponse;
import com.example.Assignment_0.dto.ProjectAllocationDto;
import com.example.Assignment_0.dto.ProjectAllocationRequest;
import com.example.Assignment_0.entity.ProjectAllocation;
import com.example.Assignment_0.service.ProjectAllocationServiceImpl;
import com.example.Assignment_0.utils.enums.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.Assignment_0.utils.AuthorizationUtils.isAuthorized;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("project-allocation")
public class ProjectAllocationController {
    private final ProjectAllocationServiceImpl projectAllocationService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> assignOrRemoveProject(HttpServletRequest request, @RequestParam Operation operation, @Valid @RequestBody ProjectAllocationRequest projectAllocationRequest) {
        try {
            if (!isAuthorized(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Only ADMIN can assign or remove employee from project.", 401, null));
            }

            if (operation.equals(Operation.ADD)) {
                return ResponseEntity.ok(new ApiResponse<>("Successfully assigned employee to project!", 200, projectAllocationService.assignProject(projectAllocationRequest)));
            } else if (operation.equals(Operation.REMOVE)) {
                projectAllocationService.removeFromProject(projectAllocationRequest);
                return ResponseEntity.ok(new ApiResponse<>("Successfully removed employee from project!", 200, null));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("Invalid Request", 400, null));
        } catch (Exception e) {
            log.error("Error in assignOrRemoveProject: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllProjectAllocations(HttpServletRequest request) {
        try {
            if (!isAuthorized(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project allocation list can be retrieved only by ADMIN.", 401, null));
            }
            List<ProjectAllocation> projectAllocations = projectAllocationService.getAllProjectAllocations();
            return ResponseEntity.ok(new ApiResponse<>("Retrieved all Assigned Project Successfully", 200, projectAllocations));
        } catch (Exception e) {
            log.error("Error in getAllProjectAllocations: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<?>> getProjectDetailsById(HttpServletRequest request, @PathVariable Long projectId) {
        try {
            List<ProjectAllocationDto> projectAllocation = projectAllocationService.getProjectAllocationDtoListByProjectId(projectId);
            return ResponseEntity.ok(new ApiResponse<>("Retrieved Assigned Project with given id Successfully", 200, projectAllocation));
        } catch (Exception e) {
            log.error("Error in getProjectDetailsById: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

}
