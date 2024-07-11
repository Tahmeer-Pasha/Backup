package com.example.Assignment_0.controller;


import com.example.Assignment_0.dto.ApiResponse;
import com.example.Assignment_0.dto.ProjectAllocationDto;
import com.example.Assignment_0.dto.ProjectAllocationRequest;
import com.example.Assignment_0.entity.ProjectAllocation;
import com.example.Assignment_0.service.ProjectAllocationServiceImpl;
import com.example.Assignment_0.utils.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("project-allocation")
public class ProjectAllocationController {
    private final ProjectAllocationServiceImpl projectAllocationService;

    @PostMapping
    private ResponseEntity<ApiResponse<?>> assignOrRemoveProject(HttpServletRequest request, @RequestParam Operation operation, @RequestBody ProjectAllocationRequest projectAllocationRequest){
        try{
            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
            if (userDetails == null || userDetails.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project can be created only by ADMIN.", 401, null));
            }

            if(operation.equals(Operation.ADD)){
                return ResponseEntity.ok(new ApiResponse<>("Successfully assigned employee to project!",200,projectAllocationService.assignProject(projectAllocationRequest)));
            } else if (operation.equals(Operation.REMOVE)) {
                projectAllocationService.removeFromProject(projectAllocationRequest);
                return ResponseEntity.ok(new ApiResponse<>("Successfully removed employee from project!",200,null));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("Invalid Request",400,null));

        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new ApiResponse<>(e.getMessage(),500,null));
        }
    }

    @GetMapping
    private ResponseEntity<ApiResponse<?>> getAllProjectAllocations(HttpServletRequest request){
        try{
            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
            if (userDetails == null || userDetails.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project can be created only by ADMIN.", 401, null));
            }
          List<ProjectAllocation> projectAllocations = projectAllocationService.getAllProjectAllocations();
            return ResponseEntity.ok(new ApiResponse<>("Retrieved all Assigned Project Successfully",200, projectAllocations));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new ApiResponse<>(e.getMessage(),500,null));
        }
    }

    @GetMapping("/{projectId}")
    private ResponseEntity<ApiResponse<?>> getProjectDetailsById(HttpServletRequest request, @PathVariable Long projectId){
        try{
            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
            if (userDetails == null || userDetails.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project can be created only by ADMIN.", 401, null));
            }
            List<ProjectAllocationDto> projectAllocation = projectAllocationService.getProjectAllocationListByProjectId(projectId);
            return ResponseEntity.ok(new ApiResponse<>("Retrieved Assigned Project with given id Successfully",200, projectAllocation));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new ApiResponse<>(e.getMessage(),500,null));
        }
    }
}
