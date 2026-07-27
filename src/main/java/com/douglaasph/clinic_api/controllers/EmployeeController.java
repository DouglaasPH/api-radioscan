package com.douglaasph.clinic_api.controllers;

import com.douglaasph.clinic_api.controllers.dto.employee.EmployeesManagementMetricsDto;
import com.douglaasph.clinic_api.controllers.dto.employee.RegisterEmployeeDto;
import com.douglaasph.clinic_api.models.entities.Employee;
import com.douglaasph.clinic_api.models.entities.User;
import com.douglaasph.clinic_api.models.entities.enums.Position;
import com.douglaasph.clinic_api.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/employee")
@Tag(name = "Employee", description = "Endpoints for managing employees of the clinic")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    // AUTHORIZATION: ADMIN
    @Operation(summary = "Register employee", description = "Register employee and valid data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registered employee with success"),
            @ApiResponse(responseCode = "400", description = "Invalid data (validation failure)")
    })
    @PostMapping("/register")
    public ResponseEntity<Employee> register (@RequestBody @Valid RegisterEmployeeDto dto) {
        Employee employeeResponse = employeeService.register(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(employeeResponse.getId()).toUri();
        return ResponseEntity.created(uri).body(employeeResponse);
    }

    // AUTHORIZATION: ADMIN or PATIENT
    @Operation(summary = "Find all by name or position or both")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All doctors found")
    })
    @GetMapping
    public ResponseEntity<List<Employee>> findAllByNameOrPosition (@RequestParam(required = false) String name, @RequestParam(required = false) Position position) {
        return ResponseEntity.ok().body(employeeService.findAll(name, position));
    }

    // AUTHORIZATION: ANY ROLE (authenticated only)
    @Operation(summary = "Find employee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById (@PathVariable Long id) {
        return ResponseEntity.ok().body(employeeService.findById(id));
    }

    // AUTHORIZATION: ONLY ADMIN
    @Operation(
            summary = "Get employee management metrics for admin",
            description = "Retrieves key performance indicators, active headcount, turn-over statistics, and administrative summary data related to clinic employees."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee management metrics retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (missing or invalid JWT token)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (user lacks required ADMIN authority)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while processing employee metrics"
            )
    })
    @GetMapping("metrics/admin")
    public ResponseEntity<EmployeesManagementMetricsDto> employeesManagementMetrics() {
        return ResponseEntity.ok(this.employeeService.employeesManagementMetrics());
    }

    // AUTHORIZATION: ONLY ADMIN
    @Operation(
            summary = "Get employee management",
            description = "Retrieves key performance indicators, active headcount, turn-over statistics, and administrative summary data related to clinic employees."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee management metrics retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (missing or invalid JWT token)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (user lacks required ADMIN authority)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while processing employee metrics"
            )
    })
    @GetMapping("management/admin")
    public ResponseEntity<Page<User>> employeesManagementWithPagination(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Position position,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        return ResponseEntity.ok(this.employeeService.findAllEmployeesForManagementWithPagination(name, position, page));
    }
}
