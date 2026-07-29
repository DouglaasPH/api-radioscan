package com.douglaasph.clinic_api.controllers;

import com.douglaasph.clinic_api.controllers.dto.appointment.*;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;
import com.douglaasph.clinic_api.models.entities.Appointment;
import com.douglaasph.clinic_api.models.entities.enums.AppointmentStatus;
import com.douglaasph.clinic_api.models.entities.enums.AppointmentType;
import com.douglaasph.clinic_api.services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/appointment")
@Tag(name = "Appointment", description = "Endpoints for managing appointment of the clinic")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    // AUTHORIZATION: ADMIN
    @Operation(summary = "Insert appointment", description = "Valid data and add appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inserted appointment with success"),
            @ApiResponse(responseCode = "400", description = "Invalid data (validation failure)")
    })
    @PostMapping
    public ResponseEntity<Appointment> insert(@RequestBody @Valid CreateAppointmentDto dto) throws BadRequestException {
            Appointment appointmentResponse = appointmentService.insert(dto);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(appointmentResponse.getId()).toUri();
            return ResponseEntity.created(uri).body(appointmentResponse);
    }

    // AUTHORIZATION: PATIENT AND ADMIN
    @Operation(summary = "Find all available appointments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All inserted appointments")
    })
    @GetMapping("/available")
    public ResponseEntity<Page<AllAvailablesAppointmentsResponseDto>> findAllAvailable(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDate date,
            @RequestParam(required = false) AppointmentType appointmentType,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        return ResponseEntity.ok().body(appointmentService.findAllAvailable(date, appointmentType, page));
    }

    // AUTHORIZATION: ANY ROLE (authenticated only)
    @Operation(summary = "Find all appointments linked to the user ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All inserted appointments")
    })
    @GetMapping
    public ResponseEntity<List<PatientEmployeeAppointmentResponseDto>> findAll(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
        return ResponseEntity.ok().body(appointmentService.findByPatientEmail(authentication.getName(), isAdmin));
    }

    // AUTHORIZATION: PATIENT
    @Operation(summary = "Book appointment", description = "Book appointment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booked appointment with success"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @PutMapping("/{appointmentId}/book")
    public ResponseEntity<Appointment> book(@PathVariable Long appointmentId, Authentication authentication) throws AppointmentConflictException {
        Appointment response = appointmentService.book(appointmentId, authentication);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // AUTHORIZATION: ADMIN or PATIENT
    @Operation(summary = "Cancel appointment", description = "Cancel appointment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Canceled appointment with success"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<Appointment> cancel(@PathVariable Long appointmentId, Authentication authentication) {
        return ResponseEntity.ok().body(appointmentService.cancel(appointmentId, authentication.getName()));
    }

    // AUTHORIZATION: EMPLOYEE (ONLY TECHNICAL)
    @Operation(summary = "Start exam capture process", description = "Validates the appointment and generates a secure upload URL for the X-Ray image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Process started successfully. Upload URL generated."),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "409", description = "Business rule violation (e.g., appointment not assigned to you, or status is not BOOKED)")
    })
    @PostMapping("/{appointmentId}/request-upload")
    @PreAuthorize("@securityUtils.isEmployeeTechnical(authentication)")
    public ResponseEntity<RequestUploadResponseDto> startExam(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) throws AppointmentConflictException {
        RequestUploadResponseDto uploadUrl = appointmentService.startExamCapture(appointmentId, authentication.getName());
        return ResponseEntity.ok(uploadUrl);
    }

    // AUTHORIZATION: ONLY ADMIN AND EMPLOYEES
    @Operation(
            summary = "List appointments for management for admin and employees",
            description = "Retrieves a paginated list of clinic appointments with optional filters by name and job position."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of appointments retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User lacks required ADMIN authority"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("management")
    public ResponseEntity<Page<AppointmentManagementAdminDto>> appointmentsManagementWithPagination(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String employeeName,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        return ResponseEntity.ok(this.appointmentService.findAllAppointmentsForManagementWithPagination(status, employeeName, page));
    }

    // AUTHORIZATION: ONLY EMPLOYEES
    @Operation(
            summary = "Retrieve employee dashboard metrics",
            description = """
                    Returns the dashboard metrics for employees including: 
                    Total appointments scheduled for the current day, 
                    Total completed appointments for the current day, 
                    Total pending appointments for the current day
                    """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee dashboard metrics retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Missing or invalid JWT token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User lacks required EMPLOYEE authority"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while retrieving dashboard metrics"
            )
    })
    @GetMapping("metrics")
    public ResponseEntity<DashboardMetricsForEmployeeResponseDto> getMetricsForEmployeees( Authentication authentication) {
        return ResponseEntity.ok(this.appointmentService.metricsForEmployeeDashboard(authentication));
    }
}
