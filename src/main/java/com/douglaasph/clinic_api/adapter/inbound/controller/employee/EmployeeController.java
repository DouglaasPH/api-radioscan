package com.douglaasph.clinic_api.adapter.inbound.controller.employee;

import com.douglaasph.clinic_api.adapter.inbound.controller.employee.request.CreateEmployeeRequest;
import com.douglaasph.clinic_api.adapter.inbound.controller.employee.response.MetricsEmployeesForAdminResponse;
import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.results.MetricsEmployeesForAdminResult;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.ports.inbound.CreateEmployeeAccountUseCasePort;
import com.douglaasph.clinic_api.domain.ports.inbound.GetEmployeesForManagementWithPaginationUseCasePort;
import com.douglaasph.clinic_api.domain.ports.inbound.GetMetricsEmployeesForAdminUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/employee")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Endpoints for managing clinic employees (doctors and technicians)")
public class EmployeeController {
    private final CreateEmployeeAccountUseCasePort createEmployeeAccountUseCasePort;
    private final GetMetricsEmployeesForAdminUseCasePort getMetricsEmployeesForAdminUseCasePort;
    private final GetEmployeesForManagementWithPaginationUseCasePort getEmployeesForManagementWithPaginationUseCasePort;

    // AUTHORIZATION: ADMIN
    @PostMapping("/register")
    @Operation(
            summary = "Register a new employee",
            description = "Creates a new employee account (doctor or technical). Restricted to admins."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error in the request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not an admin", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email or license number already exists", content = @Content)
    })
    public ResponseEntity<Employee> register (@RequestBody @Valid CreateEmployeeRequest request) {
        Employee employeeResponse = createEmployeeAccountUseCasePort.execute(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getLicenseNumber(),
                request.getPosition()
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(employeeResponse.getId()).toUri();
        return ResponseEntity.created(uri).body(employeeResponse);
    }

    // AUTHORIZATION: ADMIN
    @GetMapping("metrics/admin")
    @Operation(
            summary = "Get employee metrics for admin dashboard",
            description = "Returns aggregated metrics about employees (total count, count by position). Restricted to admins."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not an admin", content = @Content)
    })
    public ResponseEntity<MetricsEmployeesForAdminResponse> employeesManagementMetrics() {
        MetricsEmployeesForAdminResult result = this.getMetricsEmployeesForAdminUseCasePort.execute();
        return ResponseEntity.ok(MetricsEmployeesForAdminResponse.fromDomain(result));
    }

    // AUTHORIZATION: ADMIN
    @GetMapping("management/admin")
    @Operation(
            summary = "List employees with pagination and filters",
            description = "Returns a paginated list of employees, optionally filtered by name and/or position. Restricted to admins."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of employees retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not an admin", content = @Content)
    })
    public ResponseEntity<Page<User>> employeesManagementWithPagination(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Position position,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        return ResponseEntity.ok(this.getEmployeesForManagementWithPaginationUseCasePort.execute(name, position, page));
    }
}
